package jobscheduling.trial2020v2;

import java.io.BufferedReader;
//import java.io.BufferedInputStream;
//import java.io.BufferedOutputStream;
//import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
//import java.io.FileInputStream;
//import java.io.FileOutputStream;
//import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
//import java.io.ObjectInputStream;
//import java.io.ObjectOutputStream;
//import java.util.ArrayList;
//import java.util.List;
import java.util.Iterator;

//import org.moeaframework.core.Population;
import org.moeaframework.core.Solution;
//import org.moeaframework.util.io.CommentedLineReader;
import org.moeaframework.util.io.CommentedLineReader;

import jobscheduling.trial2020v2.Job.Status;

public class JSPIO {

	/**
	 * Private constructor to prevent instantiation.
	 */
	private JSPIO() {
		super();
	}
	
	public static void writeObjectives(File file, Iterable<Solution> solutions)
			throws IOException {
		BufferedWriter writer = null;

		try {
			writer = new BufferedWriter(new FileWriter(file));

			for (Solution solution : solutions) {
				writer.write(Double.toString(solution.getObjective(0)));

				for (int i = 1; i < solution.getNumberOfObjectives(); i++) {
					writer.write(" ");
					writer.write(Double.toString(solution.getObjective(i)));
				}

				writer.newLine();
			}
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
	}

	public static void writeSolutions(File file, Iterable<Solution> solutions)
			throws IOException {
		BufferedWriter writer = null;

		try {
			writer = new BufferedWriter(new FileWriter(file));
			
			boolean initial_flag = true;
			int nsche = 0;
			for (Solution solution : solutions) {
				ScheduleVariable sv = (ScheduleVariable)solution.getVariable(0);
				
				if (initial_flag) {
					initial_flag = false;

					nsche = sv.getScheduleLength(); 
					
					writer.write("job0");
					for (int i=1; i<nsche; i++) {
						writer.write(",");
						writer.write("job"+i);
					}
					for (int i=0; i<nsche; i++) {
						writer.write(",");
						writer.write("start_time"+i);
					}
					for (int i=0; i<nsche; i++) {
						writer.write(",");
						writer.write("end_time"+i);
					}
					writer.newLine();
				}
				
				writer.write(Integer.toString(sv.getSchedule(0)));
				for (int i=1; i<nsche; i++) {
					writer.write(",");
					writer.write(Integer.toString(sv.getSchedule(i)));
				}
				for (int i=0; i<nsche; i++) {
					writer.write(",");
					writer.write(Double.toString(sv.getStart_time(i)));
				}
				for (int i=0; i<nsche; i++) {
					writer.write(",");
					writer.write(Double.toString(sv.getEnd_time(i)));
				}
				writer.newLine();
			}
			
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
	}
	
	
	public static void readJobs(File file, JSP problem, boolean reset_status) throws
	IOException {
		String line = null;
		BufferedReader reader = null;
		
		try {
			reader = new CommentedLineReader(new FileReader(file));
			reader.readLine();

			while ((line = reader.readLine()) != null) {
				String[] tokens = line.trim().split(",");
				
//				int id = Integer.parseInt(tokens[0]);
				int request_node_num = Integer.parseInt(tokens[1]);
				double request_time = Double.parseDouble(tokens[2]);
				double arrive_time = Double.parseDouble(tokens[3]);
				double start_time = Double.parseDouble(tokens[4]);
//				double end_time = Double.parseDouble(tokens[5]);
				Status status = reset_status ? Status.SUBMITTED : Status.valueOf(tokens[6]);
				
				Job job = new Job(request_node_num, request_time, arrive_time, start_time, status);
	
				problem.addJob(job);
			}	
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
	}	
	
	public static void writeJobs(File file, JSP problem)
			throws IOException {
		BufferedWriter writer = null;

		try {
			writer = new BufferedWriter(new FileWriter(file));

			writer.write("id,request_node_num,request_time[h],arrive_time[h],start_time[h],end_time[h],status");
			writer.newLine();
			
			int njob = problem.getJobSize();
			for (int i=0; i<njob; i++) {
				Job job = problem.getJob(i);
				
				int id = job.getId();
				int request_node_num = job.getReq_node_num();
				double request_time = job.getReq_time();
				double arrive_time = job.getArrive_time();
				double start_time = job.getStart_time();
				double end_time = job.getEnd_time();
				Status status = job.getStatus();
				
				writer.write(Integer.toString(id));
				writer.write(",");
				writer.write(Integer.toString(request_node_num));
				writer.write(",");
				writer.write(Double.toString(request_time));
				writer.write(",");
				writer.write(Double.toString(arrive_time));
				writer.write(",");
				writer.write(Double.toString(start_time));
				writer.write(",");
				writer.write(Double.toString(end_time));
				writer.write(",");
				writer.write(status.name());
				writer.newLine();
			}
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
	}	

	public static void writeScheduleInfoList(File file, ScheduleInfoList schedule_infos)
			throws IOException {
		BufferedWriter writer = null;

		try {
			writer = new BufferedWriter(new FileWriter(file));

			writer.write("time,node");
			writer.newLine();

			Iterator<ScheduleInfo> iterator = schedule_infos.iterator();
			while (iterator.hasNext()) {
				ScheduleInfo schedule_info = iterator.next();
				writer.write(Double.toString(schedule_info.time));
				writer.write(",");
				writer.write(Integer.toString(schedule_info.node_num));
				writer.newLine();
			}			
			
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
	}	
	
	
}
