
## There are three input datas: 


- table Product

create table product (\s
  name string,\s
  price float,\
  dt1 timestamp,\
  category string,\
  ip string\
)\

input3000.txt 
product4, 502.5, 2010-10-17 19:57:12.336, category15, 39.251.196.245
product14, 500.0, 2010-10-20 18:14:09.600, category6, 46.62.153.220
...


- table CountryIP

create external table CountryIP (
  network string,
  geoname_id int,
  registered_country_geoname_id int,
  represented_country_geoname_id int,
  is_anonymous_proxy boolean,
  is_satellite_provider boolean
)

CountryIP.csv
1.0.0.0/24,2077456,2077456,,0,0
1.0.1.0/24,1814991,1814991,,0,0
...


- table CountryName

create table CountryName ( . 
  geoname_id int,  
  locale_code string,  
  continent_code string,
  continent_name string,
  country_iso_code string,
  country_name string,
  is_in_e boolean
)

CountryName.csv
49518,en,AF,Africa,RW,Rwanda,0
51537,en,AF,Africa,SO,Somalia,0
...


## Tasks

- 5.1 Select top 10 most frequently purchased categories (using only Product)
- 5.2 Select top 10 most frequently purchased product in each category (using only Product)
- 6.3 Select top 10 countries with the highest money spending (using Product, CountryName, CountryIP)


Theare are three approaches to decide the tasks: RDD, Dataframe, SQL

