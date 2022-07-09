/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aos.operatorselectors;

import java.util.Collection;
import java.util.Iterator;

import org.moeaframework.core.Variation;

/**
 * Adaptive pursuit algorithm is based on Thierens, D. (2005). An adaptive
 * pursuit strategy for allocating operator probabilities. Belgian/Netherlands
 * Artificial Intelligence Conference, 385?386. doi:10.1145/1068009.1068251
 *
 * @author nozomihitomi
 */
public class AdaptivePursuit extends ProbabilityMatching {

    /**
     * The maximum probability that the heuristic with the highest credits can
     * be selected. It is implicitly defined as 1.0 - m * pmin where m is the
     * number of operators used and pmin is the minimum selection probability
     */
    private final double pmax;

    /**
     * The Learning Rate
     */
    private final double beta;

    /**
     * Constructor to initialize adaptive pursuit map for selection. The maximum
     * selection probability is implicitly defined as 1.0 - m*pmin where m is
     * the number of operators defined in the given credit repository and pmin
     * is the minimum selection probability
     *
     * @param operators from which to select from 
     * @param alpha the adaptation rate
     * @param beta the learning rate
     * @param pmin the minimum selection probability
     */
    public AdaptivePursuit(Collection<Variation> operators, double alpha, double beta, double pmin) {
        super(operators, alpha, pmin);
        this.pmax = 1 - (operators.size() - 1) * pmin;
        this.beta = beta;
        if (pmax < pmin) {
            throw new IllegalArgumentException("the implicit maxmimm selection "
                    + "probability " + pmax + " is less than the minimum selection probability " + pmin);
        }
        reset();
    }
    
    @Override
    public String toString() {
        return "AdaptivePursuit";
    }    
        
    /**
     * Updates the selection probabilities of the operators according to the
     * qualities of each operator.
     */
    @Override
    public void updateProbabilities(){
    	double sum1 = sumQualities1();
        double sum2 = sumQualities2();

        Iterator<Variation> iter = operators.iterator();
                
        Variation leadOperator1 = argMax1(qualities1.keySet());
        Variation leadOperator2 = argMax2(qualities2.keySet());

        double alpha = 0.5;
        while (iter.hasNext()) {
            Variation operator_i = iter.next();
            
            double newProb1 = Math.abs(sum1) < Math.pow(10.0, -14) ?
            		1.0 / (double) operators.size() :
            		pmin + (1 - probabilities.size() * pmin) * (qualities1.get(operator_i) / sum1);
            
            double newProb2 = Math.abs(sum2) < Math.pow(10.0, -14) ?
            		1.0 / (double) operators.size() :
            		pmin + (1 - probabilities.size() * pmin) * (qualities2.get(operator_i) / sum2);

            newProb1 = (operator_i == leadOperator1) ?
            		newProb1 + beta * (pmax - newProb1):
            		newProb1 + beta * (pmin - newProb1);            
            
            newProb2 = (operator_i == leadOperator2) ?
            		newProb2 + beta * (pmax - newProb2):
           			newProb2 + beta * (pmin - newProb2);
            
            double newProb = alpha * newProb1 + (1 - alpha) * newProb2;

            probabilities.put(operator_i, newProb);                
        }
   }

    /**
     * Want to find the operator that has the maximum quality
     *
     * @param operator
     * @return the current quality of the specified operator
     */
    @Override
    protected double function2maximize1(Variation operator) {
    	return qualities1.get(operator);
    }
    protected double function2maximize2(Variation operator) {
        return qualities2.get(operator);
    }    
}