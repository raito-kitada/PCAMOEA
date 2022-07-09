package jobscheduling.trial2020v2;

import java.util.List;
import java.util.ArrayList;

import org.moeaframework.core.PRNG;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variation;

public class OX implements Variation {
	/**
	 * The probability of applying this OX operator.
	 */
	private final double probability;
	
	public OX(double probability) {
		super();
		this.probability = probability;
	}
	
	public double getProbability() {
		return probability;
	}

	@Override
	public int getArity() {
		return 2;
	}

	@Override
	public Solution[] evolve(Solution[] parents) {
		Solution result1 = parents[1].copy();
		Solution result2 = parents[0].copy();

		ScheduleVariable sv1 = (ScheduleVariable)result1.getVariable(0);
		ScheduleVariable sv2 = (ScheduleVariable)result2.getVariable(0);
		
		int nsche = sv1.getScheduleLength();
		
		if (PRNG.nextDouble() <= probability) {
			// generate two crossover points. cp1, cp2 in [0,nsche]
			// cp2 must larger than cp1
			int cp1 = PRNG.nextInt(nsche);
			int cp2 = PRNG.nextInt(nsche);
			while (cp1 == cp2) {
				cp2 = PRNG.nextInt(nsche);
			}
			if (cp1 > cp2) {
				int tmp = cp1;
				cp1 = cp2;
				cp2 = tmp;
			}
			
//			System.out.println("cp1="+cp1+" cp2="+cp2);
			
			// [cp1, cp2] : keep
			// [0,cp1), (cp2, nsche) : crossover
			// 
			List<Integer> remain1 = new ArrayList<Integer>();
			List<Integer> remain2 = new ArrayList<Integer>();
			for (int i=cp2+1; i<nsche; i++) {
				remain1.add(sv2.getSchedule(i));
				remain2.add(sv1.getSchedule(i));
			}
			for (int i=0; i<cp2+1; i++) {
				remain1.add(sv2.getSchedule(i));
				remain2.add(sv1.getSchedule(i));
			}
			for (int i=cp1; i<=cp2; i++) {
				int ret1 = remain1.indexOf(sv1.getSchedule(i));
				int ret2 = remain2.indexOf(sv2.getSchedule(i));
				if (ret1 != -1) {
					remain1.remove(ret1);
				}
				if (ret2 != -1) {
					remain2.remove(ret2);
				}
			}
			
			int index = (cp2 + 1 == nsche) ? 0 : cp2 + 1;
			for (int i=0; i<remain1.size(); i++) {
				sv1.setSchedule(index, remain1.get(i));
				sv2.setSchedule(index, remain2.get(i));
				
				index = (index + 1 == nsche) ? 0 : index+1; 
			}
		}
		
		result1.setVariable(0, sv1);
		result2.setVariable(0, sv2);

		return new Solution[] { result1, result2 };
	}
}
