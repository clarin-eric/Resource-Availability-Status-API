# version 4.1.0
- adding source field to composed unique key in table record
- adding source field to LinkToBeChecked and to Filter

# version 4.0.1
- increasing field size of field byteSize in table status from int to bigint in mysql/maria db and from int to long in Java

# version 4.0.0
- separation of API and mysql implementation in sub-projects rasa-api and rasa-mysql-impl
- reduction and simplification of data access methods in resource classes
- dropping statistics resource classes and assigning their methods to the remaining resource classes
- major change of underlying database design of the mysql implementation towards a relational design (createDB.sql script in test/resources)
- factory design of database access
- making configuration of hikari connection in mysql implementation pool more flexible    