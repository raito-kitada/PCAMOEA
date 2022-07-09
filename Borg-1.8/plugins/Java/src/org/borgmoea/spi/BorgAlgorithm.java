package org.borgmoea.spi;

import java.io.NotSerializableException;
import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.borgmoea.Borg;
import org.borgmoea.ObjectiveFunction;
import org.borgmoea.Result;
import org.borgmoea.Solution;
import org.moeaframework.analysis.sensitivity.EpsilonHelper;
import org.moeaframework.core.Algorithm;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Problem;
import org.moeaframework.core.variable.EncodingUtils;
import org.moeaframework.core.variable.RealVariable;
import org.moeaframework.util.TypedProperties;

/**
 * Wrapper for the Borg MOEA implementing the MOEA Framework's {@link Algorithm} interface.
 */
public class BorgAlgorithm implements Algorithm {
	
	/**
	 * The optimization problem.
	 */
	private final Problem problem;
	
	/**
	 * The algorithm parameters.
	 */
	private final TypedProperties properties;
	
	/**
	 * The Pareto optimal set produced by the Borg MOEA; or
	 * {@code null} if the algorithm has not yet finished running.
	 */
	private Result result;
	
	/**
	 * {@code true} if the algorithm has finished running; {@code false} otherwise.
	 */
	private boolean finished;
	
	/**
	 * Constructs an instance of the Borg MOEA to solve the given optimization problem.
	 * 
	 * @param problem the optimization problem
	 * @param properties the algorithm parameters
	 */
	public BorgAlgorithm(Problem problem, TypedProperties properties) {
		super();
		this.problem = problem;
		this.properties = properties;
		this.result = null;
		this.finished = false;
	}

	@Override
	public void evaluate(org.moeaframework.core.Solution solution) {
		problem.evaluate(solution);
	}

	@Override
	public int getNumberOfEvaluations() {
		if (finished) {
			return properties.getInt("maxEvaluations", 10000);
		} else {
			return 0;
		}
	}

	@Override
	public int getNumberOfIterations() {
		if (finished) {
			return 1;
		} else {
			return 0;
		}
	}	
	
	@Override
	public Problem getProblem() {
		return problem;
	}

	@Override
	public NondominatedPopulation getResult() {
		NondominatedPopulation population = new NondominatedPopulation();
		
		List<Map<String, Double>> statistics = result.getStatistics();
		Iterator<Map<String, Double>> stItr = statistics.iterator();
		while (stItr.hasNext()) {
			Map<String, Double> entry = (Map<String, Double>)stItr.next();
			for (String key : entry.keySet()) {
				System.out.println(key + " = " + entry.get(key));
			}
		}
		
		
		for (Solution solution : result) {
			population.add(translate(solution));
		}
		
		return population;
	}
	
	/**
	 * Translates a Borg MOEA solution into an MOEA Framework solution.
	 * 
	 * @param solution the Borg MOEA solution
	 * @return the equivalent MOEA Framework solution
	 */
	private org.moeaframework.core.Solution translate(Solution solution) {
		org.moeaframework.core.Solution result = problem.newSolution();
		
		for (int i = 0; i < problem.getNumberOfVariables(); i++) {
			EncodingUtils.setReal(result.getVariable(i), solution.getVariable(i));
		}
		
		for (int i = 0; i < problem.getNumberOfObjectives(); i++) {
			result.setObjective(i, solution.getObjective(i));
		}
		
		for (int i = 0; i < problem.getNumberOfConstraints(); i++) {
			result.setConstraint(i, solution.getConstraint(i));
		}
		
		return result;
	}

	@Override
	public Serializable getState() throws NotSerializableException {
		throw new NotSerializableException();
	}

	@Override
	public boolean isTerminated() {
		return finished;
	}

	@Override
	public void setState(Object state) throws NotSerializableException {
		throw new NotSerializableException();
	}

