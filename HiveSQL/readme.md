
1. Create tables from HDFS

```bash
> hive -f create_tables.sql
```

2. Add UDF to Hive (change path and names according to yours):

```bash
> hive -e "delete jar /home/cloudera/my/b-0.0.1.jar;
add jar /home/cloudera/my/b-0.0.1.jar;
create temporary function getStartIP as 'a.udf.GetStartIP';
create temporary function getEndIP as 'a.udf.GetEndIP';
create temporary function getIP as 'a.udf.GetIP';"
```

3. Start tasks:

```bash
> hive -f select_51_52.sql
```

```bash
> hive -f select_63.sql
```

