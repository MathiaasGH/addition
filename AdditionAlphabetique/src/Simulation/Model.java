package Simulation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import Exceptions.ProblemException;

/**
 * La classe Model permet d'initialiser une instance du modèle créé pour résoudre des problèmes arithmético-alphabétique
 * @author Mathias
 * @version 1.0
 */
public class Model {

	protected static double specificity = 1.3; // MODEL ✅
	protected static final double rt_mu = 4.5; // MODEL ✅
	protected static final double rt_sd = 1; // MODEL ✅
	//To calculate the probability to retrieval a rule OR an answer from procedure memory and answer memory
	protected static double decisionDeterminism = 1.2;
	//To adjust weight of a rule in procedure memory according to the correct or incorrect answer
	protected static double errorDiscount = 0;
	//To increase weight of an answer in answer memory
	protected static double weightIncrease = 1.5; 
	// To set initial practice weight to skip from element i to element i+1
	protected static int initialPractice = 0;
	// To increase practice to skip from element i to element i+1
	protected static double increasePractice=1;
	//To set initial strength of an answer to retrieve it more or less efficiency
	protected static double initialStrength=1.5;
	//To increase the strength of an answer to retrieve it more or less efficiency
	protected static double increaseStrength=2;
	// 3 parameters to calculate time by retrieving memory
	protected double n = 171;
	protected double t = 4900;
	protected double p = 90;

	// 3 parameters to calculate time by using production rules
	protected double a=20;
	protected double b =1200;
	protected double d=400;

	// 3 parameters to estimate participant-independent response time
	protected static int elementEncoding = 80*3;
	protected static int motorCommand = 300;
	protected static int comparison = 200;



	public int session=0;
	List<Problem> problemList = new LinkedList<Problem>();
	double[] practice = new double[26];

	/**
	 * Constructeur pour le modèle.
	 */
	public Model() {
		for(int i=0;i<26;i++) {
			practice[i]=initialPractice;
		}
	}

	/**
	 * Constructeur pour le modèle
	 * @param dd la valeur du paramère decisionDeterminism
	 * @param ed la valeur du paramètre errorDiscount
	 * @param wi la valeur du paramètre weigthIncrease
	 * @param ip la valeur du paramètre initialPractice
	 * @param inp la valeur du paramètre increasePractice
	 * @param is la valeur du paramètre initialStrength
	 * @param ins la valeur du paramètre increaseStrength
	 */
	public Model(double dd, double ed, double wi, int ip, double inp, double is, double ins) {
		decisionDeterminism = dd;
		errorDiscount = ed;
		weightIncrease= wi;
		initialPractice= ip;
		increasePractice = inp;
		initialStrength= is;
		increaseStrength= ins;
		for(int i=0;i<26;i++) {
			practice[i]=initialPractice;
		}
	}

	/**
	 * Modifie le paramètre initialPractice en modifiant les valeurs des cases du tableau practice
	 * @param j la valeur du paramètre initialPractice
	 */
	public void setinitialPractice(int j) {
		initialPractice=j;
		for(int i=0;i<26;i++) {
			practice[i]=initialPractice;
		}
	}

	/**
	 * Modifie le paramètre initialPractice sans modifier les valeurs des cases du tableau practice
	 * @param j la valeur du paramètre initialPractice
	 */
	public void setinitialPracticeWithoutReset(int j) {
		initialPractice=j;
	}

	/**
	 * Modifie le paramètre increasePractice
	 * @param j la valeur du paramètre increasePractice
	 */
	public void setincreasePractice(double j) {
		increasePractice=j;
	}

	/**
	 * Modifie le paramètre initialStrength
	 * @param j la valeur du paramètre initialStrength
	 */
	public void setinitialStrength(double j) {
		initialStrength=j;
	}

	/**
	 * Modifie le paramètre increaseStrength
	 * @param j la valeur du paramètre increaseStrength
	 */
	public void setincreaseStrength(double j) {
		increaseStrength=j;
	}

	/**
	 * Modifie le paramètre de specificity
	 * @param sp  la valeur du paramètre specificity
	 */
	public void setSpecificity(double sp) {
		specificity=sp;
	}

	/**
	 * Modifie le paramètre de weigthIncrease
	 * @param wi la valeur du paramètre weigthIncrease
	 */
	public void setWeightIncrease(double wi) {
		weightIncrease=wi;
	}

	/**
	 * Modifie le paramère de decisionDeterminism
	 * @param val la valeur du paramètre decisionDeterminism
	 */
	public void setDecisionDeterminism(double val) {
		decisionDeterminism=val;
	}

	/**
	 * Modifie le paramètre de errorDiscount
	 * @param val la valeur du paramètre errorDiscount
	 */
	public void setErrorDiscount(double val) {
		errorDiscount=val;
	}

	/**
	 * Récupère le temps pour passer d'une lettre L à sa voisine L+1
	 * @param lettre la lettre de départ (L)
	 * @return le temps pris pour passer de la lettre de départ à la lettre d'arrivée (L -> L+1)
	 */
	public double getTime(String lettre, Problem pb) {
		try {
			practice[(int)(lettre.charAt(0)) - 65]=practice[(int)(lettre.charAt(0)) - 65]+increasePractice;
			return a+b*Math.exp(-practice[(int)(lettre.charAt(0)) - 65] / d);
		}
		catch(ArrayIndexOutOfBoundsException e) {
			try {
				practice[(int)(lettre.charAt(0)) - 97]=practice[(int)(lettre.charAt(0)) - 97]+increasePractice;
				return a+b*Math.exp(-practice[(int)(lettre.charAt(0)) - 97] / d);
			}
			catch(ArrayIndexOutOfBoundsException ex) {
				return a+b*Math.exp(-practice[(int)(lettre.charAt(0)) - 97] / d);
			}
		}
	}

