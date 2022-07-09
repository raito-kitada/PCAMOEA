package lab.moea.operator.real;

import java.util.ArrayList;
import java.util.List;

import org.moeaframework.core.Population;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variable;
import org.moeaframework.core.variable.RealVariable;

import scala.Tuple2;
import smile.math.MathEx;
import smile.math.matrix.Matrix;
import smile.projection.PCA;

public class PODOP {
	private static int nvar;
	private static Matrix W;
	private static Matrix WT;
	private static double[] pmu;
	private static int nelem;

	public PODOP() {}

	public static void initialize(int nvar) {
		PODOP.nvar = nvar;
		PODOP.nelem = nvar;
	}
	
	public static void update(Population population) {
//		System.out.print("update start...");
		double[][] X = new double[population.size()][nvar];

		{
			int i = 0;
			for (Solution solution : population) {
				for (int j=0; j<nvar; j++) {
					Variable variable = solution.getVariable(j);
					if (variable instanceof RealVariable) {
						RealVariable var = (RealVariable) variable;
						double v = var.getValue();
						double lb = var.getLowerBound();
						double ub = var.getUpperBound();
						
						double v2 = normalize(v, lb, ub);
						X[i][j] = v2;
					}	
				}
				i++;
			}
		}
		
		PCA pca = PCA.fit(X);
		
		double[] cvps = pca.getCumulativeVarianceProportion();
		
		double sum = 0.0;
		nelem = nvar;
		for (int i=0; i<nvar-1; i++) {
			if (cvps[i] >= 0.95) {
				nelem = i + 2;
				break;
			}
		}
		
		nelem =nvar; // must be modified [1,nvar]
		pca.setProjection(nelem);
		W = pca.getProjection();
		WT = W.transpose();
		pmu = W.mv(pca.getCenter());
		
		Solution sol = population.get(0);
		double[] values = new double[nvar];
		for (int i=0; i<nvar; i++) {
			Variable variable = sol.getVariable(i);
			if (variable instanceof RealVariable) {
				RealVariable var = (RealVariable) variable;
				double v = var.getValue();
				double lb = var.getLowerBound();
				double ub = var.getUpperBound();
				
				v = normalize(v, lb, ub);
				values[i] = v;
			}	
		}
		
		double[] p1 = W.mv(values);
		MathEx.sub(p1, pmu);
		double[] p2 = pca.project(values);

		MathEx.add(p1, pmu);
		double[] r1 = WT.mv(p1);
		
//		Matrix reversed = projected.mm(WT);
//		System.out.println(reversed.toString());

//		System.out.println("end");
	}

	public static int getNumElem() {
		return nelem;
	}
	
	public static double[] forwardProject(Solution solution) {
		double[] values = new double[nvar];
		for (int i=0; i<nvar; i++) {
			Variable variable = solution.getVariable(i);
			if (variable instanceof RealVariable) {
				RealVariable var = (RealVariable) variable;
				double v = var.getValue();
				double lb = var.getLowerBound();
				double ub = var.getUpperBound();
				
				v = normalize(v, lb, ub);
				values[i] = v;
			}	
		}
		
		double[] projected = W.mv(values);
		MathEx.sub(projected, pmu);
		return projected;
	}
	
	public static double[] reverseProject(double[] projected) {
		MathEx.add(projected, pmu);
		double[] reverse = WT.mv(projected);
		return reverse;
	}
	
	protected static double normalize(double v, double lb, double ub) {
		return (v - lb) / (ub - lb);
	}
	
	protected static double denormalize(double nv, double lb, double ub) {
		return nv * (ub - lb) + lb;
	}
	
	protected static double discretization(double v, int ndigit) {
		double s = Math.pow(10, ndigit);
		return Math.round(v * s) / s;
	}	
}
