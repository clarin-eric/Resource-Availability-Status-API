# version 5.1.x
- adding methods for deactivation and deletion of links which haven't been confirmed for a certain amount of time in days
- adding method, which takes an SQL String as parameter and returns a Stream of Map. This method is basically for testing 
- leaving choice of database driver and the use of a connection pool to the parent project

# version 5.0.x
- trimming URLs before any processing
- adding indexed groupKey field to table url (the criteria by which linkchecker is grouping URLs, f.e. to respect crawl delays)
- adding boolean valid field to table url
- checking new URLs for validity with status record in case of invalid URLs
- extracting host from URL and saving it in groupKey field for new URLs  
- adding a new method getLinksToChecked to API and implementation

# version 4.1.x
- adding source field to composed unique key in table record
- adding source field to LinkToBeChecked and to Filter
- performance tuning in mysql implementation of LinkToBeCheckedResourceImpl through review of synchronization in save methods
- replacement of mysql jdbc driver through mariadb driver

# version 4.0.x
- separation of API and mysql implementation in sub-projects rasa-api and rasa-mysql-impl
- reduction and simplification of data access methods in resource classes
- dropping statistics resource classes and assigning their methods to the remaining resource classes
- major change of underlying database design of the mysql implementation towards a relational design (createDB.sql script in test/resources)
- factory design of database access
- making configuration of hikari connection in mysql implementation pool more flexible    