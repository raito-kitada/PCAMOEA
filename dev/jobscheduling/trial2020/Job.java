package jobscheduling.trial2020;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class Job {
	/**
	 * Shared variables to generate unique IDs in this class
	 */
	private static int id_base = 0;

	/**
	 * Job ID which is unique in the scheduler
	 */
	private final int id;

	/**
	 * The number of request node
	 */
	private final int request_node_num;

	/**
	 * The request time [hour]
	 */
	private final double request_time;

	/**
	 * Submitted time [hour]
	 */
	private final double arrive_time;

	/**
	 * Constructs a job.
	 * 
	 * @param req_node_num 
	 * @param req_time 
	 * @param arrive_time
	 */
	public Job(int request_node_num, double request_time, double arrive_time) {
		this.id = id_base;
		this.request_node_num = request_node_num;
		this.request_time = request_time;
		this.arrive_time = arrive_time;

		id_base++;
	}

	public int getId() {
		return id;
	}

	public int getReq_node_num() {
		return request_node_num;
	}

	public double getReq_time() {
		return request_time;
	}

	public double getArrive_time() {
		return arrive_time;
	}

	public void print() {
		System.out.println("JOB : ID=" + id + " Node=" + request_node_num+" Request_Time[hour]="+request_time + " Arrived_Time[sec]="+arrive_time * 3600);
	}


	public Job copy() {
		Job copy = new Job(request_node_num, request_time, arrive_time);
		return copy;
	}
	
	public int hashCode() {
		return new HashCodeBuilder()
				.append(id)
				.append(request_node_num)
				.append(request_time)
				.append(arrive_time)
				.toHashCode();
	}

	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		} else if ((obj == null) || (obj.getClass() != getClass())) {
			return false;
		} else {
			Job rhs = (Job)obj;
			
			return new EqualsBuilder()
					.append(id, rhs.id)
					.isEquals();
		}
	}

}
