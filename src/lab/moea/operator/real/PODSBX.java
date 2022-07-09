/* Copyright 2009-2020 David Hadka
 *
 * This file is part of the MOEA Framework.
 *
 * The MOEA Framework is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 *
 * The MOEA Framework is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with the MOEA Framework.  If not, see <http://www.gnu.org/licenses/>.
 */
package lab.moea.operator.real;

import java.util.ArrayList;
import java.util.List;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.mllib.linalg.Vectors;
import org.apache.spark.mllib.linalg.distributed.RowMatrix;
import org.moeaframework.core.PRNG;
import org.moeaframework.core.Settings;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variable;
import org.moeaframework.core.Variation;
import org.moeaframework.core.variable.RealVariable;

/**
 * Simulated binary crossover (SBX) operator.  SBX attempts to simulate the 
 * offspring distribution of binary-encoded single-point crossover on 
 * real-valued decision variables.  An example of this distribution,
 * which favors offspring nearer to the two parents, is shown below.
 * <p>
 * <img src="doc-files/SBX-1.png" alt="Example SBX operator distribution" />
 * <p>
 * The distribution index controls the shape of the offspring distribution. 
 * Larger values for the distribution index generates offspring closer to the 
 * parents.
 * <p>
 * This operator is type-safe.
 * <p>
 * References:
 * <ol>
 *   <li>Deb, K. and Agrawal, R. B.  "Simulated Binary Crossover for Continuous
 *       Search Space."  Indian Institute of Technology, Kanpur, India.  
 *       Technical Report No. IITK/ME/SMD-94027, 1994.
 * </ol>
 */
public class PODSBX implements Variation {

	/**
	 * The probability of applying this SBX operator to each variable.
	 */
	private final double probability;

	/**
	 * The distribution index of this SBX operator.
	 */
	private final double distributionIndex;

	/**
	 * Enable randomly swapping decision variables between the parents.
	 */
	private final boolean swap;

	/**
	 * If {@code true}, use symmetric distributions; otherwise asymmetric
	 * distributions are used.
	 */
	private final boolean symmetric;
	
	/**
	 * Constructs a SBX operator with the specified probability and
	 * distribution index.
	 * 
	 * @param probability the probability of applying this SBX operator to each
	 *        variable
	 * @param distributionIndex the distribution index of this SBX operator
	 */
	public PODSBX(double probability, double distributionIndex) {
		this(probability, distributionIndex, true, false);
	}

	/**
	 * Constructs a SBX operator with the specified probability and
	 * distribution index.  Set {@code swap} to {@code true} to recreate the
	 * traditional SBX operation; and to {@code false} to use the SBX variant
	 * used by NSGA-III.
	 * 
	 * @param probability the probability of applying this SBX operator to each
	 *        variable
	 * @param distributionIndex the distribution index of this SBX operator
	 * @param swap if {@code true}, randomly swap the variables between the two
	 *        parents
	 * @param symmetric if {@code true}, symmetric distrubutions are used
	 */
	public PODSBX(double probability, double distributionIndex, boolean swap,
			boolean symmetric) {
		super();
		this.probability = probability;
		this.distributionIndex = distributionIndex;
		this.swap = swap;
		this.symmetric = symmetric;
	}

	/**
	 * Returns the probability of applying this SBX operator to each variable.
	 * 
	 * @return the probability of applying this SBX operator to each variable
	 */
	public double getProbability() {
		return probability;
	}

	/**
	 * Returns the distribution index of this SBX operator.
	 * 
	 * @return the distribution index of this SBX operator
	 */
	public double getDistributionIndex() {
		return distributionIndex;
	}

	/**
	 * Returns {@code true} if this SBX operator swaps variables between the
	 * two parents.  Disabling this swapping produces offspring closer to the
	 * two parents, which is beneficial for NSGA-III.
	 * 
	 * @return {@code true} if this SBX operator swaps variables between the
	 *         two parents
	 */
	public boolean isSwap() {
		return swap;
	}

	/**
	 * Returns {@code true} if the offspring are distributed symmetrically; or
	 * {@code false} if asymmetric distributions are used.
	 * 
	 * @return {@code true} if the offspring are distributed symmetrically; or
	 *         {@code false} if asymmetric distributions are used
	 */
	public boolean isSymmetric() {
		return symmetric;
	}

	@Override
	public int getArity() {
		return 2;
	}

