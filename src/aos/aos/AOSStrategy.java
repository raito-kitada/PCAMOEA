 //* To change this license header, choose License Headers in Project Properties.
package aos.aos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Population;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variation;

import aos.creditassigment.Credit;
import aos.creditassigment.ICreditAssignment;
import aos.nextoperator.IOperatorSelector;
import aos.operator.VariationName;
import lab.moea.history.AOSHistoryInfo;
import lab.moea.history.AOSHistory;
import lab.moea.util.Util;
/**
 * The adaptive operator strategy that consists of a credit assignment and
 * operator selection strategy. The credit assignment strategy defines when and
 * how an operator is rewarded. The operator selection strategy uses the credits
 * to determine which operator to select next
 *
 * @author nozomihitomi
 */
public class AOSStrategy {

    private final String nfeStr = "NFE"; // Number of Function Evaluation
    
    private final String iteStr = "Iteration"; // Iteration Number

    private final String creatorStr = "operator";

    /**
     * The credit assignment strategy
     */
    private final ICreditAssignment creditAssignment1;
    private final ICreditAssignment creditAssignment2;

    /**
     * The operator selection strategy
     */
    private final IOperatorSelector operatorSelector;

    /**
     * The current paretofront
     */
    private NondominatedPopulation paretofront;
    
    private int numberOfEvaluation;
    
    private Population population;
    private NondominatedPopulation archive;
    
    protected ArrayList<AOSHistoryInfo> credits1History;
    protected ArrayList<AOSHistoryInfo> credits2History;
    
    protected AOSHistory aosHistory;
    
    /**
     * Creates the AOS strategy with a credit assignment and operator selection
     * strategy
     *
     * @param creditAssignment credit assignment strategy
     * @param operatorSelection operator selection strategy
     */
    public AOSStrategy(ICreditAssignment creditAssignment1,ICreditAssignment creditAssignment2, IOperatorSelector operatorSelector) {
        this.creditAssignment1 = creditAssignment1;
        this.creditAssignment2 = creditAssignment2;
        this.operatorSelector = operatorSelector;
        this.paretofront = new NondominatedPopulation();
        this.population = null;
        this.archive = null;
        
        this.credits1History = new ArrayList<AOSHistoryInfo>();
        this.credits2History = new ArrayList<AOSHistoryInfo>();
        
        this.aosHistory = new AOSHistory();
    }

    public void setHistoryPath(String path) {
        this.aosHistory.setPath(path);
    }
    
    /**
     * gets the credit assignment strategy
     * @return the credit assignment strategy
     */
    public ICreditAssignment getCreditAssignment1() {
        return creditAssignment1;
    }
    public ICreditAssignment getCreditAssignment2() {
        return creditAssignment2;
    }
    /**
     * gets the operator selection strategy
     * @return the operator selection strategy
     */
    public IOperatorSelector getOperatorSelector() {
        return operatorSelector;
    }
    
    public Variation nextOperator() {
        return operatorSelector.nextOperator();
    }
    
    public void initialize(Population population, NondominatedPopulation archive) {
    	paretofront.addAll(population);
    	this.population = population;
    	this.archive = archive;
    	
    	for (Solution solution : this.population) {
    		solution.setAttribute(nfeStr, population.size());
    		solution.setAttribute(iteStr, 0);
    		solution.setAttribute(creatorStr, "");
    	}

        operatorSelector.updateProbabilities();
        operatorSelector.updateHistories(numberOfEvaluation);
    }
    
    public void setPopulation(Population population) { 
    	this.population = population;
    }
    
    public void setArchive(NondominatedPopulation archive) { 
    	this.archive = archive;
    }

    public void computeCreditAndReward(Solution[] parents, Solution[] offsprings, Variation operator) {
		Set<String> oNames = operatorSelector.getOperatorNames();        

    	//set attributes to all newly created offspring
        for (Solution offspring : offsprings) {
        	offspring.setAttribute(nfeStr, numberOfEvaluation);
        	offspring.setAttribute(iteStr, operatorSelector.getNumberOfIterations());
        	offspring.setAttribute(creatorStr, VariationName.Get(operator));
        }

        // update PF
        paretofront.addAll(offsprings);
        //paretofront = new NondominatedPopulation(population);

        // compute credit assignments
        Map<String, Double> credits1 = creditAssignment1.compute(
                offsprings, parents, population, paretofront, archive, oNames);
        
        Map<String, Double> credits2 = creditAssignment2.compute(
                offsprings, parents, population, paretofront, archive, oNames);
        
        // DEBUG
        Util.DebugPlot(parents, offsprings, "parent", "offsprings");
        
        // update qualities and probabilities
        for (String oName : oNames) {
        	double credit1 = credits1.containsKey(oName) ? credits1.get(oName) : 0.0;
        	double credit2 = credits2.containsKey(oName) ? credits2.get(oName) : 0.0;
            Credit reward = new Credit(operatorSelector.getNumberOfIterations(), credit1, credit2);
            
            operatorSelector.updateQualities(reward, operatorSelector.getOperator(oName));          
        }
        
        operatorSelector.updateProbabilities();
        
        // update history
        credits1History.add(makeCreditHistoryEntry(credits1));
        credits2History.add(makeCreditHistoryEntry(credits2));
        operatorSelector.updateHistories(numberOfEvaluation);
    }

    /**
     * Increase the number of evaluation by arity
     * 
     * @param arity
     */
	public void addNumberOfEvaluation(int arity) {
		this.numberOfEvaluation += arity;		
	}
	
	public AOSHistoryInfo makeCreditHistoryEntry(Map<String, Double> credits) {
		AOSHistoryInfo entry = new AOSHistoryInfo();
		entry.nfe = numberOfEvaluation;

		entry.value = new HashMap<String, Double>();
		Set<String> oNames = operatorSelector.getOperatorNames();
		for (String oName : oNames) {
			double credit = credits.containsKey(oName) ? credits.get(oName) : 0.0;
			entry.value.put(oName, credit);
		}

		return entry;
	}
	
	public void saveHistories() {
		Set<String> oNames = operatorSelector.getOperatorNames();
		aosHistory.WriteHistory(credits1History, oNames, "credit1History.csv");
		aosHistory.WriteHistory(credits2History, oNames, "credit2History.csv");
		
		operatorSelector.saveHistories(aosHistory.getPath());
	}
}