package jobscheduling.trial2020;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Collections;

import java.util.logging.Logger;

import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.EncodingUtils;
import org.moeaframework.problem.AbstractProblem;

import jobscheduling.trial2020.Job;
import jobscheduling.trial2020.ScheduleVariable;
import jobscheduling.trial2020.ScheduleVariable.Status;

public class JSP extends AbstractProblem {
	private List<Job> jobs;
	private int max_node;
	private double current_time = 0.0;
	
	public JSP(int max_node) {
		super(1, 2); // 1 variable, 2 objectives
		this.max_node = max_node;
		jobs = new ArrayList<Job>();
	}
	
	public void addJob(Job job) {
		jobs.add(job);
	}
	
	public Job getJob(int index) {
		return jobs.get(index);
	}
	
	public int getJobSize() {
		return jobs.size();
	}
	
	public double getCurrentTime() {
		return current_time;
	}
	
	public void setCurrentTime(double current_time) {
		this.current_time = current_time;
	}

	public void printJobs() {
		for (int i=0; i<jobs.size(); i++) {
			jobs.get(i).print();
		}
	}	
	
	public void printJobs(Solution solution) {
		ScheduleVariable sv = (ScheduleVariable) solution.getVariable(0);
		
		for (int i=0; i<jobs.size(); i++) {
			jobs.get(sv.getSchedule(i)).print();
			System.out.println("\t\tStart_Time="+sv.getStart_time(i)+" End_Time="+sv.getEnd_time(i)+" Status="+sv.getStatus(i));
		}
	}
	
	public void printSchedule(Solution solution) {
		ScheduleVariable sv = (ScheduleVariable) solution.getVariable(0);
		
		for (int i=0; i<jobs.size(); i++) {
			System.out.print(sv.getSchedule(i)+" ");
		}
		System.out.println("");
	}
	
	public void printJobs(ScheduleVariable sv) {
		for (int i=0; i<jobs.size(); i++) {
			jobs.get(sv.getSchedule(i)).print();
			System.out.println("\t\tStart_Time="+sv.getStart_time(i)+" End_Time="+sv.getEnd_time(i)+" Status="+sv.getStatus(i));
		}
	}
	
	public void calcStartTime(ScheduleVariable sv, double initial_start_time) {
		Logger logger = Logger.getLogger(getName());
		
		// number of schedule
		int nsche = sv.getScheduleLength();
		
		// current number of node
		int current_node_sum = 0;
		
		// current start time
		double current_start_time = initial_start_time; // [h]
		
		// current queued job		
		List<Integer> queued_jobs = new ArrayList<Integer>();
		
		for (int current_index, i=0; i<nsche; i++) {
			current_index = sv.getSchedule(i);
			int current_request_node_num = jobs.get(current_index).getReq_node_num();
			
			if (current_node_sum + current_request_node_num > max_node) {
				// search the shortest next start time
				// It is searched from end_time and request_node_num of the queued_job list.
				
				// sort queued_job by End_Time
				Collections.sort(queued_jobs, new EndTimeComparator(sv));
											
				Iterator it = queued_jobs.iterator();
				while(it.hasNext()) {
					int j = (int)it.next();
					int j_index = sv.getSchedule(j);
					
					// update current_start_time
					current_start_time = sv.getEnd_time(j);
					
					// update current_node_sum
					current_node_sum -= jobs.get(j_index).getReq_node_num(); 
					
					// remove the job from queued_jobs because this job will be finished in current_start_time
					it.remove();
					
					//logger.info("- "+j+" : Start_Time="+current_start_time+" Node Sum="+current_node_sum);
					
					if (current_node_sum + current_request_node_num <= max_node) {
						break;
					}
				}
			}
			
			current_node_sum += current_request_node_num; // update current_node_sum
			sv.setStart_time(i, current_start_time);
			sv.setEnd_time(i, current_start_time + jobs.get(current_index).getReq_time());
			sv.setStatus(i, Status.QUEUED);
			
			//logger.info("+ "+i+" : Start_Time="+current_start_time+" Node Sum="+current_node_sum);
			
			// i-th job is successfully queued. 
			queued_jobs.add(i);
		}
	}
	
	public double calcMeanWaitingTime(ScheduleVariable sv) {
		//Logger logger = Logger.getLogger(getName());
	
		// number of schedule
		int nsche = sv.getScheduleLength();
		
		// calc mean_waiting_time
		double mean_waiting_time = 0.0;
		for (int i=0; i<nsche; i++) {
			int index = sv.getSchedule(i);
			
			double arrive_time = jobs.get(index).getArrive_time();	
//			double arrive_time = 0.0;
			
			mean_waiting_time += sv.getStart_time(i) - arrive_time;
		}
		
		mean_waiting_time /= nsche;
		
		//logger.info("Mean Waiting Time="+mean_waiting_time);
		return mean_waiting_time;
	}
	
	public double calcMeanNodeUsageRatio(ScheduleVariable sv) {
		//Logger logger = Logger.getLogger(getName());
		
		// number of schedule
		int nsche = sv.getScheduleLength();
		
		double mean_node_usage_ratio = 0.0;
		for (int i=0; i<nsche; i++) {
			int index = sv.getSchedule(i);
			double request_node_num = jobs.get(index).getReq_node_num();
			double request_time = jobs.get(index).getReq_time();
			
//			double arrive_time = jobs.get(index).getArrive_time();
//			double arrive_time = 0.0;
			
			mean_node_usage_ratio += request_node_num * request_time;
		}
		
		mean_node_usage_ratio /= max_node * sv.getMax_end_time();
				
		//logger.info("Mean Node Usage Ratio="+mean_node_usage_ratio);
		return mean_node_usage_ratio;
	}
	
	@Override
	public void evaluate(Solution solution) {
		ScheduleVariable sv = (ScheduleVariable)solution.getVariable(0);
		
		//printJobs(sv);
		calcStartTime(sv, current_time);
		
		solution.setObjective(0, calcMeanWaitingTime(sv));
		solution.setObjective(1, -1.0 * calcMeanNodeUsageRatio(sv));
	}
	
	@Override
	public Solution newSolution() {
		Solution solution = new Solution(1, 2); // 1 variable, 2 objectives

		int njob = jobs.size();
		ScheduleVariable sv = new ScheduleVariable(njob);
		solution.setVariable(0, sv);
		return solution;
	}
}
