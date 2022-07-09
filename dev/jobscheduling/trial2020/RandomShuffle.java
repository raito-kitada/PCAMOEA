package jobscheduling.trial2020;

import org.moeaframework.core.PRNG;

public class RandomShuffle {
	public static void execute(int[] list) {
		for(int i = list.length - 1; i >= 0; i--) {
			int tmp = list[i];

			int idx = PRNG.nextInt(list.length);
			list[i] = list[idx];
			list[idx] = tmp;
		}		
	}
}
