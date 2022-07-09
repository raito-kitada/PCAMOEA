package pcaec_raito;

import org.moeaframework.util.TypedProperties;
import lab.moea.util.Util;

public class pcaec_raito {

	private static String MAIN_PROPERTY_PATH = "main.properties";
	
	public static void main(String[] args) {
		String property_path = (args.length == 1) ? args[0] : "";
		
		solveProblem spm = new solveProblem();

		TypedProperties prop = Util.loadParameters(property_path + "/" + MAIN_PROPERTY_PATH);
		prop.getProperties().setProperty("PROPERTY_PATH", property_path);

		int ntrial = prop.getInt("number_of_trials", 1);
		
		String[] problemNames   = prop.getStringArray("problems", new String[] {"DTLZ2"});
		String[] algorithmNames = prop.getStringArray("algorithms", new String[] {"NSGAII"});
		String[] nelems = prop.getStringArray("nelems", new String[] {"1.0"});
		for (int trial=0; trial<ntrial; trial++) {
			for (String pName : problemNames) {
		     	for (String aName : algorithmNames) {
			     		spm.opt(aName, pName, trial, prop);
		     	}
		     }	
		}
		
		System.out.println("Finish");
	}

}