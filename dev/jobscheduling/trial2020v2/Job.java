package jobscheduling.trial2020v2;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class Job {
	/**
	 * ID reference to generate unique IDs in this class
	 */
	private static int id_ref = 0;

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
	 * Start time [hour]
	 */
	private double start_time;
	
	/**
	 * End time [hour]
	 */
	private double end_time;
	
	/**
	 * The data type of job status
	 */
	public enum Status {
		SUBMITTED, // just submitted, not scheduled
		QUEUED,    // scheduled, waiting for execution
		RUNNING,   // executing
		FINISH,    // finished on schedule
		ABORT      // aborted for any reason
	}
	
	/**
	 * The job status
	 */
	private Status status;

	/**
	 * Constructs a job.
	 * 
	 * @param req_node_num 
	 * @param req_time 
	 * @param arrive_time
	 */
	public Job(int request_node_num, double request_time, double arrive_time, double start_time, Status status) {
		this.id = id_ref;
		this.request_node_num = request_node_num;
		this.request_time = request_time;
		this.arrive_time = arrive_time;
		this.start_time = start_time;
		this.end_time = start_time + request_time;
		this.status = status;

		id_ref++;
	}

	public static void reset() {
		id_ref = 0;
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

	public double getStart_time() {
		return start_time;
	}

	public double getEnd_time() {
		return end_time;
	}

	public Status getStatus() {
		return status;
	}
	
	public void setStart_time(double start_time) {
		this.start_time = start_time;
		end_time = start_time + request_time;
		status = Status.QUEUED;
	}
		
	public boolean isOptTarget(double current_time) {
		if (status == Status.SUBMITTED) {
			if (arrive_time < current_time) {
				return true;
			}
		}
		return false;
	}
		
	public boolean isRunningNow() {
		return (status == Status.RUNNING) ?  true : false;
	}
	
	public boolean isRunning(double current_time) {
		return (start_time >= 0 && start_time <= current_time && current_time < end_time) ? true : false;
	}
	
	public void Abort() {
		status = Status.ABORT;
	}
	
	public void updateStatus(double current_time) {
		switch (status) {
		case QUEUED:
			if (start_time < current_time) {
				status = Status.RUNNING;
			}
			break;
		case RUNNING:
			if (end_time < current_time) {
				status = Status.FINISH;
			}
			break;
		default:
			break;
		}
		
	}
	
	public void print() {
		System.out.format("JOB: ID=%5d Node=%4d RTime[h]=%6.1f ATime[s]=%10.3f STime[h]=%6.3f ETime[h]=%6.3f Status=%s ",
				id, request_node_num, request_time, arrive_time*3600, start_time, end_time, status);
	}

	public Job copy() {
		Job copy = new Job(request_node_num, request_time, arrive_time, start_time, status);
		return copy;
	}
	
	public int hashCode() {
		return new HashCodeBuilder()
				.append(id)
				.append(request_node_num)
				.append(request_time)
				.append(arrive_time)
				.append(start_time)
				.append(end_time)
				.append(status)
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
