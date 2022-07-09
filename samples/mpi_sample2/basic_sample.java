package mpi_sample2;

import org.moeaframework.util.TypedProperties;
import lab.moea.util.Util;

public class basic_sample {

	private static String MAIN_PROPERTY_PATH = "main.properties";
	
	public static void main(String[] args) {
		String property_path = (args.length == 1) ? args[0] : "";
		
		String directoryName = System.getProperty("user.dir");
		System.out.println("Current Working Directory is = " +directoryName);
		
		solveProblem spm = new solveProblem();

		TypedProperties prop = Util.loadParameters(property_path + "/" + MAIN_PROPERTY_PATH);
		prop.getProperties().setProperty("PROPERTY_PATH", property_path);

		int ntrial = prop.getInt("number_of_trials", 1);
		
		String[] problemNames   = prop.getStringArray("problems", new String[] {"DTLZ2"});
		String[] algorithmNames = prop.getStringArray("algorithms", new String[] {"NSGAII"});
		
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