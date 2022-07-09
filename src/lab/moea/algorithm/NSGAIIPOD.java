package lab.moea.algorithm;

import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.mllib.linalg.distributed.RowMatrix;
import org.moeaframework.algorithm.NSGAII;
import org.moeaframework.core.EpsilonBoxDominanceArchive;
import org.moeaframework.core.Initialization;
import org.moeaframework.core.NondominatedSortingPopulation;
import org.moeaframework.core.Population;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Selection;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variation;
import org.moeaframework.core.variable.RealVariable;

import lab.moea.operator.real.PODOP;
/**
 * NSGA-II with SDPSD
 * 
 * It's almost the same as NSGA-II.
 * But before calling the iterate function of NSGA-II, 
 * the update function of SDPSD is called.
 * 
 */
public class NSGAIIPOD extends NSGAII{
	
	public NSGAIIPOD(Problem problem, NondominatedSortingPopulation population,
			EpsilonBoxDominanceArchive archive, Selection selection,
			Variation variation, Initialization initialization) {
		super(problem, population, archive, selection, variation, initialization);
	}

	public void iterate() {
		Population population = getResult();
		
		int update_interval = 1; // must be modified
		if ( (numberOfIterations-1) % update_interval == 0) PODOP.update(population);
		
		super.iterate();
	}
}
