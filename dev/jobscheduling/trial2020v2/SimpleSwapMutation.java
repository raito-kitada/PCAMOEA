package jobscheduling.trial2020v2;

import org.moeaframework.core.PRNG;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variation;

public class SimpleSwapMutation implements Variation {
	/**
	 * The probability this operator is applied to each decision variable.
	 */
	private final double probability;

	public SimpleSwapMutation(double probability) {
		super();
		this.probability = probability;
	}

	public double getProbability() {
		return probability;
	}

	@Override
	public Solution[] evolve(Solution[] parents) {
		Solution result = parents[0].copy();

		ScheduleVariable sv = (ScheduleVariable)result.getVariable(0);
		//sv.printSchedule();

		int nsche = sv.getScheduleLength();
		for (int index1 = 0; index1 < nsche; index1++) {
			if ((PRNG.nextDouble() <= probability)) {
				// Generate two random index
				int index2 = PRNG.nextInt(nsche);
				while (index1 == index2) {
					index2 = PRNG.nextInt(nsche);
				}
				
				// get schedules
				int sche1 = sv.getSchedule(index1);
				int sche2 = sv.getSchedule(index2);

				// swap schedules
				sv.setSchedule(index1, sche2);
				sv.setSchedule(index2, sche1);
			}
		}

		result.setVariable(0, sv);

		//sv.printSchedule();
		return new Solution[] { result };
	}

	@Override
	public int getArity() {
		return 1;
	}

}
