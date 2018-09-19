--
-- table Product
--
drop table if exists product;
create external table product (
  name string,
  price float,
  dt1 timestamp,
  category string,
  ip string
)
PARTITIONED BY (dt string) 
row format delimited fields terminated by ','
LOCATION '/user/cloudera/product/';

--ROW FORMAT SERDE 'org.apache.hadoop.hive.serde2.OpenCSVSerde'
--WITH SERDEPROPERTIES (
--  "separatorChar" = ",",
--  "quoteChar"     = "`",
--  "escapeChar"    = "\\"
--)

--load data local inpath '/home/cloudera/input.txt' overwrite into table product;

--create table product partitioned by (dt date);
ALTER TABLE product ADD PARTITION (dt = '2010-10-15');
ALTER TABLE product ADD PARTITION (dt = '2010-10-16');
ALTER TABLE product ADD PARTITION (dt = '2010-10-17');
ALTER TABLE product ADD PARTITION (dt = '2010-10-18');
ALTER TABLE product ADD PARTITION (dt = '2010-10-19');
ALTER TABLE product ADD PARTITION (dt = '2010-10-20');
ALTER TABLE product ADD PARTITION (dt = '2010-10-21');



--
-- table CountryName
--

--49518,en,AF,Africa,RW,Rwanda,0

drop table if exists CountryName;
create external table CountryName (
  geoname_id int,
  locale_code string,
  continent_code string,
  continent_name string,
  country_iso_code string,
  country_name string,
  is_in_e boolean
)
ROW FORMAT SERDE 'org.apache.hadoop.hive.serde2.OpenCSVSerde'
WITH SERDEPROPERTIES (
  "separatorChar" = ",",
  "quoteChar"     = "\"",
  "escapeChar"    = "\\"
)
LOCATION '/user/cloudera/CountryName/';


--
-- table CountryIP
--

--1.0.0.0/24,2077456,2077456,,0,0

drop table if exists CountryIP;
create external table CountryIP (
  network string,
  geoname_id int,
  registered_country_geoname_id int,
  represented_country_geoname_id int,
  is_anonymous_proxy boolean,
  is_satellite_provider boolean
)
ROW FORMAT SERDE 'org.apache.hadoop.hive.serde2.OpenCSVSerde'
WITH SERDEPROPERTIES (
  "separatorChar" = ",",
  "quoteChar"     = "\"",
  "escapeChar"    = "\\"
)
LOCATION '/user/cloudera/CountryIP/';


select * from product limit 10;
select * from CountryName limit 10;
select * from CountryIP limit 10;