package aos.operator;

import org.moeaframework.core.Variation;
import org.moeaframework.core.operator.CompoundVariation;

/*
 * This class is a helper class to extract the name of variation.
 * @author tatsukawa
 */
public class VariationName {
	public static String Get(Variation v) {
        String name;
        if (v instanceof CompoundVariation) {
        	name = ((CompoundVariation)v).getName();
        } else {
            String[] operatorName = v.toString().split("operator.");
            String[] splitName = operatorName[operatorName.length - 1].split("@");
            name = splitName[0];
        }           	
		return name;
	}
}
