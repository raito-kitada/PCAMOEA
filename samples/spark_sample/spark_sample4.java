package spark_sample;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.ml.linalg.Vector;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

public class spark_sample4 {

	public static void main(String[] args) {
        SparkSession spark = SparkSession.builder()
                .master("local")
                .appName("pcasample")
                .getOrCreate();

        Dataset<Row> df = spark.read()
                    .option("header", "false")
                    .option("delimiter",",")
                    .csv("./samples/spark_sample/pca_sample.csv");

        df.show();
        
        Dataset<Row> df2 = df.filter("_c0 > 0");        
        df2.show();
        
        Dataset<Row> df3 = df.select("_c0");
        df3.show();
	}

}
