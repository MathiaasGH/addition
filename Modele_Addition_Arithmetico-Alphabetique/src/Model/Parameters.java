package Model;

/**
 * Classe permettant de stocker tous les paramètres du modèle
 * @author Mathias DEVILLIERS
 */
public final class Parameters {
	
	protected static final double goodRule = 0.3; //
	protected static final double malRule = -1; // 
	protected static double specificity = 2; // MODEL UMA
	protected static final double rt_mu = 4.5; // MODEL UMA
	protected static final double rt_sd = 1; // MODEL UMA
	//To calculate the probability to retrieval a rule OR an answer from procedure memory and answer memory
	protected static double decisionDeterminism = 1.2;
	//To adjust weight of a rule in procedure memory according to the correct or incorrect answer
	protected static double errorDiscount = 0.02; //0.03
	//To increase weight of an answer in answer memory
	protected static double weightIncrease = 1.5; 
	// To set initial practice weight to skip from element i to element i+1
	protected static int initialPractice = 0;
	// To increase practice to skip from element i to element i+1
	protected static double increasePractice=.5;//0.65 ; .85
//	//To set initial strength of an answer to retrieve it more or less efficiency
//	protected static double initialStrength= 1;//.9 ; 1.28
	// To increase the strength of an answer to retrieve it more or less efficiency
	protected static double increaseStrength=.6; // .9 ; 1.28
	
	
	protected static double counting_reinforcement=.4;
	protected static double retrieving_reinforcement=.9;
	
	// To decide which strategy to use
	protected static double rationalityParameter = 0.0018;
	
	// 3 parameters to calculate time by retrieving memory
	protected static final double n = 171;
	protected static double t = 3750; // 2800
	protected static double p = 40; // 90

	// 3 parameters to calculate time by using production rules
	protected static final double a=20;
	protected static double b = 800; // 800
	protected static double d=325; // 325
	
	//2 parameters to calculate decision time
	protected static double alphaDT=400;
	protected static double omegaDT=150;

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
