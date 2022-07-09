package basic_sample_pca;

import org.moeaframework.Instrumenter;
import org.moeaframework.analysis.collector.Accumulator;
import org.moeaframework.core.Algorithm;
import org.moeaframework.core.Initialization;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.NondominatedSortingPopulation;
import org.moeaframework.core.PRNG;
import org.moeaframework.core.Population;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variation;
import org.moeaframework.util.TypedProperties;

import basic_sample2.Correlation;

import org.moeaframework.core.operator.GAVariation;
import org.moeaframework.core.operator.RandomInitialization;
import org.moeaframework.core.operator.TournamentSelection;
import org.moeaframework.core.operator.real.PM;
import org.moeaframework.core.operator.real.SBX;
import org.moeaframework.core.comparator.ChainedComparator;
import org.moeaframework.core.comparator.CrowdingComparator;
import org.moeaframework.core.comparator.ParetoDominanceComparator;

import lab.moea.algorithm.CustomAlgorithmFactory;
import lab.moea.problem.CustomProblemFactory;
import lab.moea.util.SimpleLog;
import lab.moea.util.Util;
import lab.moea.util.io.SimplePIO;
import java.util.ArrayList;
import org.moeaframework.core.variable.EncodingUtils;
 
public class solveProblem {
	private static String MOP_PROPERTY_PATH = "mop.properties";
	private static String ALG_PROPERTY_PATH = "algorithm.properties";
	
	private Accumulator accumulator;
	
	public solveProblem() {
		accumulator = new Accumulator();
	}
	
	public void opt(String aName, String pName, int trial, TypedProperties prop) {
		/**
		 *  Set output path
		 */
        String outputPath = prop.getString("output_base_path", "./") + "/" + pName + "/" + aName + "/" + trial + "/";
        SimplePIO spio = new SimplePIO(outputPath);
        
        String property_path = prop.getString("PROPERTY_PATH", "");
        
		/**
		 * Set random seed
		 */
		long seed = trial * 10 + 1000;
 		PRNG.setSeed(seed);

// 		/**
// 		 * Open history file 
// 		 */
//     	String historyName = Util.makeFileName("history_pf", ".txt");     		
//     	spio.openHistoryFile(historyName, false);

     	/**
 		 * Open objectives of point file 
 		 */
     	String objectives_point = Util.makeFileName("objectives_point", ".csv");     		
     	spio.openHistoryFile(objectives_point, false);
    	
     	/**
     	 * Define optimization problem
     	 */
     	TypedProperties mop_prop = Util.loadParameters(property_path + "/" + MOP_PROPERTY_PATH);
		int nobj = mop_prop.getInt("number_of_objectives", 2);
		int nvar = mop_prop.getInt("number_of_variables", 10);
		int ncon = mop_prop.getInt("number_of_constraints", 0);
		Problem problem = CustomProblemFactory.getProblem(pName, nobj, nvar, ncon, mop_prop);

		/**
		 * Construct instrumenter
		 */
		int ifreq = prop.getInt("instrument_frequency", 2);
		Instrumenter instrumenter = new Instrumenter()
				.withProblem(pName+"_"+nobj)
				.withFrequency(ifreq)
        		.attachElapsedTimeCollector()
				.attachGenerationalDistanceCollector()
				.attachInvertedGenerationalDistanceCollector()
				.attachHypervolumeCollector();

//		NondominatedPopulation referenceSet = instrumenter.getReferenceSet();
		
     	TypedProperties alg_prop = Util.loadParameters(property_path + "/" + ALG_PROPERTY_PATH);
		int npop = alg_prop.getInt("populationSize", 12);
		int max_evaluation = alg_prop.getInt("maxEvaluations", 10);

		/**
		 * Create an initial random population.
		 */
		Initialization initialization = new RandomInitialization(
				problem,
				npop
				);
		
		/**
		 * Define the crossover and mutation operator.
		 */
		TournamentSelection selection = new TournamentSelection(2, 
				new ChainedComparator(
						new ParetoDominanceComparator(),
						new CrowdingComparator()
						)
				);

		/**
		 * Define variation.
		 * 
		 * disable swapping variables in SBX operator to remain consistent with
		 * Deb's implementation
		 */
		Variation variation = new GAVariation(
				new SBX(1.0, 25.0, false, false),
				new PM(1.0 / nvar, 30.0)
				);
		
		/**
		 * Construct the algorithm
		 * When use Properties class, setProperty function should be used. (Don't use set function) 
		 * Please see 
		 */
		Algorithm algorithm = CustomAlgorithmFactory.getAlgorithm(
				aName, problem, selection, variation, initialization, alg_prop);
        
		/**
		 * attach collectors to algorithm
		 */
		Algorithm ialgorithm = instrumenter.instrument(algorithm);
		
		/**
		 * Run the algorithm for the specified number of evaluation. 
		 */
		int ite = 0;
		int interval = prop.getInt("print_interval", 100);
		while (ialgorithm.getNumberOfEvaluations() < max_evaluation) {
			if (ite % interval == 0 ) SimpleLog.Info(aName + ", " + pName + " : trial = " + trial + ", gen = " + ite);
			ialgorithm.step();
			
			NondominatedPopulation result = algorithm.getResult();
    		System.out.println(result.data.size());
    		
            ArrayList<ArrayList<Double>> arrays = new ArrayList<ArrayList<Double>>();
        	int j=0;
            for(Solution rs:result) {
        		ArrayList<Double> array = new ArrayList<>();
	        	for(int i=0 ; i<rs.variables.length ; i++  ) {
//	        		System.out.println(rs.getVariable(i))
	        		double x = EncodingUtils.getReal(rs.getVariable(i));
	        		array.add(x);
	        	}
	        	if(arrays.size() == 0) {
	        		arrays.add(array);
	        		}
        		else {
        			arrays.add(j,array);
        		}
	        	j++;
//	        	System.out.println(arrays);
	        }
//	        	System.out.println(arrays.get(0));
//	        	System.out.println(arrays);
//			System.out.println(arrays);
			//spio.writeHistory_objectives(outputPath+"objectives_point.csv",ialgorithm.getNumberOfEvaluations(), ite,arrays );
            
            //PCAを行う
            spark_sample3 pca = new spark_sample3();
            pca.pca_library(arrays);
			ite++;
		}
		
		/**
		 * Get the Pareto approximate results
		 */
		NondominatedPopulation result = algorithm.getResult();
     	String paretoName = Util.makeFileName("final_pf",".txt");     		
     	spio.writeSolutions(paretoName, result);
		
		Accumulator accumulator = instrumenter.getLastAccumulator();
		        			
		/**
		 * Save the runtime dynamics to png and csv
		 */
     	String ImgName = Util.makeFileName("accum_img", ".png");     		
     	spio.writeAccumToImg(ImgName, accumulator);

     	String CSVName = Util.makeFileName("accum_img", ".csv");
     	spio.writeAccumToCSV(CSVName, accumulator);
	
		/**
		 *  Print the runtime dynamics
		 */
		Util.printRuntimeDynamics(accumulator, false);
		
		/**
		 * Close all files
		 */
		spio.closeAll();

		/**
		 * Plot final result
		 */
//		Util.Plot(result, aName, pName, true);

	}
	
}
