package adaptivesearch.cpsd;

import org.moeaframework.util.TypedProperties;

import lab.moea.util.Util;

public class CPSDMain {

	private static String MAIN_PROPERTY_PATH = "main.properties";
	
	public static void main(String[] args) {
		String property_path = (args.length == 1) ? args[0] : "";
		
		solveCPSD spm = new solveCPSD();
		
		String directoryName = System.getProperty("user.dir");
		System.out.println("Current Working Directory is = " +directoryName);

		TypedProperties prop = Util.loadParameters(property_path + "/" + MAIN_PROPERTY_PATH);
		prop.getProperties().setProperty("PROPERTY_PATH", property_path);

		int ntrial = prop.getInt("number_of_trials", 1);
		
		String[] problemNames   = prop.getStringArray("problems", new String[] {"DTLZ2"});
		String[] algorithmNames = prop.getStringArray("algorithms", new String[] {"NSGAII"});
		int[] ndigits = prop.getIntArray("ndigits", new int[] {2});
		
		for (int trial=0; trial<ntrial; trial++) {
			for (String pName : problemNames) {
		     	for (String aName : algorithmNames) {
		     		for (int ndigit : ndigits) {
			     		spm.opt(aName, pName, ndigit, trial, prop);
		     		}
		     	}
			}
		}
		
		System.out.println("Finish");
	}

}