package aos_sample;

import java.util.ArrayList;
import java.util.Properties;

import org.moeaframework.Instrumenter;
import org.moeaframework.algorithm.AbstractEvolutionaryAlgorithm;
import org.moeaframework.analysis.collector.Accumulator;
import org.moeaframework.core.Algorithm;
import org.moeaframework.core.Initialization;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.PRNG;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Variation;
import org.moeaframework.core.comparator.ChainedComparator;
import org.moeaframework.core.comparator.CrowdingComparator;
import org.moeaframework.core.comparator.ParetoDominanceComparator;
import org.moeaframework.core.operator.RandomInitialization;
import org.moeaframework.core.operator.TournamentSelection;
import org.moeaframework.core.spi.OperatorFactory;
import org.moeaframework.util.TypedProperties;

import aos.aos.AOSMOEA;
import aos.aos.AOSStrategy;
import aos.creditassigment.ICreditAssignment;
import aos.creditassignment.offspringparent.ParentDomination;
import aos.creditassignment.offspringparent.ParentIndicator;
import aos.creditassignment.setcontribution.ParetoFrontContribution;
import aos.creditassignment.setimprovement.OffspringParetoFrontDominance;
import aos.nextoperator.IOperatorSelector;
import aos.operator.AOSVariation;
import aos.operatorselectors.ProbabilityMatching;
import lab.moea.algorithm.CustomAlgorithmFactory;
import lab.moea.history.AOSHistory;
import lab.moea.problem.CustomProblemFactory;
import lab.moea.util.SimpleLog;
import lab.moea.util.Util;
import lab.moea.util.io.SimplePIO;

public class solveProblem {
	private static String MOP_PROPERTY_PATH = "mop.properties";
	private static String ALG_PROPERTY_PATH = "algorithm.properties";

	/**
	 * optimize the multi-objective optimization problem with specified algorithm. 
	 * 
	 * @param pName the name of problem to solve
	 * @param aName the name of algorithm
	 * @param trial a trial number
	 */
	@SuppressWarnings("unused")
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
 		
        /**
 		 * Open history file 
 		 */
     	String historyName = Util.makeFileName("history_pf", ".txt");     		
     	spio.openHistoryFile(historyName, false);

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
		Instrumenter instrumenter = new Instrumenter()
				.withProblem(pName+"_"+nobj)
				.withFrequency(2)
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
		 * Set up AOS
		 */
		// create operators
		// Please see org.moeaframework.core.operator.StandardOperators.java for operator options
        Properties op_property = new Properties();
//        op_property.setProperty("populationSize", String.valueOf(npop));
        
        OperatorFactory of = OperatorFactory.getInstance();
        ArrayList<Variation> operators = new ArrayList<Variation>();
//        operators.add(of.getVariation("um"      , op_property, problem));
        operators.add(of.getVariation("sbx+pm"  , op_property, problem));
        operators.add(of.getVariation("de+pm"   , op_property, problem));
        operators.add(of.getVariation("pcx+pm"  , op_property, problem));
        operators.add(of.getVariation("undx+pm" , op_property, problem));
//        operators.add(of.getVariation("spx+pm"  , op_property, problem));  

        // create operator selector
        IOperatorSelector operatorSelector = new ProbabilityMatching(operators, 0.8, 0.1);//(operators, alpha, pmin)
        operatorSelector.setOutputPath(outputPath);
        
        // create credit assignment
        ICreditAssignment creditAssignment1 = new ParentDomination(1, 0, 0);  // OP-Dominance
        ICreditAssignment creditAssignment2 = new ParetoFrontContribution(1, 0); // CS-Dominance
        ICreditAssignment creditAssignment3 = new ParentIndicator(problem, 0.6); // OP-Indicator
        ICreditAssignment creditAssignment4 = new OffspringParetoFrontDominance(1, 0); // SI-Dominance
        
        // create aos strategy 
        AOSStrategy aosStrategy = new AOSStrategy(
        		creditAssignment1, 
        		creditAssignment2, 
        		operatorSelector
        );
        aosStrategy.setHistoryPath(outputPath);

		/**
		 * Define variation.
		 */
		AOSVariation variation = new AOSVariation(aosStrategy, problem); 
		
		/**
		 * Construct the algorithm
		 * When use Properties class, setProperty function should be used. (Don't use set function) 
		 * Please see 
		 */
		TypedProperties alg_properties = new TypedProperties();
		alg_properties.getProperties().setProperty("use_archive", "true");
		alg_properties.getProperties().setProperty("populationSize", String.valueOf(npop));
		Algorithm algorithm = CustomAlgorithmFactory.getAlgorithm(
				aName, problem, selection, variation, initialization, alg_properties);
        
        AOSMOEA aos = new AOSMOEA((AbstractEvolutionaryAlgorithm) algorithm, aosStrategy);
        
		/**
		 * attach collectors to algorithm
		 */
		Algorithm ialgorithm = instrumenter.instrument(aos);
		
		/**
		 * Run the algorithm for the specified number of evaluation. 
		 */
		int gen = 0;
		int interval = 100;
		while (ialgorithm.getNumberOfEvaluations() < max_evaluation) {
			if (gen % interval == 0 ) SimpleLog.Info("trial = " + trial + ", gen = " + gen);

			ialgorithm.step();
			
			NondominatedPopulation result = aos.getResult();
			spio.writeHistory(gen, ialgorithm.getNumberOfEvaluations(), result);

			gen++;
		}
		
		aosStrategy.saveHistories();
		
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
		//Util.Plot(result, aName, pName, false);
	}
}
