package jobscheduling.trial2020v2;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

//import org.moeaframework.Executor;
import org.moeaframework.analysis.plot.Plot;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Solution;
//import org.moeaframework.core.variable.EncodingUtils;

//import org.moeaframework.core.PRNG;
//import org.moeaframework.core.PopulationIO;
import org.moeaframework.algorithm.NSGAII;
//import org.moeaframework.core.Algorithm;

//import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.NondominatedSortingPopulation;

//import org.moeaframework.core.Problem;

import org.moeaframework.core.Initialization;
import org.moeaframework.core.operator.RandomInitialization;

import org.moeaframework.core.operator.TournamentSelection;
import org.moeaframework.core.comparator.ChainedComparator;
import org.moeaframework.core.comparator.CrowdingComparator;
import org.moeaframework.core.comparator.ParetoDominanceComparator;

import org.moeaframework.core.Variation;
import org.moeaframework.core.operator.GAVariation;

public class JSPMain {
	private static int npop = 100;
	private static int ngen = 100;
	private static int max_node = 384;
	private static double time_interval = 60.0 / 3600; // [hour] 
	private static double sim_period = time_interval * 1; //
	
	private static String output_path = "output/JSP/";
	
	private static void run_single_solution() {
		Logger logger = Logger.getLogger(JSPMain.class.getName());

		JSP problem = new JSP(max_node, time_interval);
		readJob(problem, true);
		problem.printJobs();
		
		problem.step(); // update current_time and status

		List<Integer> ojobids = problem.getOptJobIds();
		ScheduleVariable.setScheduleRef(ojobids);
		
		Solution sol = problem.newSolution();
		ScheduleVariable sv = (ScheduleVariable)sol.getVariable(0);
		
		sv.seq();
		sv.printSchedule();

		problem.evaluate(sol);
		problem.setSchedule(sol);
		problem.printJobsBySolution(sol);
		
		List<Solution> sols = new ArrayList<Solution>();
		sols.add(sol);
		
		try {
			JSPIO.writeObjectives(new File(output_path+"single__objs.csv"), sols);
			JSPIO.writeSolutions(new File(output_path+"single_vars.csv"), sols);
			JSPIO.writeScheduleInfoList(new File(output_path+"single_scheduleinfo_list.csv"), problem.getScheduleInfoList());
        } catch (IOException ex) {
            logger.severe(ex.toString());
        }
		
		try {
			JSPIO.writeJobs(new File(output_path+"single_jobs.csv"), problem);
		} catch (IOException ex) {
            logger.severe(ex.toString());
        }
	}
	
//	private static void test3() {
//		JSP problem = new JSP(max_node, time_interval);
//		
//		try {
//			JSPIO.readJobs(new File(output_path+"jobs.csv"), problem, true);
//		} catch (IOException ex) {
//            Logger.getLogger(JSPMain.class.getName()).log(Level.SEVERE, null, ex);
//        } 				
//	}
//	
//	private static void test2() {
//		ScheduleInfoList sinfos = new ScheduleInfoList();
//		
//		sinfos.set(0, 0);
//		sinfos.set(0.003, 384);
//		sinfos.set(1.003, 372);
//		sinfos.add(2.003, 132);
//		sinfos.add(5.003, 96);
//		sinfos.add(12.003, 84);
//		sinfos.add(24.003, 36);
//		sinfos.set(26.003, 0);
//		
//		sinfos.print();
//
//		System.out.println(sinfos.isAvailable(0.006, 12, 12, max_node));
////		System.out.println(sinfos.isAvailable(0, 5, 400, max_node));
////		System.out.println(sinfos.isAvailable(5, 12, 384-40, max_node));
////		System.out.println(sinfos.findStartTime(0, 300, 11, max_node));
//	}
//	
//	private static void test() {
//		JSP problem = new JSP(max_node, time_interval);
//		JobGenerator jobgen = new JobGenerator(max_node);
//		jobgen.setArriveTimeRefSec(0.0);
//		
//		Job job = jobgen.nextJob();
//		double arrive_time = job.getArrive_time(); // [hour]
//
//		while (arrive_time < time_interval*10) {
//			problem.addJob(job);
//			job = jobgen.nextJob();
//			arrive_time = job.getArrive_time();
//		}
//		
//		problem.printJobs();
//	
//		for (int i=0; i<2; i++) {
//			problem.step(); // update current_time and status
//
//			List<Integer> ojobids = problem.getOptJobIds();
//			ScheduleVariable.setScheduleRef(ojobids);
//			
//			System.out.println(ojobids.toString());
//
//			Solution sol = problem.newSolution();
//			ScheduleVariable sv = (ScheduleVariable)sol.getVariable(0);
//			sv.randomize();
//			sv.printSchedule();
////			problem.evaluate(sol);
//			problem.calcStartTime(sv);
//
//			problem.printJobsBySolution(sol);
//			problem.setSchedule(sol);
//			problem.printJobsBySolution(sol);
//		
//		}
//		
//	}
	
