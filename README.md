# Resource Availability Status Api

Resource Availability Status API project, aka `RASA`, contains an abstract Java API (rasa-api) and currently one 
mysql implementation of the API (rasa-mysql-impl) that queries the link checking database of 
the [Curation Module](https://github.com/clarin-eric/clarin-curation-module)
and the [Linkchecker](https://github.com/clarin-eric/linkchecker). 
It takes care of the database specific code and provides convenience methods for developers who want to access 
meaningful information extracted from the results of Linkchecker. 

### Usage

Add rasa-api and a rasa implementation (f.e. rasa-mysql-impl) as a dependency to your pom.xml:

```
<dependency>
    <groupId>eu.clarin.cmdi</groupId>
    <artifactId>rasa-api</artifactId>
    <version>4.0.0</version>
</dependency>
<dependency>
    <groupId>eu.clarin.cmdi</groupId>
    <artifactId>rasa-mysql-impl</artifactId>
    <version>4.0.0</version>
</dependency>
```

Create Rasa Factory with database parameters (example for mysql-impl with [HikariCP](https://github.com/brettwooldridge/HikariCP)):

```
Properties properties = new Properties();
properties.setProperty("jdbcUrl","...");
properties.setProperty("username", "...");
properties.setProperty("password", "...");
...
RasaFactory factory = RasaFactoryBuilderImpl.getRasaFactory(properties);
```

Get the resources:

```
CheckedLinkResource checkedLinkResource = factory.getCheckedLinkResource();
LinkToBeCheckedResource linkToBeCheckedResource = factory.getLinkToBeCheckedResource();
```

**CheckedLink Examples:**

Get a CheckedLink object by its url:

```
CheckedLinkFilter filter = checkedLinkResource.getCheckedLinkFilter().filter.setUrlIs("...");
CheckedLink checkedLink = checkedLinkResource.get(filter).getFirst().get();
```

Get a stream of CheckedLink objects by their providerGroup "xyz" with status 200:

```
CheckedLinkFilter filter = checkedLinkResource.getCheckedLinkFilter().setProviderGroupIs("xyz").setStatusIs(200);
try(Stream<CheckedLink> links = checkedLinkResource.get(filter)) {
    //...
}
```

Count the number of checkedLinks for providerGroup xyz: 

```
CheckedLinkFilter filter = checkedLinkResource.getCheckedLinkFilter().setProviderGroupIs("xyz");
checkedLinkResource.count(filter);
}
```

**LinkToBeChecked Examples:**

Save a new link to be checked into the database:

```
LinkToBeChecked linkToBeChecked = new LinkToBeChecked(url, record, collection, expectedMimeType);
linkToBeCheckedResource.save(linkToBeChecked);
```

Count the number of URLs in the database

```
LinkToBeCheckedFilter filter = linkToBeCheckedResource.getLinkToBeCheckedFilter();
linkToBeCheckedResource.filter(linkToBeChecked);
```

Count the number of URLs for providerGroup "xyz"

```
LinkToBeCheckedFilter filter = linkToBeCheckedResource.getLinkToBeCheckedFilter().setProviderGroupIs("xyz");
linkToBeCheckedResource.filter(linkToBeChecked);
```

### Javadoc

`mvn javadoc:javadoc` will create javadoc files under `target/site/apidocs` From there if you open `index.html` you can browse the javadoc 

### Use Cases:

![RASA architecture diagram](RASA-architecture-diagram.png)

1. Curation Module inserts new links to be checked or activates/deactivates existing links via RASA. 
2. Linkchecker queries the database, checks the links and saves them back in the database via RASA.
3. VLO-importer queries the database for link status information

### Database Schema of the mysql implementation
There are three tables in the database that RASA interacts with: `url`, `status`, `history`, `url_context`, `context` and `providerGroup`. There are also some indexes to help with performance when querying the database. 
Definitive table schema can be found [here](https://github.com/clarin-eric/resource-availability-status-api/blob/master/rasa-mysql-impl/src/test/resources/createDB.sql).

`url:` This table is the definitive list of all urls. 
Curation Module fills this table up during the weekly update. 
A tuple in this table maps to LinkToBeChecked class in RASA.
    
    id INT: the unique record identifier
    url VARCHAR(1024): url of the link (primary key)


`status:` This is the table where the latest responses of the checks are persisted. 
Linkchecker reads urls from `url` table, checks them and saves the checking result into this table.
A tuple in this table maps to CheckedLink class in RASA.

    id INT: the unique record identifier
    url_id INT: record of the link (unique and foreign key to url table)
    statusCode INT: status code of the response
    method VARCHAR(128): method of the request (HEAD or GET)
    contentType VARCHAR(255): mime type of the response
    byteSize INT: size of the response in bytes
    duration INT: duration of the response in milliseconds
    checkingDate DATETIME: last time the link was checked
    redirectCount INT: amount of redirects in total for the link
    message VARCHAR(255): explanation of the response, ex: why a link was undetermined, or what exception was thrown during the request 
    category VARCHAR(50): category of the response, possible categories: Ok, Broken, Undetermined, Restricted_Access, Blocked_By_Robots_txt
    
`history:` This is the table where links are saved if they were checked more than one time. 
So if a link is already in the `status` table 
and is checked again, then the old status record is copied here while the status record is overridden.
Schema of this table is almost the same as 'status' table, but we save the id from status table as status_id (in fact a redundant information), drop 
unique key constraint for url_id and add a unique key constraint on url_id + checkingDate, since the same URL can't be checked more than once at 
the same time. 

`url_context:` As the name says this table links a URL to one or more context(s). 
   
    id INT: the unique record identifier
    url_id INT: the unique record identifier from url table
    context_id INT: the unique record identifier from context table
    ingestionDate DATETIME: last time the URL was ingested in the context
    active BOOLEAN: indicates whether the URL is still active in this context
    
`context:` This table saves the context(s) of each link to an URL

    id INT: the unique record identifier
    source VARCHAR(256): could be harvested data or some other data source
    record VARCHAR(256): for example a file name 
    providerGroup_id INT: the unique record identifier of the providerGroup table
    expectedMimeType VARCHAR(256): some links provide information on the expected mime type of the link resource
    
`providerGroup:` This table saves the names of the provider groups

    id INT: the unique record identifier
    name VARCHAR(256): the name of the provider group    
   
    