	@Override
	public Solution[] evolve(Solution[] parents) {
//		System.out.print(".");
		Solution result1 = parents[0].copy();
		Solution result2 = parents[1].copy();
	
		double lowerbound = -3.0;
		double upperbound = 3.0;

		int nelem = PODOP.getNumElem();
		
		double[] svec1 = forwardProject(result1);
		double[] svec2 = forwardProject(result2);
		
		
		double[] ret1 = new double[nelem];
		double[] ret2 = new double[nelem];

		if (PRNG.nextDouble() <= probability) {
			for (int i = 0; i < nelem; i++) {
				double variable1[] = {svec1[i]};
				double variable2[] = {svec2[i]};

				if (PRNG.nextBoolean()) {
					if (symmetric) {
						evolve_symmetric(variable1,variable2,
								lowerbound, upperbound,
								distributionIndex,
								swap);
					} else {
						evolve_asymmetric(variable1, variable2,
								lowerbound, upperbound,									
								distributionIndex,
								swap);
					}
				}
				
				ret1[i] = variable1[0];
				ret2[i] = variable2[0];
			}
		}
		
		
		double[] rvec1 = reverseProject(ret1);
		double[] rvec2 = reverseProject(ret2);
		
		checkBound(parents[0], parents[1], rvec1, 0.0, 1.0);		
		checkBound(parents[0], parents[1], rvec2, 0.0, 1.0);
		
		int ncol = result1.getNumberOfVariables();
		for (int i=0; i<ncol; i++) {
			result1.setVariable(i, new RealVariable(rvec1[i], 0.0, 1.0));
			result2.setVariable(i, new RealVariable(rvec2[i], 0.0, 1.0));		
		}
		
//		if (PRNG.nextDouble() <= probability) {
//			for (int i = 0; i < result1.getNumberOfVariables(); i++) {
//				Variable variable1 = result1.getVariable(i);
//				Variable variable2 = result2.getVariable(i);
//
//				if (PRNG.nextBoolean() && (variable1 instanceof RealVariable)
//						&& (variable2 instanceof RealVariable)) {
//					if (symmetric) {
//						evolve_symmetric((RealVariable)variable1,
//								(RealVariable)variable2, distributionIndex,
//								swap);
//					} else {
//						evolve_asymmetric((RealVariable)variable1,
//								(RealVariable)variable2, distributionIndex,
//								swap);
//					}
//				}
//			}
//		}			
		
		return new Solution[] { result1, result2 };
	}
	
	protected double[] forwardProject(Solution solution) {
		return PODOP.forwardProject(solution);
	}
	
	protected double[] reverseProject(double[] values) {
		return PODOP.reverseProject(values);
	}

	protected boolean checkBound(Solution p1, Solution p2, double[] rvec, double lowerbound, double upperbound) {
		int n = rvec.length;
		boolean flag = true;
		for (int i=0; i<n; i++) {
			if (rvec[i] < lowerbound) {
				rvec[i] = PRNG.nextBoolean() ? ((RealVariable)p1.getVariable(i)).getValue(): 
											   ((RealVariable)p2.getVariable(i)).getValue();
				flag = false; // fail
			}
			if (rvec[i] > upperbound) {
				rvec[i] = PRNG.nextBoolean() ? ((RealVariable)p1.getVariable(i)).getValue(): 
					   						   ((RealVariable)p2.getVariable(i)).getValue();
				flag = false; // fail
			}
		}
		return flag; // success
	}
	
	/**
	 * Evolves the specified variables using the SBX operator.
	 * 
	 * @param v1 the first variable
	 * @param v2 the second variable
	 * @param distributionIndex the distribution index of this SBX operator
	 */
	public static void evolve(RealVariable v1, RealVariable v2,
			double distributionIndex) {
		double dv1[] = {v1.getValue()};
		double dv2[] = {v2.getValue()};
		evolve_asymmetric(dv1, dv2, v1.getLowerBound(), v2.getUpperBound(), distributionIndex, true);
		v1.setValue(dv1[0]);
		v2.setValue(dv2[0]);
	}

	/*
	 * The following code was provided by Haitham Seada on Dec 14, 2015.  This
	 * replaces the old implementation based on PISA, which appears to have
	 * some numerical issues, particularly on problems like DTLZ3.
	 */

