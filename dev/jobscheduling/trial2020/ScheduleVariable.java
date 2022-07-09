package jobscheduling.trial2020;

/* Copyright 2020 Tomoaki Tatsukawa
*
*/

// import java.util.BitSet;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.moeaframework.core.PRNG;
import org.moeaframework.core.Variable;

import jobscheduling.trial2020.RandomShuffle;
import jobscheduling.trial2020.Utils;

/**
* Decision variable for job.
*/
public class ScheduleVariable implements Variable {

	private static final long serialVersionUID = 10000L;

	/**
	 * The schedule of job execution
	 */
	private int[] schedule;

	/**
	 * Scheduled start time [hour]
	 */
	private double[] start_time;
	
	/**
	 * End time [hour]
	 */
	private double[] end_time;

	/**
	 * The data type of job status
	 */
	public enum Status {
		SUBMITTED, // just submitted, not scheduled
		QUEUED,    // waiting for execution
		RUNNING,   // executing
		FINISH,    // finished on schedule
		ABORT      // aborted for any reason
	}
	
	/**
	 * The job status
	 */
	private Status[] status;

	/**
	 * Constructs a job.
	 * 
	 * @param number_of_jobs 
	 */
	public ScheduleVariable(int number_of_jobs) {
		super();
		
		this.schedule = new int[number_of_jobs];
		this.start_time = new double[number_of_jobs];
		this.end_time = new double[number_of_jobs];
		this.status = new Status[number_of_jobs];
		
		for (Status s: this.status) {
			s = Status.SUBMITTED;
		}
	}
	

	public int getScheduleLength() {
		return schedule.length;
	}
	
	
	public int getSchedule(int index) {
		return schedule[index];
	}


	public void setSchedule(int index, int item) {
		this.schedule[index] = item;
	}

	public void printSchedule() {
		for (int sche: schedule) {
			System.out.print(sche+" ");
		}
		System.out.println("");
	}
		
	public double getStart_time(int index) {
		return start_time[index];
	}

	public void setStart_time(int index, double start_time) {
		this.start_time[index] = start_time;
	}

	public double getEnd_time(int index) {
		return end_time[index];
	}
	
	public double getMax_end_time() {
		double max = end_time[0];
		for (int i=1; i<end_time.length; i++) {
			max = Math.max(max, end_time[i]);
		}
		return max;
	}

	public void setEnd_time(int index, double end_time) {
		this.end_time[index] = end_time;
	}

	public Status getStatus(int index) {
		return status[index];
	}

	public void setStatus(int index, Status status) {
		this.status[index] = status;
	}
		
	@Override
	public ScheduleVariable copy() {
		ScheduleVariable copy = new ScheduleVariable(schedule.length);
		System.arraycopy(schedule, 0, copy.schedule, 0, schedule.length);
		return copy;
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
				.append(schedule)
				.toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		} else if ((obj == null) || (obj.getClass() != getClass())) {
			return false;
		} else {
			ScheduleVariable rhs = (ScheduleVariable)obj;
			
			return new EqualsBuilder()
					.append(schedule, rhs.schedule)
					.isEquals();
		}
	}

	@Override
	public void randomize() {
		Utils.seq(schedule);
		RandomShuffle.execute(schedule);
	}

}



