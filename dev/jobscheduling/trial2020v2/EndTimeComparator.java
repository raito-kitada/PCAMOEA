package jobscheduling.trial2020v2;

import java.util.Comparator;

public class EndTimeComparator implements Comparator<Integer>{
	private ScheduleVariable sv;
	
	public EndTimeComparator(ScheduleVariable sv) {
		this.sv = sv;
	}

	@Override
	public int compare(Integer p1, Integer p2) {
		return sv.getEnd_time(p1) < sv.getEnd_time(p2) ? -1 : 1;
	}
}
