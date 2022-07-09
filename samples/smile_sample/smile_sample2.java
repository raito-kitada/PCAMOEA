package smile_sample;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.stream.IntStream;

import smile.data.DataFrame;
import smile.io.CSV;
import smile.math.matrix.Matrix;
import smile.projection.PCA;

public class smile_sample2 {

	public static void main(String[] args) throws IOException, URISyntaxException {
		CSV csv = new CSV();
		DataFrame df = csv.read("samples/smile_sample/iris_scaled.csv");
		Matrix dat = df.toMatrix();
		
		System.out.println("---------------------------------------------------");
		System.out.println("Data Size");
		System.out.println("---------------------------------------------------");
		System.out.println(dat.nrows()+","+dat.ncols());
		System.out.println("");
		
		PCA pca = PCA.fit(dat.toArray());

		System.out.println("---------------------------------------------------");
		System.out.println("Variance");
		System.out.println("---------------------------------------------------");
		double[] vs = pca.getVariance();
		for(double v : vs) {
			System.out.print(v+" ");
		}
		System.out.println("");
		
		System.out.println("---------------------------------------------------");
		System.out.println("Loadings");
		System.out.println("---------------------------------------------------");
		System.out.println(pca.getLoadings().toString());
//		for (int i=0; i<mat.nrows(); i++) {
//			for (int j=0; j<mat.ncols(); j++) {
//				System.out.print(mat.get(i, j)+" ");
//			}
//			System.out.println("");
//		}
//		System.out.println("");
		
		System.out.println("---------------------------------------------------");
		System.out.println("Projection");
		System.out.println("---------------------------------------------------");
		pca.setProjection(2);
		Matrix W = pca.getProjection();
		Matrix WT = W.transpose();
		System.out.println(W.toString());
//		for (int i=0; i<projection.nrows(); i++) {
//			for (int j=0; j<projection.ncols(); j++) {
//				System.out.print(projection.get(i, j) + " ");
//			}
//			System.out.println("");
//		}		
//		System.out.println("");		
		
		System.out.println("---------------------------------------------------");
		System.out.println("CumulativeVarianceProportion");
		System.out.println("---------------------------------------------------");
		double[] cvps = pca.getCumulativeVarianceProportion();
		for(double v : cvps) {
			System.out.print(v + " ");
		}
		System.out.println("");
		System.out.println("");
		
		System.out.println("---------------------------------------------------");
		System.out.println("Original Data");
		System.out.println("---------------------------------------------------");
		System.out.println(dat.toString());
//		int nrow = 10; //odat.nrows()
//		for (int i=0; i<nrow; i++) {
//			for (int j=0; j<odat.ncols(); j++) {
//				System.out.print(odat.get(i, j)+" ");
//			}
//			System.out.println("");
//		}
//		System.out.println("");
		
		System.out.println("---------------------------------------------------");
		System.out.println("Projected Data");
		System.out.println("---------------------------------------------------");
		Matrix projected = new Matrix(pca.project(dat.toArray()));	
		System.out.println(projected.toString());
//		for (int i=0; i<nrow; i++) {
//			for (int j=0; j<projected.ncols(); j++) {
//				System.out.print(projected.get(i, j) + " ");
//			}
//			System.out.println("");
//		}		
//		System.out.println("");
		
		
		System.out.println("---------------------------------------------------");
		System.out.println("Projected Data (manual)");
		System.out.println("---------------------------------------------------");
		Matrix projected2 = W.mm(dat.transpose()).transpose();
		System.out.println(projected2.toString());
		
		System.out.println("---------------------------------------------------");
		System.out.println("Reverced Data");
		System.out.println("---------------------------------------------------");
		Matrix reversed = WT.mm(projected.transpose());		
		System.out.println(reversed.toString());
//		for (int i=0; i<nrow; i++) {
//			for (int j=0; j<reversed.ncols(); j++) {
//				System.out.print(reversed.get(i, j) + " ");
//			}
//			System.out.println("");
//		}		
//		System.out.println("");		
				
		System.out.println("---------------------------------------------------");
		System.out.println("Matrix slicing");
		System.out.println("---------------------------------------------------");
		Matrix smat = W.col(new int[]{0});
		Matrix smatT = smat.transpose(); 
		System.out.println(smat.nrows() + " " + smat.ncols());
		System.out.println(smatT.nrows() + " " + smatT.ncols());
		
		System.out.println("---");
		
		Matrix smat2 = W.col(new int[]{0,1});
		Matrix smat2T = smat2.transpose(); 
		System.out.println(smat2.nrows() + " " + smat2.ncols());
		System.out.println(smat2T.nrows() + " " + smat2T.ncols());
		
		System.out.println("---");
		
		Matrix smat3 = W.col(IntStream.range(0, 2).toArray());
		Matrix smat3T = smat3.transpose(); 
		System.out.println(smat3.nrows() + " " + smat3.ncols());
		System.out.println(smat3T.nrows() + " " + smat3T.ncols());
		
		System.out.println("---------------------------------------------------");
	}

}
