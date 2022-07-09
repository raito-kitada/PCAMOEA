package jobscheduling.trial2020v2;

/* Copyright 2020 Tomoaki Tatsukawa
*
*/

// import java.util.BitSet;

import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
//import org.moeaframework.core.PRNG;
import org.moeaframework.core.Variable;

/**
* Decision variable for job.
*/
public class ScheduleVariable implements Variable {

	private static final long serialVersionUID = 10000L;
	
	/**
	 * Base data for schedule
	 */
	private static List<Integer> schedule_ref;

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
	 * Constructs a job.
	 * 
	 * @param number_of_jobs 
	 */
	public ScheduleVariable() {
		super();
		
		int number_of_jobs = schedule_ref.size();
		this.schedule = new int[number_of_jobs];
		this.start_time = new double[number_of_jobs];
		this.end_time = new double[number_of_jobs];
	}
	
	public static void setScheduleRef(List<Integer> sref) {
		schedule_ref = sref;
	}
	
	public static List<Integer> getScheduleRef() {
		return schedule_ref;
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
		for (Integer sche: schedule) {
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

	
	@Override
	public ScheduleVariable copy() {
		ScheduleVariable copy = new ScheduleVariable();
		System.arraycopy(schedule, 0, copy.schedule, 0, schedule.length);
		return copy;
	}
	
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
				.append(schedule)
				.append(start_time)
				.append(end_time)
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
		schedule = new int[schedule_ref.size()];
		for(int i=0; i<schedule.length; i++) {
			schedule[i] = schedule_ref.get(i);
		}

		RandomShuffle.execute(schedule);
	}

	public void seq() {
		schedule = new int[schedule_ref.size()];
		for(int i=0; i<schedule.length; i++) {
			schedule[i] = schedule_ref.get(i);
		}
	}
	
}