	@Override
	public void step() {
		Borg borg = new Borg(problem.getNumberOfVariables(), problem.getNumberOfObjectives(), problem.getNumberOfConstraints(), new ProblemWrapper());
		
		// set the lower and upper bounds
		org.moeaframework.core.Solution solution = problem.newSolution();
		
		for (int i = 0; i < problem.getNumberOfVariables(); i++) {
			RealVariable realVariable = (RealVariable)solution.getVariable(i);
			
			borg.setBounds(i, realVariable.getLowerBound(), realVariable.getUpperBound());
		}
		
		// set the epsilons
		double[] epsilons = properties.getDoubleArray("borg.epsilon", 
				new double[] { EpsilonHelper.getEpsilon(problem) });
		
		for (int i = 0; i < problem.getNumberOfObjectives(); i++) {
			if (epsilons.length == 0) {
				borg.setEpsilon(i, 0.01);
			} else if (i < epsilons.length) {
				borg.setEpsilon(i, epsilons[i]);
			} else {
				borg.setEpsilon(i, epsilons[epsilons.length-1]);
			}
		}
		
		// set the parameters
		borg.setParameter("pm.rate", properties.getDouble("pm.rate", 1.0 / problem.getNumberOfVariables()));
		borg.setParameter("pm.distributionIndex", properties.getDouble("pm.distributionIndex", 20.0));
		borg.setParameter("sbx.rate", properties.getDouble("sbx.rate", 1.0));
		borg.setParameter("sbx.distributionIndex", properties.getDouble("sbx.distributionIndex", 15.0));
		borg.setParameter("de.crossoverRate", properties.getDouble("de.crossoverRate", 0.1));
		borg.setParameter("de.stepSize", properties.getDouble("de.stepSize", 0.5));
		borg.setParameter("um.rate", properties.getDouble("um.rate", 1.0 / problem.getNumberOfVariables()));
		borg.setParameter("spx.epsilon", properties.getDouble("spx.epsilon", 3.0));
		borg.setParameter("spx.parents", properties.getInt("spx.parents", 10));
		borg.setParameter("spx.offspring", properties.getInt("spx.offspring", 2));
		borg.setParameter("pcx.eta", properties.getDouble("pcx.eta", 0.1));
		borg.setParameter("pcx.zeta", properties.getDouble("pcx.zeta", 0.1));
		borg.setParameter("pcx.parents", properties.getInt("pcx.parents", 10));
		borg.setParameter("pcx.offspring", properties.getInt("pcx.offspring", 2));
		borg.setParameter("undx.zeta", properties.getDouble("undx.zeta", 0.5));
		borg.setParameter("undx.eta", properties.getDouble("undx.eta", 0.35));
		borg.setParameter("undx.parents", properties.getInt("undx.parents", 10));
		borg.setParameter("undx.offspring", properties.getInt("undx.offspring", 2));
		borg.setParameter("initialPopulationSize", properties.getInt("borg.initialPopulationSize", 100));
		borg.setParameter("minimumPopulationSize", properties.getInt("borg.minimumPopulationSize", 100));
		borg.setParameter("maximumPopulationSize", properties.getInt("borg.maximumPopulationSize", 10000));
		borg.setParameter("injectionRate", properties.getDouble("borg.injectionRate", 0.25));
		borg.setParameter("selectionRatio", properties.getDouble("borg.selectionRatio", 0.02));
		borg.setParameter("restartMode", properties.getInt("borg.restartMode", 0));
		borg.setParameter("maxMutationIndex", properties.getInt("borg.maxMutationIndex", 10));
		borg.setParameter("probabilityMode", properties.getInt("borg.probabilityMode", 0));
		borg.setParameter("frequency", properties.getInt("borg.frequency", 0));
		
		// solve the problem
		result = borg.solve(properties.getInt("maxEvaluations", 10000));
		finished = true;
	}

	@Override
	public void terminate() {
		finished = true;
	}
	
	/**
	 * Connects the {@code evaluate} method in the MOEA Framework's {@link Problem}
	 * interface to the {@code evaluate} method in {@link ObjectiveFunction}.
	 */
	private class ProblemWrapper implements ObjectiveFunction {

		@Override
		public void evaluate(double[] variables, double[] objectives,
				double[] constraints) {
			org.moeaframework.core.Solution solution = problem.newSolution();
			
			for (int i = 0; i < problem.getNumberOfVariables(); i++) {
				EncodingUtils.setReal(solution.getVariable(i), variables[i]);
			}
			
			problem.evaluate(solution);
			
			for (int i = 0; i < problem.getNumberOfObjectives(); i++) {
				objectives[i] = solution.getObjective(i);
			}
			
			for (int i = 0; i < problem.getNumberOfConstraints(); i++) {
				constraints[i] = solution.getConstraint(i);
			}
		}
		
	}

}
