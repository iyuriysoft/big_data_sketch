package tryspark;

import org.apache.commons.collections4.IterableUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;

import scala.Tuple2;
import schema.CountryIP;
import schema.CountryName;
import schema.Product;

public class SparkRDD {
    private static final String DATA_PATH = "/Users/Shared/test/";
    private static final String PRODUCT_PATH = DATA_PATH + "input3000.txt";
    private static final String COUNTRYIP_PATH = DATA_PATH + "CountryIP.csv";
    private static final String COUNTRYNAME_PATH = DATA_PATH + "CountryName.csv";

    public static void main(String args[]) {

        Logger.getLogger("org").setLevel(Level.WARN);
        Logger.getLogger("akka").setLevel(Level.WARN);

        //
        // become a record in RDD
        //

        // Define Spark Configuration
        SparkConf conf = new SparkConf().setAppName("01-Getting-Started").setMaster("local[*]");

        // Create Spark Context with configuration
        JavaSparkContext sc = new JavaSparkContext(conf);

        //
        // load data
        //

        JavaRDD<Product> rddProduct = sc.textFile(PRODUCT_PATH).map(new Function<String, Product>() {
            private static final long serialVersionUID = -4562616263303571036L;

            public Product call(String line) throws Exception {
                return new Product(line.split(","));
            }
        });

        JavaRDD<CountryName> rddCountryName = sc.textFile(COUNTRYNAME_PATH).map(new Function<String, CountryName>() {
            private static final long serialVersionUID = -4562616263303571036L;

            public CountryName call(String line) throws Exception {
                return new CountryName(line.split(","));
            }
        });

        JavaRDD<CountryIP> rddCountryIP = sc.textFile(COUNTRYIP_PATH).map(new Function<String, CountryIP>() {
            private static final long serialVersionUID = -4562616263303571036L;

            public CountryIP call(String line) throws Exception {
                return new CountryIP(line.split(","));
            }
        });

        //
        //
        // 5.1
        //
        //

        JavaPairRDD<String, Iterable<Product>> rdd51 = rddProduct.groupBy(w -> w.getCategory().trim());

        // I approach
        //
        System.out.println();
        System.out.println("5.1 sortBy");
        {
            JavaRDD<Tuple2<String, Integer>> rdd51a = rdd51.mapValues(f -> {
                return IterableUtils.size(f);
            }).map(f -> f.swap()).sortBy(f -> f._1, false, 1).map(f -> f.swap());
            System.out.println();
            rdd51a.take(10).stream().forEach(a -> {
                System.out.println(a);
            });
        }

        // II approach
        //
        System.out.println();
        System.out.println("5.1 sortByKey");
        {
            JavaPairRDD<Integer, String> rdd51b = rdd51.mapValues(f -> {
                return IterableUtils.size(f);
            }).mapToPair(f -> new Tuple2<>(f._2, f._1)).sortByKey(false);
            System.out.println();
            rdd51b.take(10).forEach(a -> {
                System.out.println(a);
            });
        }

        //
        //
        // 5.2
        //
        //

        System.out.println();
        System.out.println("5.2 sortBy");
        JavaPairRDD<Tuple2<String, String>, Iterable<Product>> rdd52 = rddProduct.groupBy(w -> {
            return new Tuple2<>(w.getCategory().trim(), w.getName().trim());
        });
        {
            JavaRDD<Tuple2<Tuple2<String, String>, Integer>> rdd52a = rdd52.mapValues(f -> {
                return IterableUtils.size(f);
            }).map(f -> f.swap()).sortBy(f -> f._1, false, 1).map(f -> f.swap());
            System.out.println();
            rdd52a.take(10).stream().forEach(a -> {
                System.out.println(a);
            });
        }

        //
        //
        // 6.1
        //
        //
        // SELECT t.ip, sum(t.price) sump FROM product t GROUP BY t.ip ORDER BY sump
        // DESC LIMIT 10;
        JavaPairRDD<Long, Iterable<Product>> rdd61 = rddProduct.groupBy(w -> w.getIPAsLong());

        System.out.println();
        System.out.println("6.1 ");
        JavaPairRDD<Float, Long> rdd61a;
        {
            rdd61a = rdd61.mapValues(f -> {
                float c = 0;
                for (Product p : f) {
                    c = c + p.getPriceAsFloat();
                }
                return c;
            }).mapToPair(f -> new Tuple2<>(f._2, f._1)).sortByKey(false);

            // limit only 10 elements
            rdd61a = sc.parallelize(rdd61a.take(10)).mapToPair((x) -> new Tuple2<Float, Long>(x._1, x._2));
            System.out.println();
            rdd61a.collect().stream().forEach(a -> {
                System.out.println(a);
            });
        }

        System.out.println();
        System.out.println("6.1 join");
        {
            // join countryIP & countryName
            JavaPairRDD<Long, CountryIP> aIP = rddCountryIP.mapToPair(f -> new Tuple2<>(f.getGeonameId(), f));
            JavaPairRDD<Long, CountryName> aName = rddCountryName.mapToPair(f -> new Tuple2<>(f.getGeonameId(), f));
            JavaPairRDD<Long, Tuple2<CountryIP, CountryName>> aJoin = aIP.join(aName);
            aJoin.cache();
            // cross Countries with products
            JavaPairRDD<Tuple2<Float, Long>, Tuple2<Long, Tuple2<CountryIP, CountryName>>> rdd61b = rdd61a
                    .cartesian(aJoin)
                    .filter(f -> f._1._2 > f._2._2._1.getStartIPAsLong() && f._1._2 < f._2._2._1.getEndIPAsLong());
            rdd61b.cache();
            // sort
            JavaPairRDD<Float, Tuple2<Tuple2<Float, Long>, Tuple2<Long, Tuple2<CountryIP, CountryName>>>> rdd61c = rdd61b
                    .mapToPair(f -> new Tuple2<>(f._1._1, new Tuple2<>(f._1, f._2))).sortByKey(false);

            rdd61c.cache();
            System.out.println();
            System.out.println("sorted:");
            rdd61c.take(10).forEach(a -> {
                System.out.println(String.format("%.1f %d %s %s", a._1, a._2._2._2._2.getGeonameId(),
                        a._2._2._2._2.getCountryName(), a._2._2._2._1.getNetwork()));
            });
            // System.out.println();
            // System.out.println("unsorted:");
            // rdd61b.collect().forEach(a -> {
            // System.out.println(String.format("%.1f %s %s %d", a._1._1,
            // a._2._2._1.getNetwork(),
            // a._2._2._2.getCountryName(), a._2._2._2.getGeonameId()));
            // });

        }

        System.out.println("That's it");
        sc.close();
    }
}
