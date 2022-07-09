package jobscheduling.trial2020v2;

import java.io.BufferedReader;
//import java.io.BufferedWriter;
import java.io.IOException;
import java.io.FileReader;
//import java.io.FileWriter;

import org.moeaframework.core.PRNG;

import jobscheduling.trial2020v2.Job.Status;

public class JobGenerator {
	private double[] cumuDensityDist;
	private double arrive_time_ref = 0.0; // [sec]
	private int max_node;
	
	public JobGenerator(int max_node) {
		this.max_node = max_node;
		
		double[][] csvdata;
		csvdata = new double[32][24];
		cumuDensityDist = new double[32 * 24];
		
		try {
			BufferedReader br = new BufferedReader(new FileReader("./jobscheduling/resource/ProbabilityDistribution_small.csv"));
			
			for(int i=0; i<32; i++) {
				String line_in = br.readLine();
				String[] data = line_in.split(",");
				for(int j=0; j<24; j++) {
					csvdata[i][j] = Double.parseDouble(data[j]);
				}
			}
			br.close();			
		} catch(IOException e) {
			System.out.println(e.getMessage());
		}
		
		cumuDensityDist[0] = 0.0;
		for(int i=0; i<32; i++) {
			for (int j=0; j<24; j++) {
				int index1 = i * 24 + j;
				int index2 = (i * 24 + j - 1 < 0) ? 0 : i * 24 + j - 1;
				cumuDensityDist[index1] += csvdata[i][j] + cumuDensityDist[index2];
			}
		}
		cumuDensityDist[cumuDensityDist.length-1] = 1.0;
		
//		for (int i=0; i<cumuDensityDist.length; i++) {
//			System.out.println(i + " = " + cumuDensityDist[i]);
//		}
	}
	
	private int getIndex() {
		double r = PRNG.nextDouble();
		if (r < cumuDensityDist[0]) return 0;
		
		for(int index = 1; index<cumuDensityDist.length; index++) {
			if (cumuDensityDist[index-1] <= r && r < cumuDensityDist[index]) {
				return index;
			}
		}
		return cumuDensityDist.length - 1; 
	}
	
	private double getArriveIntervalSec(double lambda) {
		double r = PRNG.nextFloat();
		return -(1.0/lambda)*Math.log(1 - r); // [sec]
	}
	
	public double getArriveTimeRefSec() {
		return arrive_time_ref;
	}
	
	public void setArriveTimeRefSec(double arrive_time_base) {
		this.arrive_time_ref = arrive_time_base; // [sec]
	}
	
	public Job nextJob() {
		int index = getIndex();
		
		int request_node_num = (index / 24 + 1) * 12 < max_node ? (index / 24 + 1) * 12 : max_node;
		double request_time = (index % 24 + 1); // [hour]
		double arrive_interval = getArriveIntervalSec(0.812); // lambda = 0.00812, [sec]
		double arrive_time = (arrive_time_ref + arrive_interval) / 3600; // [sec] -> [hour]
		arrive_time_ref += arrive_interval;
		Job job = new Job(request_node_num, request_time, arrive_time, -1.0, Status.SUBMITTED);

		/**
		 * Debug print code 
		 */
//		System.out.println(job.getId()+","+
//						   index+","+
//						   request_node_num+","+
//						   arrive_time
//						   );

		/**
		 * Debug code : This result is used in 'debug_vis.ipynb'.
		 */
//		try {
//			BufferedWriter bw = new BufferedWriter(new FileWriter("debug.csv", true));
//			bw.write(job.getId()+","+
//					   index+","+
//					   request_node_num+","+
//					   request_time+","+
//					   arrive_time
//					   );
//			bw.newLine();
//			bw.close();
//		} catch(IOException e) {
//			System.out.println(e.getMessage());
//		}
		
		return job;
	}
}
