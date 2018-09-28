# test_big_data_project


# 1. Implement Random Events producer

Use Java, Scala or Python to implement event producer. Each event message describes single product purchase.
Producer should connect to Flume socket (see below) and send events in a CSV format, one event per line.

Generate CSV-file with random product purchase events (a.testdata.CreateTestData)

"generate_data.jar lines_count products_count categories_count"

example:
```bash
> generate_data.jar 3000 10 10
```

As a result, file "input.txt" will be with data like these:
```
product1, 508.7, 2010-10-15 04:52:40.388, category2, 197.240.70.89
product0, 501.9, 2010-10-20 11:24:41.728, category1, 67.166.220.64
product2, 500.1, 2010-10-21 00:13:49.728, category1, 22.180.248.21
...
...
```


## 2. Flume should put events (Product) to HDFS directory events/${year}/${month}/${day}

Try to put more than 3000 events to HDFS in several batches

### To do this to create Custom Source for csv-files

Parameters:

myfile - path for input file (type: string; default: 'input.txt')<br>
mypause - forced delay bitween events (type: int; default: 0)<br>
mydateindex - index with date data for 'timestamp' if present otherwise current time (type: int; default: -1)<br>

Start Flume like this:

```bash
> bin/flume-ng agent -n a1 -c conf -f conf/flume_product.template -Dflume.root.logger=TRACE,console
> bin/flume-ng agent -n a1 -c conf -f conf/flume_countryIP.template -Dflume.root.logger=TRACE,console
> bin/flume-ng agent -n a1 -c conf -f conf/flume_countryName.template -Dflume.root.logger=TRACE,console
```

### Pulls data from local file to HDFS

Geo data from http://dev.maxmind.com/geoip/geoip2/geolite2/

'GeoLite2-Country-Blocks-IPv4.csv' is renamed to 'CountryIP.csv'<br>
'GeoLite2-Country-Locations-en.csv' is renamed to 'CountryName.csv'<br>

Start Flume like this:

```bash
> bin/flume-ng agent -n a1 -c conf -f conf/flume_countryIP.template -Dflume.root.logger=TRACE,console
> bin/flume-ng agent -n a1 -c conf -f conf/flume_countryName.template -Dflume.root.logger=TRACE,console
```

In other words, we do trasformation:

<p>
Input Data:

/home/cloudera/my/input3000.txt<br>
/home/cloudera/my/CountryIP.csv<br>
/home/cloudera/my/CountryName.csv<br>

<p>
Output Data (HDFS):

/user/cloudera/product/<br>
/user/cloudera/CountryIP<br>
/user/cloudera/CountryName<br>



## 3. Create external Hive table to process data

External table should be partitioned by one field - purchase date. All partitions should be created manually right after you apply Hive scheme.

E.g. HDFS location = events/2016/02/14 , Hive partition = 2016-02-14

Tasks:
<p>
- (5.1) Select top 10  most frequently purchased categories
<p>
- (5.2) Select top 10 most frequently purchased product in each category
<p>
- (6.3) Select top 10 countries with the highest money spending
  Note: Hive UDF might help you to join events with IP geo data

<p>
* Create tables from HDFS

```bash
> hive -f create_tables.sql
```
<p>
* Add UDF to Hive (change path and names according to yours):

```bash
> hive -e "delete jar /home/cloudera/my/b-0.0.1.jar;
add jar /home/cloudera/my/b-0.0.1.jar;
create temporary function getStartIP as 'a.udf.GetStartIP';
create temporary function getEndIP as 'a.udf.GetEndIP';
create temporary function getIP as 'a.udf.GetIP';"
```
<p>
* Start tasks:

```bash
> hive -f select_51_52.sql
```

```bash
> hive -f select_63.sql
```



## 4. Spark Core

Tasks are implemented using Spark in three different approaches: RDD, Dataframe, SQL

Producer -> Flume -> HDFS -> Spark -> RDBMS
