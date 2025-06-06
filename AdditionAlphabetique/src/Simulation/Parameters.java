package Simulation;

public final class Parameters {

	protected static double specificity = 1.3; // MODEL ✅
	protected static final double rt_mu = 4.5; // MODEL ✅
	protected static final double rt_sd = 1; // MODEL ✅
	//To calculate the probability to retrieval a rule OR an answer from procedure memory and answer memory
	protected static double decisionDeterminism = 1.1;
	//To adjust weight of a rule in procedure memory according to the correct or incorrect answer
	protected static double errorDiscount = 0.06;
	//To increase weight of an answer in answer memory
	protected static double weightIncrease = 7.5; 
	// To set initial practice weight to skip from element i to element i+1
	protected static int initialPractice = 0;
	// To increase practice to skip from element i to element i+1
	protected static double increasePractice=8;
	//To set initial strength of an answer to retrieve it more or less efficiency
	protected static double initialStrength=1;
	// To increase the strength of an answer to retrieve it more or less efficiency
	protected static double increaseStrength=9.5;
	// 3 parameters to calculate time by retrieving memory
	protected static final double n = 171;
	protected static final double t = 4900;
	protected static final double p = 90;

	// 3 parameters to calculate time by using production rules
	protected static final double a=20;
	protected static final double b =1200;
	protected static final double d=400;

	// 3 parameters to estimate participant-independent response time
	protected static final int elementEncoding = 80*3;
	protected static final int motorCommand = 300;
	protected static final int comparison = 200;
	
	protected static double switching_cost=0;
	protected static char previous_strat='p';
	protected static int other_strat=0;
	
	private Parameters() {
		
	}
}
