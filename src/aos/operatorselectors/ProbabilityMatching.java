/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aos.operatorselectors;

import aos.nextoperator.AbstractOperatorSelector;
import aos.operator.VariationName;
import lab.moea.history.AOSHistory;
import lab.moea.history.AOSHistoryInfo;
import aos.creditassigment.Credit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.moeaframework.core.PRNG;
import org.moeaframework.core.Variation;

/**
 * Selects operators based on probability which is proportional to the
 * operators credits. Each operator gets selected with a minimum probability
 * of pmin. If current credits in credit repository becomes negative, zero
 * credit is re-assigned to that operator. For the first iteration, operators
 * are selected with uniform probability.
 *
 * @author nozomihitomi
 */
public class ProbabilityMatching extends AbstractOperatorSelector {
    /**
     * Alpha is the adaptation rate
     */
    protected final double alpha;

    /**
     * Hashmap to store the selection probabilities of each operator
     */
    protected HashMap<Variation, Double> probabilities;
    protected HashMap<Variation, Double> probabilities1;
    protected HashMap<Variation, Double> probabilities2;

    protected ArrayList<AOSHistoryInfo> probabilitiesHistory;
    protected ArrayList<AOSHistoryInfo> probabilities1History;
    protected ArrayList<AOSHistoryInfo> probabilities2History;
    protected ArrayList<AOSHistoryInfo> qualities1History;
    protected ArrayList<AOSHistoryInfo> qualities2History;
    
    /**
     * The minimum probability for a operator to be selected
     */
    protected final double pmin;

    /**
     * Constructor to initialize probability map for selection
     *
     * @param operators from which to select from
     * @param pmin The minimum probability for a operator to be selected
     */
    public ProbabilityMatching(Collection<Variation> operators, double alpha, double pmin) {
        super(operators);
        this.alpha = alpha;
        this.probabilities = new HashMap<Variation, Double>();
        this.probabilities1 = new HashMap<Variation, Double>();
        this.probabilities2 = new HashMap<Variation, Double>();
        this.pmin = pmin;
        
        this.probabilitiesHistory = new ArrayList<AOSHistoryInfo>();
        this.probabilities1History = new ArrayList<AOSHistoryInfo>();
        this.probabilities2History = new ArrayList<AOSHistoryInfo>();
        this.qualities1History = new ArrayList<AOSHistoryInfo>();
        this.qualities2History = new ArrayList<AOSHistoryInfo>();
        
        reset();
    }

    @Override
    public String toString() {
        return "ProbabilityMatching";
    }    
    
    /**
     * Will return the next operator that gets selected based on probability
     * proportional to a operators credits. Each operator gets selected with a
     * minimum probability of pmin
     *
     * @return
     */
    @Override
    public Variation nextOperator() {
        double p = PRNG.nextDouble();
        Iterator<Variation> iter = probabilities.keySet().iterator();
        double sum = 0.0;
        Variation operator = null;
        while (iter.hasNext()) {
            operator = iter.next();
            sum += probabilities.get(operator);
            if (sum >= p) {
                break;
            }
        }
        incrementIterations();
        if (operator == null) {
            throw new NullPointerException("No operator was selected by Probability matching operator selector. Check probabilities");
        } else {
            return operator;
        }
    }

    /**
     * calculate the sum of all qualities across the operators
     *
     * @return the sum of the operators' qualities
     */
    protected double sumQualities1() {
        double sum = 0.0;
        Iterator<Variation> iter = qualities1.keySet().iterator();
        while (iter.hasNext()) {
            sum += qualities1.get(iter.next());
        }
        return sum;
    }
    
    /**
     * calculate the sum of all qualities across the operators
     *
     * @return the sum of the operators' qualities
     */
    protected double sumQualities2() {
        double sum = 0.0;
        Iterator<Variation> iter = qualities2.keySet().iterator();
        while (iter.hasNext()) {
            sum += qualities2.get(iter.next());
        }
        return sum;
    }
    
