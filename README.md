# flink-connector-clickhouse
The clickhouse connector allows for reading data from and writing data into any relational databases with a clickhouse driver.

## Options

```shell
mvn package
cp clickhouse-jdbc-0.2.6.jar /FLINK_HOME/lib
cp flink-connector-jdbc_2.11-1.12.0.jar /FLINK_HOME/lib
cp guava-19.0.jar /FLINK_HOME/lib
``` 

## How to create a Clickhouse table

```sql
-- register a Clickhouse table 'users' in Flink SQL
DROP TABLE IF EXISTS MyUserTable;
CREATE TABLE MyUserTable (
    id String,
    name String,
    age String,
    create_date Date
) WITH (
    'connector' = 'clickhouse',
    'url' = 'jdbc:clickhouse://127.0.0.1:8123/temp',
    'table-name' = 'users',
    'username' = 'default',
    'password' = '',
    'format' = 'json'
);


-- write data into the Clickhouse table from the other table "T"
INSERT INTO MyUserTable
SELECT id, name, age FROM T;

-- scan data from the Clickhouse table
SELECT id, name, age FROM MyUserTable;

-- temporal join the Clickhouse table as a dimension table
SELECT * FROM myTopic
LEFT JOIN MyUserTable FOR SYSTEM_TIME AS OF myTopic.proctime
ON myTopic.key = MyUserTable.id;
```