	private static void readJob(JSP problem, boolean reset_status) {
		try {
			JSPIO.readJobs(new File(output_path+"jobs.csv"), problem, reset_status);
		} catch (IOException ex) {
            Logger.getLogger(JSPMain.class.getName()).log(Level.SEVERE, null, ex);
        } 		
	}
	
	
	private static void generateJob(JSP problem) {
		JobGenerator jobgen = new JobGenerator(max_node);

		/**
		 * generate jobs within a specified period of time
		 */
		Job job = jobgen.nextJob();
		double arrive_time = job.getArrive_time(); // [hour]

		while (arrive_time < sim_period) {
			problem.addJob(job);
			
			job = jobgen.nextJob();
			arrive_time = job.getArrive_time();
		}
				
		/**
		 * fix job size
		 */
//		for (int i=0; i<njob; i++) {
//			Job job = jobgen.nextJob();
//			problem.addJob(job);
//		}
		
		problem.printJobs();
	}
	
	private static void opt() {
		Logger logger = Logger.getLogger(JSPMain.class.getName());
		
		/**
		 * Define the optimization problem.
		 * The types of variables are determined in this class.
		 */
		JSP problem = new JSP(max_node, time_interval);
		generateJob(problem);

		double current_time = problem.getCurrentTime();
		int nopt = 0;
		while (current_time < sim_period) {
			problem.step();
			current_time = problem.getCurrentTime();
	
			List<Integer> ojobids = problem.getOptJobIds();
			ScheduleVariable.setScheduleRef(ojobids);
			System.out.println(ojobids.toString());
			if (ojobids.size() == 0) continue;
			
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
				System.out.println("Opt="+nopt+" Gen="+gen);
				algorithm.step();
				gen++;
				
				try {
					JSPIO.writeObjectives(new File(output_path+nopt+"_"+gen+"_objs.csv"), algorithm.getPopulation());
					JSPIO.writeSolutions(new File(output_path+nopt+"_"+gen+"_vars.csv"), algorithm.getPopulation());
	            } catch (IOException ex) {
	                logger.severe(ex.toString());
	            }   
			}
			
			/**
			 * Get the Pareto approximate results
			 */
			NondominatedPopulation result = algorithm.getResult();
			
			try {
				JSPIO.writeObjectives(new File(output_path+nopt+"_pareto_objs.csv"), result);
				JSPIO.writeSolutions(new File(output_path+nopt+"_pareto_vars.csv"), result);
	        } catch (IOException ex) {
	            logger.severe(ex.toString());
	        } 
			
			// It's for analysis of objective1
			ScheduleInfoList sinfo = new ScheduleInfoList(problem.getScheduleInfoList());			
			int ndom = result.size();
			Solution next_solution = problem.newSolution();
			double obj_ref = 100000.0;
			for (int i=0; i<ndom; i++) {
				Solution solution = result.get(i);
				double obj = solution.getObjective(1);
				if (obj < obj_ref) {
					obj_ref = obj;
					ScheduleVariable sv = (ScheduleVariable)solution.getVariable(0);
					next_solution.setVariable(0, sv);
				}	
			}
			problem.setSchedule(next_solution, sinfo); // The schedule order of next_solution is set to sinfo, not problem's ScheduleInfoList.
			problem.printJobsBySolution(next_solution);

			try {
				JSPIO.writeScheduleInfoList(new File(output_path+nopt+"_scheduleinfo_list_obj1.csv"), sinfo);
	        } catch (IOException ex) {
	            logger.severe(ex.toString());
	        }

			// find next solution. Best solution of objective2 is used for the next schedule.
			next_solution = problem.newSolution();
			obj_ref = 100000.0;
			for (int i=0; i<ndom; i++) {
				Solution solution = result.get(i);
				double obj = solution.getObjective(0);
				if (obj < obj_ref) {
					obj_ref = obj;
					ScheduleVariable sv = (ScheduleVariable)solution.getVariable(0);
					next_solution.setVariable(0, sv);
				}	
			}
			problem.setSchedule(next_solution); // The schedule order of next_solution is set to problem's ScheduleInfoList.
			problem.printJobsBySolution(next_solution);

			try {
				JSPIO.writeScheduleInfoList(new File(output_path+nopt+"_scheduleinfo_list_obj0.csv"), problem.getScheduleInfoList());
	        } catch (IOException ex) {
	            logger.severe(ex.toString());
	        }
			
			
			/**
			 * Plot final result
			 */
//			Plot plt = new Plot();
//			plt.add("NSGAII", result);
//			plt.show();
			
			nopt++;
		}
		
		// output jobs 
		try {
			JSPIO.writeJobs(new File(output_path+"jobs.csv"), problem);
		} catch (IOException ex) {
            Logger.getLogger(JSPMain.class.getName()).log(Level.SEVERE, null, ex);
        } 			
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
		// Initialize logger
		InitLogger();
		
		// do optimization
		opt();
		// The case in which all jobs are scheduled in arrival order is computed.
		run_single_solution();

		System.out.println("Finish");
	}

}
