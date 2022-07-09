package spark_sample;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

import org.apache.spark.api.java.function.Function;
import org.apache.spark.mllib.linalg.Matrix;
import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.mllib.linalg.Vectors;
import org.apache.spark.mllib.linalg.distributed.RowMatrix;

import scala.Tuple2;

public class spark_sample5 {

	public static void main(String[] args) {
		SparkConf conf = new SparkConf().setAppName("sample5").setMaster("local");
		JavaSparkContext sc = new JavaSparkContext(conf);
		
		JavaRDD<String> data = sc.textFile("samples/spark_sample/pca_sample.csv");
		JavaRDD<Vector> parsedData = data.map(new Function<String, Vector>() {
			public Vector call(String s) {
		          String[] sarray = s.split(",");
		          double[] values = new double[sarray.length];
		          for (int i = 0; i < sarray.length; i++)
		            values[i] = Double.parseDouble(sarray[i]);
		          return (Vector) Vectors.dense(values);
		        }			
		});
		
		// Create a RowMatrix from JavaRDD<Vector>.
		RowMatrix mat = new RowMatrix(parsedData.rdd());

		// Compute the top 2 principal components.
		// Principal components are stored in a local dense matrix.
//		Matrix pc = mat.computePrincipalComponents(2);
		Tuple2<Matrix, Vector> pc = mat.computePrincipalComponentsAndExplainedVariance(2);
		

		// Project the rows to the linear space spanned by the top 4 principal components.
		RowMatrix projected = mat.multiply(pc._1);
		
		System.out.println(pc._1.toString());
		System.out.println(pc._2.toString());
	}

}