    /**
     * Clears the credit repository and resets the selection probabilities
     */
    @Override
    public void reset() {
        super.reset();
        probabilities.clear();
        probabilities1.clear();
        probabilities2.clear();
        
        Iterator<Variation> iter = operators.iterator();
        while (iter.hasNext()) {
            //all operators get uniform selection probability at beginning
            probabilities.put(iter.next(), 1.0 / (double) operators.size());
        }
    }
    
    /**
     * Updates the qualities of the specified operators
     *  
     * @param reward given to the operator
     * @param operator to be rewarded
     */
    public void updateQualities(Credit reward, Variation operator) {
        double newQuality1 = (1 - alpha) * qualities1.get(operator) + alpha * reward.getValue1();
        double newQuality2 = (1 - alpha) * qualities2.get(operator) + alpha * reward.getValue2();
        qualities1.put(operator, newQuality1);
        qualities2.put(operator, newQuality2);
        checkQuality();    	
    }

    /**
     * Updates the selection probabilities of the operators according to the
     * qualities of each operator.
     */
    @Override
    public void updateProbabilities(){
        double sum1 = sumQualities1();
        double sum2 = sumQualities2();

        // if the credits sum up to zero, apply uniform probability to  operators
        Iterator<Variation> iter = operators.iterator();

        double beta = 0.5;
        
        while (iter.hasNext()) {
            Variation operator_i = iter.next();
            
            double newProb1 = Math.abs(sum1) < Math.pow(10.0, -14) ? 
            	1.0 / (double) operators.size() :
            	pmin + (1 - probabilities.size() * pmin) * (qualities1.get(operator_i) / sum1);

            double newProb2 = Math.abs(sum2) < Math.pow(10.0, -14) ? 
            	1.0 / (double) operators.size() :
            	pmin + (1 - probabilities.size() * pmin) * (qualities2.get(operator_i) / sum2);

            double newProb = beta * newProb1 + (1 - beta) * newProb2;

	        probabilities.put(operator_i, newProb);
            probabilities1.put(operator_i, newProb1);
            probabilities2.put(operator_i, newProb2);
        }
    }
    
    @Override
    public boolean removeOperator(Variation operator) {
        boolean out = super.removeOperator(operator);
        probabilities.remove(operator);
        probabilities1.remove(operator);
        probabilities2.remove(operator);
        return out;
    }
    
    @Override
    public void updateHistories(int numberOfEvaluation){
    	probabilitiesHistory.add(makeHistoryEntry(probabilities, numberOfEvaluation));
    	probabilities1History.add(makeHistoryEntry(probabilities1, numberOfEvaluation));
    	probabilities2History.add(makeHistoryEntry(probabilities2, numberOfEvaluation));
    	qualities1History.add(makeHistoryEntry(qualities1, numberOfEvaluation));
    	qualities2History.add(makeHistoryEntry(qualities2, numberOfEvaluation));    	
    } 
    
    @Override
    public void saveHistories(String path) {
		Set<String> oNames = getOperatorNames();
		
		AOSHistory aosHistory = new AOSHistory();
		aosHistory.setPath(path);
		
		aosHistory.WriteHistory(probabilitiesHistory, oNames, "probabilitiesHistory.csv");
		aosHistory.WriteHistory(probabilities1History, oNames, "probabilities1History.csv");
		aosHistory.WriteHistory(probabilities2History, oNames, "probabilities2History.csv");
		aosHistory.WriteHistory(qualities1History, oNames, "qualities1History.csv");
		aosHistory.WriteHistory(qualities2History, oNames, "qualities2History.csv");
    }
    
	public AOSHistoryInfo makeHistoryEntry(HashMap<Variation, Double> values, int numberOfEvaluation) {
		AOSHistoryInfo entry = new AOSHistoryInfo();
		entry.nfe = numberOfEvaluation;

		entry.value = new HashMap<String, Double>();
		Iterator<Variation> iter = operators.iterator();
		while (iter.hasNext()) {
			Variation operator_i = iter.next();
			double value = values.containsKey(operator_i) ? values.get(operator_i) : 0.0;
			entry.value.put(VariationName.Get(operator_i), value);
		}

		return entry;
	}

}