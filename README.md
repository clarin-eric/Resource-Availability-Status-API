# Resource Availability Status Api

Resource Availability Status Api is a Java API that queries the stormychecker database of the [Curation Module](https://github.com/clarin-eric/clarin-curation-module). It provides convenience mehtods for developers who want to access meaningful information extracted from the results of Stormychecker.

### Usage

Create Rasa Factory with database parameters
```
RasaFactory factory = new ACDHRasaFactory(DATABASE_URI, DATABASE_USERNAME, DATABASE_PASSWORD);
```
Get the resources
```
CheckedLinkResource checkedLinkResource = factory.getCheckedLinkResource();
LinkToBeCheckedResource linkToBeCheckedResource = factory.getLinkToBeCheckedResource();
StatisticsResource statisticsResource = factory.getStatisticsResource();
```

**CheckedLink Example:**
```
CheckedLink checkedLink = checkedLinkResource.get(url);
```
**Filter Example with CheckedLink:**
```
ACDHCheckedLinkFilter filter = new ACDHCheckedLinkFilter(collectionName, status);
try(Stream<CheckedLink> links = checkedLinkResource.get(Optional.of(filter))) {
    //...
}
```
**LinkToBeCheceked Example:**
```
LinkToBeChecked linkToBeChecked = new LinkToBeChecked(url, record, collection, expectedMimeType);
linkToBeCheckedResourece.save(linkToBeChecked);
```

**Statistics Example:**
```
Statistics statistics = statisticsResource.getOverallStatistics();
statistics.getCount(); //the number of all urls in the status table
statistics.getAvgRespTime(); //the average response time of all checked links in the status table
```
**Filter Example with Statistics:**
```
//count all urls which have the collection "Google" and broken true from the status table:
acdhStatisticsFilter = new ACDHStatisticsCountFilter("Google", null, true, false, Table.STATUS);
long count = statisticsResource.countTable(acdhStatisticsFilter);
```

### Javadoc
`mvn javadoc:javadoc` will create javadoc files under `target/site/apidocs` From there if you open `index.html` you can browse the javadoc 
