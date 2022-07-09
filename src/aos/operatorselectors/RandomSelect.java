/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package aos.operatorselectors;

import aos.nextoperator.AbstractOperatorSelector;
import aos.creditassigment.Credit;
import java.util.Collection;
import java.util.HashMap;

import org.moeaframework.core.Variation;

/**
 * RandomSelect randomly selects a heuristic with uniform probability from the 
 * given set of heuristics
 * @author nozomihitomi
 */
public class RandomSelect extends AbstractOperatorSelector{
    
    /**
     * RandomSelect does not really utilize the credit repository so any 
     * repository will do
     * @param heuristics from which to select from 
     */
    public RandomSelect(Collection<Variation> operators) {
        super(operators);
    }

    /**
     * Randomly selects the next heuristic from the set of heuristics with 
     * uniform probability
     * @return 
     */
    @Override
    public Variation nextOperator() {
        incrementIterations();
        return super.getRandomOperator(operators);
    }

    @Override
    public String toString() {
        return "RandomSelect";
    }

    @Override
    public void updateQualities(Credit reward, Variation operator) {
        //no need to do any updates
    }
    
    @Override
    public void updateProbabilities() {
        //no need to do any updates
    }

	@Override
	public void updateHistories(int numberOfEvaluation) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void saveHistories(String path) {
		// TODO Auto-generated method stub
		
	}

}