	/**
	 * Evolves the specified variables using the SBX operator using symmetric
	 * distributions.
	 * 
	 * @param v1 the first variable
	 * @param v2 the second variable
	 * @param distributionIndex the distribution index of this SBX operator
	 * @param swap randomly swap the variable between the two parents
	 */
	public static void evolve_symmetric(double[] v1, double[] v2, double lowerbound, double upperbound, 
			double distributionIndex, boolean swap) {
		double y1, y2, betaq, beta, alpha, rand;
		double x1 = v1[0];
		double x2 = v2[0];
		double lb = lowerbound;
		double ub = upperbound;

		// avoid division by zero
		if (Math.abs(x1 - x2) > Settings.EPS) {
			if (x2 > x1) {
				y2 = x2;
				y1 = x1;
			} else {
				y2 = x1;
				y1 = x2;
			}

			// compute beta
			if ((y1 - lb) > (ub - y2)) {
				beta = 1 + (2 * (ub - y2) / (y2 - y1));
			} else {
				beta = 1 + (2 * (y1 - lb) / (y2 - y1));
			}

			// compute alpha
			beta = 1.0 / beta;
			alpha = 2.0 - Math.pow(beta, distributionIndex + 1.0);
			rand = PRNG.nextDouble();

			// compute betaq
			if (rand <= 1.0 / alpha) {
				alpha = alpha * rand;
				betaq = Math.pow(alpha, 1.0 / (distributionIndex + 1.0));
			} else {
				alpha = alpha * rand;
				alpha = 1.0 / (2.0 - alpha);
				betaq = Math.pow(alpha, 1.0 / (distributionIndex + 1.0));
			}

			// generate two children
			x1 = 0.5 * ((y1 + y2) - betaq * (y2 - y1));
			x2 = 0.5 * ((y1 + y2) + betaq * (y2 - y1));
			
			// ensure the children are within bounds
			if (x1 < lb) {
				x1 = lb;
			} else if (x1 > ub) {
				x1 = ub;
			}
			
			if (x2 < lb) {
				x2 = lb;
			} else if (x2 > ub) {
				x2 = ub;
			}
			
			// randomly swap the variables
			if (swap && PRNG.nextBoolean()) {
				double temp = x1;
				x1 = x2;
				x2 = temp;
			}
			
			v1[0] = x1;
			v2[0] = x2;
		}
	}
	
	/**
	 * Evolves the specified variables using the SBX operator using asymmetric
	 * distributions.
	 * 
	 * @param v1 the first variable
	 * @param v2 the second variable
	 * @param distributionIndex the distribution index of this SBX operator
	 * @param swap randomly swap the variable between the two parents
	 */
	public static void evolve_asymmetric(double[] v1, double[] v2, double lowerbound, double upperbound, 
			double distributionIndex, boolean swap) {
		double y1, y2, betaq, beta, alpha, rand;
		double x1 = v1[0];
		double x2 = v2[0];
		double lb = lowerbound;
		double ub = upperbound;

		// avoid division by zero
		if (Math.abs(x1 - x2) > Settings.EPS) {
			if (x2 > x1) {
				y2 = x2;
				y1 = x1;
			} else {
				y2 = x1;
				y1 = x2;
			}

			// generate first offspring
			beta = 1.0 / (1.0 + (2.0 * (y1 - lb) / (y2 - y1)));
			alpha = 2.0 - Math.pow(beta, distributionIndex + 1.0);
			rand = PRNG.nextDouble();

			if (rand <= 1.0 / alpha) {
				alpha = alpha * rand;
				betaq = Math.pow(alpha, 1.0 / (distributionIndex + 1.0));
			} else {
				alpha = alpha * rand;
				alpha = 1.0 / (2.0 - alpha);
				betaq = Math.pow(alpha, 1.0 / (distributionIndex + 1.0));
			}
			
			x1 = 0.5 * ((y1 + y2) - betaq * (y2 - y1));
			
			// generate second offspring
			beta = 1.0 / (1.0 + (2.0 * (ub - y2) / (y2 - y1)));
			alpha = 2.0 - Math.pow(beta, distributionIndex + 1.0);
			
			if (rand <= 1.0 / alpha) {
				alpha = alpha * rand;
				betaq = Math.pow(alpha, 1.0 / (distributionIndex + 1.0));
			} else {
				alpha = alpha * rand;
				alpha = 1.0 / (2.0 - alpha);
				betaq = Math.pow(alpha, 1.0 / (distributionIndex + 1.0));
			}
			
			x2 = 0.5 * ((y1 + y2) + betaq * (y2 - y1));
			
			// ensure the children are within bounds
			if (x1 < lb) {
				x1 = lb;
			} else if (x1 > ub) {
				x1 = ub;
			}
			
			if (x2 < lb) {
				x2 = lb;
			} else if (x2 > ub) {
				x2 = ub;
			}
			
			// randomly swap the variables
			if (swap && PRNG.nextBoolean()) {
				double temp = x1;
				x1 = x2;
				x2 = temp;
			}
			
			v1[0] = x1;
			v2[0] = x2;
		}
	}

