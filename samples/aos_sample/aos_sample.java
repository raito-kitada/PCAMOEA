package aos_sample;

import java.lang.reflect.Field;

import org.moeaframework.util.TypedProperties;
import lab.moea.util.Util;

public class aos_sample {

	private static String MAIN_PROPERTY_PATH = "main.properties";
	
	public static void main(String[] args) {
		ignoreJava9Warning();
		
		String property_path = (args.length == 1) ? args[0] : "";
		
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
	
	@SuppressWarnings("restriction")
	public static void ignoreJava9Warning() {
	  try {
	    Field theUnsafe = sun.misc.Unsafe.class.getDeclaredField("theUnsafe");
	    theUnsafe.setAccessible(true);
	    sun.misc.Unsafe u = (sun.misc.Unsafe) theUnsafe.get(null);
	    Class<?> cls = Class.forName("jdk.internal.module.IllegalAccessLogger");
	    Field logger = cls.getDeclaredField("logger");
	    u.putObjectVolatile(cls, u.staticFieldOffset(logger), null);
	  } catch (Exception e) {
	    // Java9以前では例外
	  }
	}
}
