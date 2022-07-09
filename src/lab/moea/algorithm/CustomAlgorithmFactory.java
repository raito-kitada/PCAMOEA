package lab.moea.algorithm;

import org.moeaframework.algorithm.MOEAD;
import org.moeaframework.algorithm.NSGAII;
import org.moeaframework.algorithm.ReferencePointNondominatedSortingPopulation;
import org.moeaframework.core.Algorithm;
import org.moeaframework.core.EpsilonBoxDominanceArchive;
import org.moeaframework.core.Initialization;
import org.moeaframework.core.NondominatedSortingPopulation;
import org.moeaframework.core.PRNG;
import org.moeaframework.core.Population;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Selection;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variation;
import org.moeaframework.core.comparator.AggregateConstraintComparator;
import org.moeaframework.core.comparator.ChainedComparator;
import org.moeaframework.core.comparator.DominanceComparator;
import org.moeaframework.core.operator.TournamentSelection;
import org.moeaframework.core.spi.AlgorithmFactory;
import org.moeaframework.util.TypedProperties;

/**
 * A provider of algorithms with custom initialization.
 * 
 */
public class CustomAlgorithmFactory {
	public static Algorithm getAlgorithm(
										String name, 
										Problem problem, 
										Selection selection, 
										Variation variation, 
										Initialization initialization, 
										TypedProperties properties
										) {
		// Common Parameter
		boolean usearchive = properties.getBoolean("use_archive", false);
		double eps         = properties.getDouble ("archive_eps", 0.01);
		EpsilonBoxDominanceArchive archive = usearchive ? new EpsilonBoxDominanceArchive(eps) : null;

		try {
			if (name.equalsIgnoreCase("NSGAII-LAB")) {
				
				return new NSGAII(
						problem,
						new NondominatedSortingPopulation(),
						archive,
						selection,
						variation,
						initialization);
				
			} else if (name.equalsIgnoreCase("NSGAIIPSD")) { // NSGAII with Parameter Space Discretization 
				
				return new NSGAIIPSD(
						problem,
						new NondominatedSortingPopulation(),
						archive,
						selection,
						variation,
						initialization); 
				
			} else if (name.equalsIgnoreCase("NSGAIIPOD")) { // NSGAII with POD 
				
				return new NSGAIIPOD(
						problem,
						new NondominatedSortingPopulation(),
						archive,
						selection,
						variation,
						initialization); 
				
			} else if(name.equalsIgnoreCase("NSGAIII-LAB")) {
				
				int divisionsOuter = properties.getInt("nsga3.divisions_outer", 12); // for 3objs (See newNSGAIII() in StandardAlgorithms.java)
				int divisionsInner = properties.getInt("nsga3.divisions_inner", 0);
				
				ReferencePointNondominatedSortingPopulation population = new ReferencePointNondominatedSortingPopulation(
						problem.getNumberOfObjectives(), divisionsOuter, divisionsInner);

				if (problem.getNumberOfConstraints() == 0) {
					selection = new Selection() {
			
						@Override
						public Solution[] select(int arity, Population population) {
							Solution[] result = new Solution[arity];
							
							for (int i = 0; i < arity; i++) {
								result[i] = population.get(PRNG.nextInt(population.size()));
							}
							
							return result;
						}
						
					};
				} else {
					selection = new TournamentSelection(2, new ChainedComparator(
							new AggregateConstraintComparator(),
							new DominanceComparator() {

								@Override
								public int compare(Solution solution1, Solution solution2) {
									return PRNG.nextBoolean() ? -1 : 1;
								}
								
							}));
				}				

				return new NSGAII(
						problem,
						population,
						archive,
						selection,
						variation,
						initialization);
				
			} else if(name.equalsIgnoreCase("MOEAD-LAB")) {
				
				int neighborhoodSize = properties.getInt   ("moead.neighborhood_size", 20);
				int eta              = properties.getInt   ("moead.eta", 2);
				double delta         = properties.getDouble("moead.delta", 0.9);
				int updateUtility    = properties.getInt   ("moead.update_utility", -1);
				
				return new MOEAD(problem,
						neighborhoodSize,
						initialization,
						variation,
						delta, 
						eta,
						updateUtility);

			} else {

				return AlgorithmFactory.getInstance().getAlgorithm(
						name, properties.getProperties(), problem);

			}
		} catch (NumberFormatException e) {
			System.out.println(e.getMessage());
			e.getStackTrace();
			System.exit(0);
		}

		return null;
	}
}
