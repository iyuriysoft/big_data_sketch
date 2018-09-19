package tryspark;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SaveMode;
import org.apache.spark.sql.SparkSession;

import schema.CountryIP;
import schema.CountryName;
import schema.Product;

public class SparkDataframe {
    private static final String MYSQL_DB = "dbo2";
    //private static final String MYSQL_DRIVER = "com.mysql.jdbc.Driver";
    private static final String MYSQL_CONNECTION_URL = "jdbc:mysql://localhost/";
    private static final String MYSQL_USERNAME = "root";
    private static final String MYSQL_PWD = "password";

    private static final String DATA_PATH = "/Users/Shared/test/";
    private static final String PRODUCT_PATH = DATA_PATH + "input3000.txt";
    private static final String COUNTRYIP_PATH = DATA_PATH + "CountryIP.csv";
    private static final String COUNTRYNAME_PATH = DATA_PATH + "CountryName.csv";

    private static final String OUT_51_PATH = DATA_PATH + "df_51.csv";
    private static final String OUT_52_PATH = DATA_PATH + "df_52.csv";
    private static final String OUT_63_PATH = DATA_PATH + "df_63.csv";
    private static final String OUT_63IP_PATH = DATA_PATH + "df_63ip.csv";

    private static void prepareMySql(String dbname) throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.jdbc.Driver");
        System.out.println("Connecting to database...");
        Connection conn = DriverManager.getConnection(MYSQL_CONNECTION_URL, MYSQL_USERNAME, MYSQL_PWD);
        System.out.println("Creating database...");
        Statement stmt = conn.createStatement();
        stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + dbname);
        stmt.close();
        System.out.println("Database created successful");
    }

    public static void main(String args[]) {

        Logger.getLogger("org").setLevel(Level.WARN);
        Logger.getLogger("akka").setLevel(Level.WARN);

        Properties connectionProperties = new Properties();
        connectionProperties.put("user", MYSQL_USERNAME);
        connectionProperties.put("password", MYSQL_PWD);

        try {
            prepareMySql(MYSQL_DB);
        } catch (ClassNotFoundException | SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        //
        // become a record in RDD
        //

        // Define Spark Configuration
        SparkConf conf = new SparkConf().setAppName("Getting-Started").setMaster("local[2]");

        // Create Spark Context with configuration
        JavaSparkContext sc = new JavaSparkContext(conf);
        SparkSession spark = SparkSession.builder().config(conf).getOrCreate();

        // Create RDD
        JavaRDD<Product> rddP = sc.textFile(PRODUCT_PATH).map(new Function<String, Product>() {
            private static final long serialVersionUID = -4562616263303571036L;

            public Product call(String line) throws Exception {
                return new Product(line.split(","));
            }
        });
        // Create Dataframe
        Dataset<Row> df = spark.createDataFrame(rddP, Product.class);

        System.out.println("Table product");
        // Register the DataFrame as a temporary view
        df.createOrReplaceTempView("product");
        Dataset<Row> df2 = spark.sql("SELECT * FROM product LIMIT 3");
        df2.show(5, false);

        //
        // 5.1
        //
        System.out.println("Select top 10  most frequently purchased categories:");
        Dataset<Row> df_51 = spark
                .sql("SELECT category, COUNT(*) as cnt FROM product " + "GROUP BY category ORDER BY cnt DESC LIMIT 10");
        df_51.show();
        df_51.select("category", "cnt").write().mode(SaveMode.Overwrite).csv(OUT_51_PATH);
        df_51.write().mode(SaveMode.Overwrite).jdbc(MYSQL_CONNECTION_URL + MYSQL_DB, "table51", connectionProperties);

        //
        // 5.2
        //
        System.out.println("Select top 10 most frequently purchased product in each category:");
        Dataset<Row> df_52 = spark.sql("SELECT tp.name, tp.category, count(*) as cnt FROM product tp INNER JOIN "
                + "(select category, count(*) as c from product group by category order by c desc) tcat "
                + "ON tp.category = tcat.category " + "GROUP BY tp.name, tp.category ORDER BY cnt DESC LIMIT 10");
        df_52.show();
        df_52.select("name", "category", "cnt").write().mode(SaveMode.Overwrite).csv(OUT_52_PATH);
        df_52.write().mode(SaveMode.Overwrite).jdbc(MYSQL_CONNECTION_URL + MYSQL_DB, "table52", connectionProperties);

        //
        // 6.3 with ip
        //
        System.out.println("Select top 10 IP with the highest money spending:");
        Dataset<Row> df_63i = spark
                .sql("SELECT t.ip, sum(t.price) sump FROM product t GROUP BY t.ip ORDER BY sump DESC LIMIT 10");
        df_63i.show();
        df_63i.select("ip", "sump").write().mode(SaveMode.Overwrite).csv(OUT_63IP_PATH);
        df_63i.write().mode(SaveMode.Overwrite).jdbc(MYSQL_CONNECTION_URL + MYSQL_DB, "table63ip",
                connectionProperties);

        //
        // 6.3 with country name
        //
        System.out.println("Select top 10 countries with the highest money spending");
        JavaRDD<CountryIP> rddGeoIP = sc.textFile(COUNTRYIP_PATH).map(new Function<String, CountryIP>() {
            private static final long serialVersionUID = -4562616263303571036L;

            public CountryIP call(String line) throws Exception {
                String[] fields = line.split(",");
                return new CountryIP(fields);
            }
        });
        Dataset<Row> dfGeoIP = spark.createDataFrame(rddGeoIP, CountryIP.class);
        dfGeoIP.createOrReplaceTempView("countryip");

        JavaRDD<CountryName> rddGeoName = sc.textFile(COUNTRYNAME_PATH).map(new Function<String, CountryName>() {
            private static final long serialVersionUID = -4562616263303571036L;

            public CountryName call(String line) throws Exception {
                String[] fields = line.split(",");
                return new CountryName(fields);
            }
        });
        Dataset<Row> dfGeoName = spark.createDataFrame(rddGeoName, CountryName.class);
        dfGeoName.createOrReplaceTempView("countryname");

        Dataset<Row> df_63 = spark.sql("SELECT tp.sump, tp.IP, tcn.countryName FROM "
                + "(select IP, IPAsLong, sum(price) sump from product group by IP, IPAsLong order by sump desc limit 10) tp, "
                + "(select geonameId, StartIPAsLong, EndIPAsLong from countryip) tc "
                + "INNER JOIN countryname tcn ON tc.geonameId = tcn.geonameId "
                + "WHERE tp.IPAsLong <= tc.EndIPAsLong AND tp.IPAsLong >= tc.StartIPAsLong ORDER BY tp.sump DESC");
        df_63.show();
        df_63.select("sump", "IP", "countryName").write().mode(SaveMode.Overwrite).csv(OUT_63_PATH);
        df_63.write().mode(SaveMode.Overwrite).jdbc(MYSQL_CONNECTION_URL + MYSQL_DB, "table63", connectionProperties);

        sc.close();
    }
}
