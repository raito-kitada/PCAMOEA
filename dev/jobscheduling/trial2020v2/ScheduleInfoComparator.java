package jobscheduling.trial2020v2;

import java.util.Comparator;

public class ScheduleInfoComparator implements Comparator<ScheduleInfo>{
	@Override
	public int compare(ScheduleInfo p1, ScheduleInfo p2) {
		if (Double.compare(p1.time, p2.time) == 0) {
			return 0;
		} else {
			return p1.time < p2.time ? -1 : 1;
		}
	}
}
