package spark_sample;

import java.util.Arrays;
import java.util.List;
import java.lang.reflect.Field;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.mllib.linalg.Matrix;
import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.mllib.linalg.Vectors;
import org.apache.spark.mllib.linalg.distributed.RowMatrix;

public class spark_sample3_rait {

	public static void main(String[] args) {
		ignoreJava9Warning();
		
		List<Vector> data = Arrays.asList(
		        Vectors.sparse(5, new int[] {1, 3}, new double[] {1.0, 7.0}),//[1.0,0.0,0.0,0.0,7.0]
		        Vectors.dense(2.0, 0.0, 3.0, 4.0, 5.0),
		        Vectors.dense(4.0, 0.0, 0.0, 6.0, 7.0)
		);
		System.out.println(data.get(0));

		JavaSparkContext jsc = new JavaSparkContext("local", "");
		JavaRDD<Vector> rows = jsc.parallelize(data);

		// Create a RowMatrix from JavaRDD<Vector>.
		RowMatrix mat = new RowMatrix(rows.rdd());

		// Compute the top 4 principal components.
		// Principal components are stored in a local dense matrix.
		Matrix pc = mat.computePrincipalComponents(5);

		// Project the rows to the linear space spanned by the top 4 principal components.
		RowMatrix projected = mat.multiply(pc);
		System.out.println(pc);
		System.out.println("finish");
	}

	@SuppressWarnings("restriction")
	public static void ignoreJava9Warning() {
	  try {
	    Field theUnsafe = sun.misc.Unsafe.class.getDeclaredField("theUnsafe");
	    theUnsafe.setAccessible(true);
	    sun.misc.Unsafe u = (sun.misc.Unsafe) theUnsafe.get(null);
	    Class<?> cls = Class.forName("jdk.internal.module.IllegalAccessLogger");
	    Field logger = cls.getDeclaredField("logger");
	    u.putObjectVolatile(cls, u.staticFieldOffset(logger), null);
	  } catch (Exception e) {
	    // Java9以前では例外
	  }
	}	
	
}
