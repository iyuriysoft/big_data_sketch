
## Tasks

- 5.1 Select top 10 most frequently purchased categories (using only Product)
- 5.2 Select top 10 most frequently purchased product in each category (using only Product)
- 6.3 Select top 10 countries with the highest money spending (using Product, CountryName, CountryIP)


Tasks are implemented in three different approaches: RDD, Dataframe, SQL

As a result, the output data are in MySql


## There are three input files: 


- ### Table Product

create table product {<br>
  name string,<br>
  price float,<br>
  dt1 timestamp,<br>
  category string,<br>
  ip string<br>
)<br>

#### input3000.txt

product4, 502.5, 2010-10-17 19:57:12.336, category15, 39.251.196.245<br>
product14, 500.0, 2010-10-20 18:14:09.600, category6, 46.62.153.220<br>
...<br>


- ### Table CountryIP

create external table CountryIP (<br>
  network string,<br>
  geoname_id int,<br>
  registered_country_geoname_id int,<br>
  represented_country_geoname_id int,<br>
  is_anonymous_proxy boolean,<br>
  is_satellite_provider boolean<br>
)<br>

#### CountryIP.csv

1.0.0.0/24,2077456,2077456,,0,0<br>
1.0.1.0/24,1814991,1814991,,0,0<br>
...<br>


- ### Table CountryName

create table CountryName (<br>
  geoname_id int,<br>
  locale_code string,<br>
  continent_code string,<br>
  continent_name string,<br>
  country_iso_code string,<br>
  country_name string,<br>
  is_in_e boolean<br>
)<br>

#### CountryName.csv

49518,en,AF,Africa,RW,Rwanda,0<br>
51537,en,AF,Africa,SO,Somalia,0<br>
...<br>

