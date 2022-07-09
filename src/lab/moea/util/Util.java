package lab.moea.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import javax.swing.JFrame;

import org.moeaframework.analysis.collector.Accumulator;
import org.moeaframework.analysis.plot.Plot;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Solution;
import org.moeaframework.util.TypedProperties;

public class Util {
	/**
	 * a flag for debugging
	 */
	private static boolean DEBUG = false;

	/**
	 * Setter of DEBUG flag
	 */
	public static void setDebug(boolean debug) {
		Util.DEBUG = debug;
	}
	
	/**
	 * Construct PF file in pf/ directory
	 * Currently DTLZ and WFG problems are only supported.
	 * 
	 * @param problem String of problem name
	 * @param nobj The number of objectives
	 * @return name of PF file
	 */
	public static String makePFName(String problem, int nobj) {
		problem = problem.toUpperCase();

		if (problem.startsWith("DTLZ")) {
			return "pf/"+ problem + "." + nobj + "D.pf";
		} else if (problem.startsWith("WFG")){
			return "pf/"+ problem + "." + nobj + "D.pf";
		} else {
			return null;
		}
	}
	
	/**
	 * Construct the output file name
	 * 
	 * @param header String to prefix a file name
	 * @param extension String of file extension. Dot is required. (e.g. ".txt", ".csv")
	 * @param strings Additional strings to add to a file name. It is connected by underscore.
	 * @return name of the file name
	 */
	public static String makeFileName(String header, String extension, String... strings) {
		String name = header;
		for (String string : strings) {
			name += "_" + string;
		}
		name += extension;
		
		return name;
	}

	/**
	 * Scatter plot for final result.
	 * 
	 * @param result the final result which is an instance of NondominatedPopulation class.
	 * @param label the label of result
	 * @param title the title of result
	 * @param is_pause_and_close if true, it will pause until enter key is pressed after rendering, 
	 *                           and then close the plot window after resuming. 
	 */
	public static void Plot(NondominatedPopulation result, String label, String title, boolean is_pause_and_close) {
		JFrame frame = new Plot()
		.add(label, result)
		.setTitle(title)
		.show();
		
		if (is_pause_and_close) {
			Util.Pause();
			Plot.close(frame);			
		}
    }	
	
	/**
	 * Scatter plot of 2 solution sets with labels. It's used when debugging. 
	 * DEBUG flag is set by setDebug function.
	 * 
	 * @param s1 1st solution set
	 * @param s2 2nd solution set
	 * @param s1label a label of 1st solution set
	 * @param s2label a label of 2nd solution set
	 */
	public static void DebugPlot(Solution[] s1, Solution[] s2, String s1label, String s2label) {
    	if (DEBUG) {
			JFrame frame = new Plot()
			.add(s1label, s1)
			.add(s2label, s2)
			.show();
			
			Util.Pause();

			Plot.close(frame);			
    	}
    }
	
	/**
	 * Print runtime dynamics. Elapsed time and generational distance are required.
	 *  
	 * @param accumulator
	 * @param printAll If true, print all history. if false, only print last history.
	 */
	public static void printRuntimeDynamics(Accumulator accumulator, boolean printAll) {
		System.out.format("  NFE    Time      Generational Distance%n");

		if (printAll) {
			for (int i=0; i<accumulator.size("NFE"); i++) {
				System.out.format("%5d    %-8.4f  %-8.4f%n",
						accumulator.get("NFE", i),
						accumulator.get("Elapsed Time", i),
						accumulator.get("GenerationalDistance", i));
			}
		} else {
			int[] is = {0, accumulator.size("NFE") / 2, accumulator.size("NFE") - 1};
			for (int i : is) {
				System.out.format("%5d    %-8.4f  %-8.4f%n",
						accumulator.get("NFE", i),
						accumulator.get("Elapsed Time", i),
						accumulator.get("GenerationalDistance", i));
			}
		}
	}
	
	/**
	 * Pause execution until the enter key is pressed
	 */
	public static void Pause() {
		@SuppressWarnings("resource")
		Scanner scanner = new Scanner(System.in);
		System.out.println("To continue, please press enter key...");
		scanner.nextLine();
	}
	
	/**
	 * Load parameters from property file
	 * 
	 * @param path property file path
	 * @param prop 
	 * @return Properties
	 */
	public static TypedProperties loadParameters(String path, TypedProperties prop) {
		try {
			InputStream is = new FileInputStream(path);
			prop.getProperties().load(is);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		}
		
		return prop;
	}	
	
	/**
	 * Load parameters from property file
	 * 
	 * @param path property file path
	 * @return Properties
	 */
	public static TypedProperties loadParameters(String path) {
		return loadParameters(path, new TypedProperties());
	}
}
