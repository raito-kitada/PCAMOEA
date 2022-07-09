package spark_sample;

import java.util.Arrays;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

public class spark_sample1 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
    try (JavaSparkContext sc = new JavaSparkContext("local", "Hello World")) {
        JavaRDD<String> rdd = sc.parallelize(Arrays.asList("Hello", "World", "!"));
        rdd.foreach(val -> System.out.print(val + " "));
    }
	}

}
