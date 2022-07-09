package adaptivesearch.cpsd;

import lab.moea.util.Util;

public class CPSDUtil extends Util{

	public static void print(String algorithm, String problem, int trial, int digit, long seed) {
		System.out.println(algorithm + " - " + problem + " [ " + trial + " ] " + digit + " digit, seed = " + seed);
	}
	
	public static String makeFileName(String header, String algorithm, String problem, int trial, String extention) {
		if (header == "") {
			return algorithm+"_"+problem+"_"+trial+"." + extention;
		} else {
			return header+"_"+algorithm+"_"+problem+"_"+trial+"." + extention;
		}
	}
}
