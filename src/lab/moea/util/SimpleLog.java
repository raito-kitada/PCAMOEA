package lab.moea.util;

import java.util.Map;
import java.util.Properties;

import org.moeaframework.util.TypedProperties;

public class SimpleLog {
	/**
	 * Print string as information
	 * 
	 * @param string
	 */
	public static void Info(String string) {
		System.out.println(string);
	}

	/**
	 * Print string as debug information
	 * 
	 * @param string
	 */
	public static void Debug(String string) {
		System.out.println("[Debug] " + string);
	}

	/**
	 * Print loaded properties information
	 * 
	 * @param prop TypedProperties for print
	 */
	public static void printParameters(TypedProperties prop) {
		Properties p = prop.getProperties();
		
        for(Map.Entry<Object, Object> e : p.entrySet()) {
            System.out.println(e.getKey().toString() + " = " + e.getValue().toString());
        }
	}
}
