/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aos.aos;

import org.moeaframework.algorithm.AbstractEvolutionaryAlgorithm;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;

/**
 * An MOEA with an adaptive operator selector controlling the use of the
 * operators
 *
 * @author nozomihitomi
 */
public class AOSMOEA extends AbstractEvolutionaryAlgorithm implements IAOS {
    /**
     * Name to id the AOS
     */
    private String name;

    /**
     * The underlying evolutionary algorithm
     */
    private final AbstractEvolutionaryAlgorithm ea;

    /**
     * The current paretofront
     */
//    private final NondominatedPopulation paretofront;

    private final AOSStrategy strategy;
    
    /**
     * 
     * @param ea the evolutionary algorithm
     * @param aosVariation the aos variation that must be given to the EA. It is
     * reassigned to the given operators during the search by the AOS
     * @param strategy the credit assignment and operator selection strategies
     */
    public AOSMOEA(AbstractEvolutionaryAlgorithm ea, AOSStrategy strategy) {
        super(ea.getProblem(), ea.getPopulation(), ea.getArchive(), null);
        
        this.strategy = strategy;

        this.ea = ea;

    }

    @Override
    protected void initialize() {
        if (!ea.isInitialized() && !ea.isTerminated()) {
            ea.step();

            // AOSStrategy should be initialized after ea initialization.
            strategy.initialize(ea.getPopulation(), ea.getArchive());
        }
    }

    @Override
    public void terminate() {
        ea.terminate(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isTerminated() {
        return ea.isTerminated(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isInitialized() {
        return ea.isInitialized(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Problem getProblem() {
        return ea.getProblem(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getNumberOfEvaluations() {
        return ea.getNumberOfEvaluations(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void evaluate(Solution solution) {
        ea.evaluate(solution); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void evaluateAll(Solution[] solutions) {
        ea.evaluateAll(solutions); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void evaluateAll(Iterable<Solution> solutions) {
        ea.evaluateAll(solutions); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void iterate() {
        ea.step();
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    

}