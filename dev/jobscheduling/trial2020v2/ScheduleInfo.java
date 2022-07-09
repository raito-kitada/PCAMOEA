package jobscheduling.trial2020v2;

import org.apache.commons.lang3.builder.HashCodeBuilder;

public class ScheduleInfo {
	double time;
	int node_num;
	
	public ScheduleInfo() {
	}

	public ScheduleInfo(ScheduleInfo s) {
		this.time = s.time;
		this.node_num = s.node_num;
	}
		
	public ScheduleInfo(double time, int node_num) {
		this.time = time;
		this.node_num = node_num;
	}
	
    @Override
    public int hashCode() {
		return new HashCodeBuilder()
				.append(time)
				.append(node_num)
				.toHashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ScheduleInfo other = (ScheduleInfo) obj;
        if (Double.compare(time, other.time) != 0) {
        	return false;
        }
        if (node_num != other.node_num) {
        	return false;
        }
        return true;
    }
}
