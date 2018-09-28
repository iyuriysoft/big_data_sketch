
## Tasks

- 5.1 Select top 10 most frequently purchased categories (using only Product)
- 5.2 Select top 10 most frequently purchased product in each category (using only Product)
- 6.3 Select top 10 countries with the highest money spending (using Product, CountryName, CountryIP)


Tasks are implemented in three different approaches: **RDD, Dataframe, SQL**

As a result, the output data are in MySql


### There are three input files: 

- Product (input3000.txt)
```
product4, 502.5, 2010-10-17 19:57:12.336, category15, 39.251.196.245
product14, 500.0, 2010-10-20 18:14:09.600, category6, 46.62.153.220
...
...
```

- CountryIP (CountryIP.csv)
```
1.0.0.0/24,2077456,2077456,,0,0
1.0.1.0/24,1814991,1814991,,0,0
...
...
```

- CountryName (CountryName.csv)
```
49518,en,AF,Africa,RW,Rwanda,0
51537,en,AF,Africa,SO,Somalia,0
...
...
```

### Output tables in MySql like this:

```sql
CREATE TABLE `table51` (
  `category` text,
  `cnt` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `table52` (
  `name` text,
  `category` text,
  `cnt` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `table63` (
  `sump` double DEFAULT NULL,
  `IP` text,
  `countryName` text
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `table63ip` (
  `ip` text,
  `sump` double DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

```

