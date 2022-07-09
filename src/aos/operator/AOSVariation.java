/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aos.operator;

import aos.aos.AOSStrategy;
import lab.moea.util.Util;

import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variation;

/**
 * This variation is a wrapper for other variations and can take on their
 * methods. The wrapped variation can be changed using the set method
 *
 * @author nozomihitomi
 */
public class AOSVariation implements Variation {

    private Variation variation;

    private Solution[] parents;

    private Solution[] offsprings;
    
    private AOSStrategy strategy;
    
    private Problem problem;
    
    public AOSVariation(AOSStrategy strategy, Problem problem) {
    	this.variation = null;
    	this.parents = null;
    	this.offsprings = null;
    	this.strategy = strategy;
    	this.problem = problem;
    }

    @Override
    public int getArity() {
    	if (variation == null)
    		setVariation(strategy.nextOperator());

    	return variation.getArity();
    }

    @Override
    public Solution[] evolve(Solution[] parents) {
    	if (variation == null)
    		setVariation(strategy.nextOperator());
    	
        this.parents = parents;
        this.offsprings = variation.evolve(parents);
        
        // Although inefficient, it's necessary to evaluate offsprings here.
        for (Solution solution : offsprings) {
    		problem.evaluate(solution);        	
        }
        strategy.addNumberOfEvaluation(variation.getArity());
        
        // compute credit and reward to current operator
        strategy.computeCreditAndReward(parents, offsprings, variation);
        
        // DEBUG
        Util.DebugPlot(parents, offsprings, "parents", "offsprings");
        
        // set next operator
        setVariation(strategy.nextOperator());

        return this.offsprings;
    }

    public Solution[] getParents() {
        return parents;
    }

    public Solution[] getOffspring() {
        return offsprings;
    }

    public void setVariation(Variation variation) {
        this.variation = variation;
    }

    public Variation getVariation(Variation variation) {
        return variation;

    }
}