package exercise1;

import cern.jet.random.engine.MersenneTwister64;
import net.finmath.montecarlo.AbstractRandomVariableFactory;
import net.finmath.montecarlo.BrownianMotionInterface;
import net.finmath.montecarlo.RandomVariableFactory;
import net.finmath.stochastic.RandomVariableInterface;
import net.finmath.time.TimeDiscretizationInterface;

/** A alternative BrownianMotion Class implementing the net.finmath.montecarlo.BrownianMotionInterface 
 * with different generation of random numbers and normal distributed Random Variables.
 * @author vince
 * @version 1.0
 */

public class EPV_BrownianMotion implements BrownianMotionInterface {
	
	private final TimeDiscretizationInterface						timeDiscretization;

	private final int			numberOfFactors;
	private final int			numberOfPaths;
	private final int			seed;

	private RandomVariableInterface[][]	brownianIncrements;
	
	
	public EPV_BrownianMotion(
			TimeDiscretizationInterface timeDiscretization,
			int numberOfFactors,
			int numberOfPaths,
			int seed) {
		super();
		this.timeDiscretization = timeDiscretization;
		this.numberOfFactors	= numberOfFactors;
		this.numberOfPaths		= numberOfPaths;
		this.seed				= seed;

		this.brownianIncrements	= null; 	// Lazy initialization
	}
	
	private void doGenerateBrownianMotion() {
		if(brownianIncrements != null) return;	// Nothing to do

		// Create random number sequence generator (we use MersenneTwister64 from colt)
		MersenneTwister64		mersenneTwister		= new MersenneTwister64(seed);

		// Allocate memory
		double[][][] brownianIncrementsArray = new double[timeDiscretization.getNumberOfTimeSteps()][numberOfFactors][numberOfPaths];

		// Pre-calculate square roots of deltaT
		double[] sqrtOfTimeStep = new double[timeDiscretization.getNumberOfTimeSteps()];
		for(int timeIndex=0; timeIndex<sqrtOfTimeStep.length; timeIndex++) {
			sqrtOfTimeStep[timeIndex] = Math.sqrt(timeDiscretization.getTimeStep(timeIndex));
		}   

		/*
		 * Generate normal distributed independent increments.
		 * 
		 * The inner loop goes over time and factors.
		 * MersenneTwister is known to generate "independent" increments in 623 dimensions.
		 * Since we want to generate independent streams (paths), the loop over path is the outer loop.
		 */
		for(int path=0; path<numberOfPaths; path++) {
			for(int timeIndex=0; timeIndex<timeDiscretization.getNumberOfTimeSteps(); timeIndex++) {
				double sqrtDeltaT = sqrtOfTimeStep[timeIndex];
				// Generate uncorrelated Brownian increment
				for(int factor=0; factor<numberOfFactors; factor++) {
					double uniformIncement = mersenneTwister.nextDouble();
					brownianIncrementsArray[timeIndex][factor][path] = net.finmath.functions.NormalDistribution.inverseCumulativeDistribution(uniformIncement) * sqrtDeltaT;
				}				
			}
		}

		// Allocate memory for RandomVariable wrapper objects.
		brownianIncrements = new RandomVariableInterface[timeDiscretization.getNumberOfTimeSteps()][numberOfFactors];

		// Wrap the values in RandomVariable objects
		for(int timeIndex=0; timeIndex<timeDiscretization.getNumberOfTimeSteps(); timeIndex++) {
            double time = timeDiscretization.getTime(timeIndex+1);
			for(int factor=0; factor<numberOfFactors; factor++) {
				brownianIncrements[timeIndex][factor] =
                        randomVariableFactory.createRandomVariable(time, brownianIncrementsArray[timeIndex][factor]);
			}
		}
	}
	
	
	
	@Override
	public RandomVariableInterface getBrownianIncrement(int timeIndex, int factor) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TimeDiscretizationInterface getTimeDiscretization() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getNumberOfFactors() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getNumberOfPaths() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public RandomVariableInterface getRandomVariableForConstant(double value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BrownianMotionInterface getCloneWithModifiedSeed(int seed) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BrownianMotionInterface getCloneWithModifiedTimeDiscretization(
			TimeDiscretizationInterface newTimeDiscretization) {
		// TODO Auto-generated method stub
		return null;
	}

	
	
	
}
