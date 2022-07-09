package jobscheduling.trial2020v2;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

//import java.util.logging.Logger;

import org.moeaframework.core.Solution;
import org.moeaframework.problem.AbstractProblem;

public class JSP extends AbstractProblem {
	private List<Job> jobs;
//	private List<Integer> scheduled_jobids; // scheduled-job-id list 
	private ScheduleInfoList schedule_infos; 
	private int max_node;
	private double time_interval = 0.0; // [hour]
	private double current_time = 0.0; // [hour]
		
	public JSP(int max_node, double time_interval) {
		super(1, 2); // 1 variable, 2 objectives
		
		this.max_node = max_node;
		this.time_interval = time_interval;
		
		Job.reset();
		
		jobs = new ArrayList<Job>();
//		scheduled_jobids = new ArrayList<Integer>();
		schedule_infos = new ScheduleInfoList();
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
	
	public List<Integer> getOptJobIds() {
		List<Integer> ojobids = new ArrayList<Integer>();
		for (Job job : jobs) {
			if (job.isOptTarget(current_time)) {
				ojobids.add(job.getId());
			}
		}
		
		return ojobids;		
	}
	
	public void step() {
		current_time += time_interval;
		for (Job job : jobs) job.updateStatus(current_time);
		
	}
	
	public int getRunningNodeNum(double current_time) {
		int node_num = 0;
		
		for (Job job : jobs) {
			if (job.isRunning(current_time)) {
				node_num += job.getReq_node_num();
			}			
		}
		
		return node_num;
	}
	
	public void setSchedule(Solution solution) {
		ScheduleVariable sv = (ScheduleVariable) solution.getVariable(0);
				
		int nsche = sv.getScheduleLength();
		for (int i=0; i<nsche; i++) {
			int jobid = sv.getSchedule(i);
			
//			scheduled_jobids.add(jobid);
			jobs.get(jobid).setStart_time(sv.getStart_time(i));
			
			schedule_infos.add(sv.getStart_time(i), 0);
			schedule_infos.add(sv.getEnd_time(i), 0);
		}
		
		updateScheduleInfo(schedule_infos);
		
//		System.out.println("# Schedule info");
//		schedule_infos.print();
	}
	
	public void setSchedule(Solution solution, ScheduleInfoList sinfo) {
		ScheduleVariable sv = (ScheduleVariable) solution.getVariable(0);
				
		int nsche = sv.getScheduleLength();
		for (int i=0; i<nsche; i++) {
			int jobid = sv.getSchedule(i);
			
			jobs.get(jobid).setStart_time(sv.getStart_time(i));
			
			sinfo.add(sv.getStart_time(i), 0);
			sinfo.add(sv.getEnd_time(i), 0);
		}
		
		updateScheduleInfo(sinfo);
		
//		System.out.println("# Schedule info");
//		sinfo.print();
	}
	
	public ScheduleInfoList getScheduleInfoList() {
		return schedule_infos;
	}
	
	public void printJobs() {
		for (int i=0; i<jobs.size(); i++) {
			jobs.get(i).print();
			System.out.println("");
		}
	}	
	
	public void printJobsBySolution(Solution solution) {
		ScheduleVariable sv = (ScheduleVariable) solution.getVariable(0);
		
		for (int i=0; i<sv.getScheduleLength(); i++) {
			jobs.get(sv.getSchedule(i)).print();
			System.out.format(" - [%2d] ID=%3d STime[s]=%6.3f ETime=%6.3f", i, sv.getSchedule(i), sv.getStart_time(i), sv.getEnd_time(i));
			System.out.println("");
		}
	}
	
	public void printSchedule(Solution solution) {
		ScheduleVariable sv = (ScheduleVariable) solution.getVariable(0);
		
		for (int i=0; i<sv.getScheduleLength(); i++) {
			System.out.print(sv.getSchedule(i)+" ");
		}
		System.out.println("");
	}
	
	public void printJobs(ScheduleVariable sv) {
		for (int i=0; i<jobs.size(); i++) {
			jobs.get(sv.getSchedule(i)).print();
			System.out.println("\t\tStart_Time="+sv.getStart_time(i)+" End_Time="+sv.getEnd_time(i));
		}
	}
	
	public int findScheduleInfoIndex(List<ScheduleInfo> infos, double current_time) {
		for(int i=1; i<infos.size(); i++) {
			if (current_time < infos.get(i).time) return i-1;
		}
		return infos.size() - 1;
	}
		
	public void updateScheduleInfo(ScheduleInfoList infos) {
		Iterator<ScheduleInfo> iterator = infos.iterator();
		while (iterator.hasNext()) {
			ScheduleInfo schedule_info = iterator.next();
			schedule_info.node_num = getRunningNodeNum(schedule_info.time);
		}
	}
		
	public void calcStartTime(ScheduleVariable sv) {
		int nsche = sv.getScheduleLength();
		
		ScheduleInfoList infos = new ScheduleInfoList(schedule_infos);
		
//		System.out.println("--------------------------------------------------------");
		for (int index, i=0; i<nsche; i++) {
			index = sv.getSchedule(i);
			int node_num = jobs.get(index).getReq_node_num();
			double request_time = jobs.get(index).getReq_time();
			
			// search start_time
			double start_time = infos.findStartTime(current_time, node_num, request_time, max_node);
			double end_time = start_time + request_time;
			
			// set start_time and end_time
			sv.setStart_time(i, start_time);
			sv.setEnd_time(i, end_time);
			
			// update infos
//			infos.print();
//			System.out.format("[%d] CT=%6.3f ST=%6.3f ET=%6.3f N=%3d\n", index, current_time, start_time, end_time, node_num);
			infos.update(start_time, end_time, node_num);
						
//			infos.print();
//			System.out.println("");
		}
//		System.out.println("--------------------------------------------------------");
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
						
			mean_node_usage_ratio += request_node_num * request_time;
		}
		
		mean_node_usage_ratio /= max_node * (sv.getMax_end_time() - current_time);
				
		//logger.info("Mean Node Usage Ratio="+mean_node_usage_ratio);
		return mean_node_usage_ratio;
	}
	
	@Override
	public void evaluate(Solution solution) {
		ScheduleVariable sv = (ScheduleVariable)solution.getVariable(0);
		
		calcStartTime(sv);
		
		solution.setObjective(0, calcMeanWaitingTime(sv));            // minimization
		solution.setObjective(1, -1.0 * calcMeanNodeUsageRatio(sv));  // maximization -> minimization
	}
	
	@Override
	public Solution newSolution() {
		Solution solution = new Solution(1, 2); // 1 variable, 2 objectives

		ScheduleVariable sv = new ScheduleVariable();
		solution.setVariable(0, sv);
		return solution;
	}
}
