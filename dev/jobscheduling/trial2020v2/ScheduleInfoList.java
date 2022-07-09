package jobscheduling.trial2020v2;

import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

public class ScheduleInfoList {
	private TreeSet<ScheduleInfo> schedule_infos;
	
	public ScheduleInfoList() {
		schedule_infos = new TreeSet<ScheduleInfo>(new ScheduleInfoComparator());
		schedule_infos.add(new ScheduleInfo(0.0, 0));
	}
	
	public ScheduleInfoList(ScheduleInfoList src) {
		schedule_infos = new TreeSet<ScheduleInfo>(new ScheduleInfoComparator());
		
        Iterator<ScheduleInfo> iterator = src.iterator();

        while(iterator.hasNext()) {
        	ScheduleInfo schedule_info = iterator.next();
        	schedule_infos.add(new ScheduleInfo(schedule_info));
        }
	}
		
	public boolean set(double time) {
		ScheduleInfo schedule_info = getElement(time);
		if (schedule_info ==  null) {
			schedule_info = new ScheduleInfo(time, 0);
			return schedule_infos.add(schedule_info);
		}
		
		return false;
	}
	
	public boolean set(ScheduleInfo s) {
		ScheduleInfo schedule_info = new ScheduleInfo(s.time, s.node_num);
		return schedule_infos.add(schedule_info);
	}
	
	public boolean set(double time, int node_num) { // data is overwritten.
		ScheduleInfo schedule_info = getElement(time);
		if (schedule_info !=  null) {
			schedule_info.node_num = node_num;
			return true;
		} else {
			schedule_info = new ScheduleInfo(time, node_num);
			return schedule_infos.add(schedule_info);
		}
	}
	
	public boolean add(double time, int node_num) {
		ScheduleInfo schedule_info = getElement(time);
		if (schedule_info !=  null) {
			schedule_info.node_num += node_num;
			return true;
		} else {
			schedule_info = new ScheduleInfo(time, node_num);
			return schedule_infos.add(schedule_info);
		}
	}	
	
	public void print() {
		for (ScheduleInfo si : schedule_infos) {
			System.out.format("time=%6.3f node_num=%3d\n", si.time, si.node_num);
		}
	}
	
	public int size() {
		return schedule_infos.size();
	}
	
	public ScheduleInfo getElement(double time) {
        Iterator<ScheduleInfo> iterator = schedule_infos.iterator();

        while(iterator.hasNext()) {
        	ScheduleInfo schedule_info = iterator.next();
        	
        	int ret = Double.compare(schedule_info.time, time);
            if (ret == 0) {
            	return schedule_info;
            }
        }
        
        return null;
	}
	
	public ScheduleInfo findNearestElement(double time) {
        Iterator<ScheduleInfo> iterator = schedule_infos.iterator();
        
        ScheduleInfo s = new ScheduleInfo(0, 0);
        
        while(iterator.hasNext()) {
        	ScheduleInfo schedule_info = iterator.next();
        	int ret = Double.compare(schedule_info.time, time);
            if (ret <= 0) {
            	s = new ScheduleInfo(schedule_info);
            } else if (ret > 0) {
            	return s;
            }
        }
        
        return s;
	}
	
	public ScheduleInfo getScheduleInfo(double time, boolean time_is_overwrite) {
        Iterator<ScheduleInfo> iterator = schedule_infos.iterator();
        
        ScheduleInfo s = new ScheduleInfo(0, 0);
        
        while(iterator.hasNext()) {
        	ScheduleInfo schedule_info = iterator.next();
        	int ret = Double.compare(schedule_info.time, time);
            if (ret <= 0) {
            	s = new ScheduleInfo(schedule_info);
            	if (time_is_overwrite) s.time = time;
            } else if (ret > 0) {
            	return s;
            }
        }
        
        return s;
	}
	
	
	public boolean isAvailable(double start_time, double end_time, int node_num, int max_node) {
		ScheduleInfo start = getScheduleInfo(start_time, false);
		ScheduleInfo end = getScheduleInfo(end_time, true);
		
		SortedSet<ScheduleInfo> subset = schedule_infos.subSet(start, end);
		
		Iterator<ScheduleInfo> iterator = subset.iterator();
		boolean is_available = true;
		while (iterator.hasNext()) {
			ScheduleInfo schedule_info = iterator.next();
			is_available = is_available && (schedule_info.node_num + node_num <= max_node);
		}
		
		return is_available;
	}
	
	public double findStartTime(double start_time, int node_num, double request_time, int max_node) {
		double end_time = start_time + request_time;

		while (!isAvailable(start_time, end_time, node_num, max_node)) {
			ScheduleInfo next = schedule_infos.higher(new ScheduleInfo(start_time, 0));

			if (next != null) {
				start_time = next.time;
			} else {
				start_time = schedule_infos.last().time;
				if (schedule_infos.last().node_num > 0) {
					System.out.println("Error");					
					print();					
					System.out.println("Error");
				}
			}
			
			end_time = start_time + request_time;
		}
		
		return start_time;
	}
	
	public void update(double start_time, double end_time, int node_num) {
		ScheduleInfo start = getScheduleInfo(start_time, true);
		ScheduleInfo end = getScheduleInfo(end_time, true);

		set(start_time, start.node_num);
		set(end_time, end.node_num);

		SortedSet<ScheduleInfo> subset = schedule_infos.subSet(start, end);
		Iterator<ScheduleInfo> iterator = subset.iterator();
		while (iterator.hasNext()) {
			ScheduleInfo schedule_info = iterator.next();
			schedule_info.node_num += node_num;
			if(schedule_info.node_num > 384) {
				System.out.println("!Error!");
				print();
				System.out.println("!Error!");
			}
		}
	}
	
	public Iterator<ScheduleInfo> iterator() {
		return schedule_infos.iterator();
	}
}
	