	/**
	 * Ajoute un problème à la liste des problèmes que le modèle doit résoudre
	 * @param problem le problème à résoudre
	 * @return le temps pris par le modèle pour résoudre le problème
	 */
	public double addProblem(Problem problem) {
		problemList.add(problem);
		return problem.gtTime();
	}

	/**
	 * Réinitialise la mémoire des réponses
	 */
	public void cleanAnswerMemory() {
		Answer_Memory.getInstance().clean();
	}

	/**
	 * Réinitialise la mémoire procédurale
	 */
	public void cleanProcedureMemory() {
		Procedure_Memory.getInstance().clean();
	}

	/**
	 * Affiche la session courante
	 */
	protected void printSession() {
		System.out.println("\n \n********************************** \n \n          Session " + session + "\n \n **********************************");
	}

	/**
	 * Simule une session de 288 problèmes pour un addend choisi et des lettres de A à F
	 * @param addend l'addend des problèmes choisi
	 * @return un tableau contenant le nombre d'erreurs et le temps total pris pour résoudre les 288 problèmes
	 */
	public double[] session(int addend, String condition) {
		session++;
		printSession();
		int errors=0;
		double time=0;
		char[] letters = {'a', 'c', 'e', 'g', 'i', 'k'};
		for(int a=0;a<288;a++) {
			int randomLetter;
			if(condition.equals("CSC"))
				randomLetter = (int) ((Math.random() * (107 - 101)) + 101);
			else
				randomLetter = new Random().nextInt(letters.length);
			try {
				Problem problem;
				if(condition.equals("CSC"))
					problem = new Problem((char)(randomLetter) + "+" + addend ,this, condition);
				else
					problem = new Problem(letters[randomLetter] + "+" + addend ,this, condition);
				double timeProblem = addProblem(problem);
				time = time + timeProblem;
				if(problem.error())
					errors++;
			}
			catch(ProblemException e) {
				System.out.println(e);
			}

		}
		return new double[] {(double)errors, time};
	}

	/**
	 * Simule une session de 288 problèmes d'addends entre 2 et 5 d'augend de A à F
	 * @return un tableau du nombre d'erreur et du temps total pris par le modèle pour résoudre toute la session
	 */
	public double[] session(String condition) {
		session++;
		printSession();
		int errors=0;
		double time=0;
		char[] letters = {'a', 'c', 'e', 'g', 'i', 'k'};
		for(int a=0;a<288;a++) {
			int randomLetter;
			if(condition.equals("CSC"))
				randomLetter = (int) ((Math.random() * (107 - 101)) + 101);
			else
				randomLetter = new Random().nextInt(letters.length);
			int randomNumber = (int) ((Math.random() * (5 - 2 + 1)) + 2);
			try {
				Problem problem;
				if(condition.equals("CSC"))
					problem = new Problem((char)(randomLetter) + "+" + randomNumber ,this, condition);
				else
					problem = new Problem(letters[randomLetter] + "+" + randomNumber ,this, condition);
				double timeProblem = addProblem(problem);
				time = time + timeProblem;
				if(problem.error())
					errors++;
			}
			catch(ProblemException e) {
				System.out.println(e);
			}
		}
		return new double[] {errors, time};
	}

	/**
	 * Affiche le contenu de la mémoire de réponses ainsi que le contenu des règles dans la mémoire procédurale
	 */
	public String toString() {
		return "Answer Memory : \n" + Answer_Memory.getInstance().toString() + "\nRules :\n" + Procedure_Memory.getInstance().toString() + "\n";
	}

	public void printPractice() {
		System.out.println(Arrays.toString(practice));
	}

	public static void main(String[] args) {
		Model model = new Model();
		try{
			model.addProblem(new Problem("a+2", model, "CSC"));
		}
		catch(ProblemException e) {
			System.out.println(e);
		}


		/*model.addProblem(new Problem("g+2", model, "CSC"));
		model.addProblem(new Problem("g+2", model, "CSC"));
		model.addProblem(new Problem("g+2", model, "CSC"));
		model.addProblem(new Problem("g+2", model, "CSC"));
		model.addProblem(new Problem("g+2", model, "CSC"));
		model.addProblem(new Problem("g+2", model, "CSC"));
		model.addProblem(new Problem("g+2", model, "CSC"));

		odel.addProblem(new Problem("f+3", model, "CSC"));
		 */
		/*try{
		model.addProblem(new Problem("a+2", model, "NCSC"));
		}
		catch(ProblemException e) {

		}*/
		//model.session("NCSC");
		//	model.session("NCSC");
		//	model.session("NCSC");
		//	model.session("NCSC");

		//model.session("CSC");
		//model.session("CSC");
		//model.session("CSC");
		//model.session("CSC");
		//model.session("CSC");
		//model.session("CSC");
		//model.session("CSC");

		//model.printPractice();
		//	System.out.println(model);

		/*try {
		for(int i=0;i<60;i++)
			model.addProblem(new Problem("a+2", model, "CSC"));
		System.out.println(model);
		model.addProblem(new Problem("a+3", model, "CSC"));
		}
		 */
		/*		}
		catch(ProblemException e) {
			System.out.println(e);
		}
		 */


		//		}
		//		catch(ProblemException e) {
		//			System.out.println(e);
		//		}

	}
}
