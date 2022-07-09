package jobscheduling.trial2020;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.moeaframework.Executor;
import org.moeaframework.analysis.plot.Plot;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.EncodingUtils;

import org.moeaframework.core.PRNG;
import org.moeaframework.core.PopulationIO;
import org.moeaframework.algorithm.NSGAII;
import org.moeaframework.core.Algorithm;

import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.NondominatedSortingPopulation;

import org.moeaframework.core.Problem;

import org.moeaframework.core.Initialization;
import org.moeaframework.core.operator.RandomInitialization;

import org.moeaframework.core.operator.TournamentSelection;
import org.moeaframework.core.comparator.ChainedComparator;
import org.moeaframework.core.comparator.CrowdingComparator;
import org.moeaframework.core.comparator.ParetoDominanceComparator;

import org.moeaframework.core.Variation;
import org.moeaframework.core.operator.GAVariation;
import org.moeaframework.core.operator.real.PM;
import org.moeaframework.core.operator.real.SBX;

import jobscheduling.trial2020.JSP;
import jobscheduling.trial2020.Job;
import jobscheduling.trial2020.ScheduleVariable;
import jobscheduling.trial2020.JSPIO;

public class JSPMain {
	private static int npop = 10;
	private static int ngen = 10;
	private static int njob = 10;
	private static int max_node = 384;
	private static int max_req_time = 24;
	private static double period = 60; //[sec]
	
	private static String output_path = "output/JSP/";
	
	private static void test() {
		JSP problem = new JSP(max_node);
		JobGenerator jobgen = new JobGenerator(max_node);
		
		for (int i=0; i<njob; i++) {
		    Job job = jobgen.nextJob();
		    problem.addJob(job);
		}	
	}
	
	private static void generateJob(JSP problem) {
		JobGenerator jobgen = new JobGenerator(max_node);

		/**
		 * generate jobs within a specified period of time
		 */
		double arrive_time = 0;
		while (arrive_time < period) {
			Job job = jobgen.nextJob();
			arrive_time = job.getArrive_time() * 3600; // [sec]
			problem.addJob(job);
		}
		
		/**
		 * fix job size
		 */
//		for (int i=0; i<njob; i++) {
//			Job job = jobgen.nextJob();
//			problem.addJob(job);
//		}		
	}
	
	private static void opt() {
		/**
		 * Define the optimization problem.
		 * The types of variables are determined in this class.
		 */
		JSP problem = new JSP(max_node);
		generateJob(problem);
		problem.setCurrentTime(period / 3600); // [hour]

		// output jobs 
		try {
			JSPIO.writeJobs(new File(output_path+"jobs.csv"), problem);
		} catch (IOException ex) {
            Logger.getLogger(JSPMain.class.getName()).log(Level.SEVERE, null, ex);
        } 		
		
		/**
		 * Create an initial random population.
		 * The population size(=npop) is specified here.
		 */
		Initialization initialization = new RandomInitialization(
				problem,
				npop);
		
		/**
		 * Define the crossover and mutation operator.
		 */
		TournamentSelection selection = new TournamentSelection(2, 
				new ChainedComparator(
						new ParetoDominanceComparator(),
						new CrowdingComparator()
						));
		
		/**
		 * Define the crossover and mutation operator.
		 */
		Variation variation = new GAVariation(
				new OX(1.0),
				new SimpleSwapMutation(1.0 / problem.getJobSize())
				);
		
		/**
		 * Construct the algorithm
		 */
		NSGAII algorithm = new NSGAII(
				problem,
				new NondominatedSortingPopulation(),
				null, // no archive
				selection,
				variation,
				initialization);
		
		/**
		 * Run the algorithm for the specified number of evaluation. 
		 */
		int max_number_of_evaluation = npop * ngen;
		int gen = 0;
		while (algorithm.getNumberOfEvaluations() < max_number_of_evaluation) {
			System.out.println("Gen="+gen);
			algorithm.step();
			gen++;
			
			try {
				JSPIO.writeObjectives(new File(output_path+gen+"_objs.csv"), algorithm.getPopulation());
				JSPIO.writeSolutions(new  File(output_path+gen+"_vars.csv"), algorithm.getPopulation());
            } catch (IOException ex) {
                Logger.getLogger(JSPMain.class.getName()).log(Level.SEVERE, null, ex);
            }   
		}
		
		/**
		 * Get the Pareto approximate results
		 */
		NondominatedPopulation result = algorithm.getResult();
		
		try {
			JSPIO.writeObjectives(new File(output_path+"pareto_objs.csv"), result);
			JSPIO.writeSolutions(new  File(output_path+"pareto_vars.csv"), result);
        } catch (IOException ex) {
            Logger.getLogger(JSPMain.class.getName()).log(Level.SEVERE, null, ex);
        } 
		
		/**
		 * Plot final result
		 */
		Plot plt = new Plot();
		plt.add("NSGAII", result);
		plt.show();
	}
	
	private static void InitLogger() {
		Logger logger = Logger.getLogger("");
		try {
			FileHandler fHandler = new FileHandler(output_path+"jsp.log", false);
	        fHandler.setFormatter(new SimpleFormatter());
	        logger.addHandler(fHandler);
	        logger.setLevel(Level.INFO);
		} catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	public static void main(String[] args) {
		InitLogger();
		
//		test();
		opt();
	}

}