	/**
	 * Evolves the specified variables using the SBX operator using symmetric
	 * distributions.
	 * 
	 * @param v1 the first variable
	 * @param v2 the second variable
	 * @param distributionIndex the distribution index of this SBX operator
	 * @param swap randomly swap the variable between the two parents
	 */
	public static void evolve_symmetric(RealVariable v1, RealVariable v2,
			double distributionIndex, boolean swap) {
		double y1, y2, betaq, beta, alpha, rand;
		double x1 = v1.getValue();
		double x2 = v2.getValue();
		double lb = v1.getLowerBound();
		double ub = v1.getUpperBound();

		// avoid division by zero
		if (Math.abs(x1 - x2) > Settings.EPS) {
			if (x2 > x1) {
				y2 = x2;
				y1 = x1;
			} else {
				y2 = x1;
				y1 = x2;
			}

			// compute beta
			if ((y1 - lb) > (ub - y2)) {
				beta = 1 + (2 * (ub - y2) / (y2 - y1));
			} else {
				beta = 1 + (2 * (y1 - lb) / (y2 - y1));
			}

			// compute alpha
			beta = 1.0 / beta;
			alpha = 2.0 - Math.pow(beta, distributionIndex + 1.0);
			rand = PRNG.nextDouble();

			// compute betaq
			if (rand <= 1.0 / alpha) {
				alpha = alpha * rand;
				betaq = Math.pow(alpha, 1.0 / (distributionIndex + 1.0));
			} else {
				alpha = alpha * rand;
				alpha = 1.0 / (2.0 - alpha);
				betaq = Math.pow(alpha, 1.0 / (distributionIndex + 1.0));
			}

			// generate two children
			x1 = 0.5 * ((y1 + y2) - betaq * (y2 - y1));
			x2 = 0.5 * ((y1 + y2) + betaq * (y2 - y1));
			
			// ensure the children are within bounds
			if (x1 < lb) {
				x1 = lb;
			} else if (x1 > ub) {
				x1 = ub;
			}
			
			if (x2 < lb) {
				x2 = lb;
			} else if (x2 > ub) {
				x2 = ub;
			}
			
			// randomly swap the variables
			if (swap && PRNG.nextBoolean()) {
				double temp = x1;
				x1 = x2;
				x2 = temp;
			}
			
			v1.setValue(x1);
			v2.setValue(x2);
		}
	}
	
	/**
	 * Evolves the specified variables using the SBX operator using asymmetric
	 * distributions.
	 * 
	 * @param v1 the first variable
	 * @param v2 the second variable
	 * @param distributionIndex the distribution index of this SBX operator
	 * @param swap randomly swap the variable between the two parents
	 */
	public static void evolve_asymmetric(RealVariable v1, RealVariable v2,
			double distributionIndex, boolean swap) {
		double y1, y2, betaq, beta, alpha, rand;
		double x1 = v1.getValue();
		double x2 = v2.getValue();
		double lb = v1.getLowerBound();
		double ub = v1.getUpperBound();

		// avoid division by zero
		if (Math.abs(x1 - x2) > Settings.EPS) {
			if (x2 > x1) {
				y2 = x2;
				y1 = x1;
			} else {
				y2 = x1;
				y1 = x2;
			}

			// generate first offspring
			beta = 1.0 / (1.0 + (2.0 * (y1 - lb) / (y2 - y1)));
			alpha = 2.0 - Math.pow(beta, distributionIndex + 1.0);
			rand = PRNG.nextDouble();

			if (rand <= 1.0 / alpha) {
				alpha = alpha * rand;
				betaq = Math.pow(alpha, 1.0 / (distributionIndex + 1.0));
			} else {
				alpha = alpha * rand;
				alpha = 1.0 / (2.0 - alpha);
				betaq = Math.pow(alpha, 1.0 / (distributionIndex + 1.0));
			}
			
			x1 = 0.5 * ((y1 + y2) - betaq * (y2 - y1));
			
			// generate second offspring
			beta = 1.0 / (1.0 + (2.0 * (ub - y2) / (y2 - y1)));
			alpha = 2.0 - Math.pow(beta, distributionIndex + 1.0);
			
			if (rand <= 1.0 / alpha) {
				alpha = alpha * rand;
				betaq = Math.pow(alpha, 1.0 / (distributionIndex + 1.0));
			} else {
				alpha = alpha * rand;
				alpha = 1.0 / (2.0 - alpha);
				betaq = Math.pow(alpha, 1.0 / (distributionIndex + 1.0));
			}
			
			x2 = 0.5 * ((y1 + y2) + betaq * (y2 - y1));
			
			// ensure the children are within bounds
			if (x1 < lb) {
				x1 = lb;
			} else if (x1 > ub) {
				x1 = ub;
			}
			
			if (x2 < lb) {
				x2 = lb;
			} else if (x2 > ub) {
				x2 = ub;
			}
			
			// randomly swap the variables
			if (swap && PRNG.nextBoolean()) {
				double temp = x1;
				x1 = x2;
				x2 = temp;
			}
			
			v1.setValue(x1);
			v2.setValue(x2);
		}
	}	
	
}
