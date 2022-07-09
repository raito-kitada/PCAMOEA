package jobscheduling.trial2020;

public class Utils {
	/**
	 * Make list with sequence of numbers from 0 to (list size - 1). 
	 * @param list : list to be initialized with sequence of numbers
	 */
	public static void seq(int[] list) {
		for(int i = list.length - 1; i >= 0; i--) {
			list[i] = i;
		}
	}
}
