package Model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import Exceptions.ProblemException;

/**
 * La classe Model permet d'initialiser une instance du modèle créé pour résoudre des problèmes arithmético-alphabétique
 * @author Mathias DEVILLIERS
 * @version 1.0
 */
public class Model {

	public int session=0;
	List<Problem> problemList = new LinkedList<Problem>();
	double[] practice = new double[26];

	public String profil;

	public Model() {
		this("breaker");
	}
	public Model(double sw) {
		this("breaker");
		Parameters.switching_cost=sw;
	}

	public Model(String profil) {
		this.profil=profil;
		if(profil.equals("breaker")) {
			Parameters.switching_cost=153;
		}
		else Parameters.switching_cost=753;
		for(int i=0;i<26;i++) {
			practice[i]=Parameters.initialPractice;
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
	public Model(double dd, double ed, double wi, int ip, double inp, double ins, String profil) {
		Parameters.decisionDeterminism = dd;
		Parameters.errorDiscount = ed;
		Parameters.weightIncrease= wi;
		Parameters.initialPractice= ip;
		Parameters.increasePractice = inp;
		Parameters.increaseStrength= ins;
		for(int i=0;i<26;i++) {
			practice[i]=Parameters.initialPractice;
		}
		this.profil = profil;
	}

	/**
	 * Modifie le paramètre initialPractice en modifiant les valeurs des cases du tableau practice
	 * @param j la valeur du paramètre initialPractice
	 */
	public void setinitialPractice(int j) {
		Parameters.initialPractice=j;
		for(int i=0;i<26;i++) {
			practice[i]=Parameters.initialPractice;
		}
	}

	/**
	 * Définit les paramètres liés au temps de résolution des problèmes
	 * @param t un paramètre impliqué dans une équation régissant le temps de résolution d'un problème
	 * @param p un paramètre impliqué dans une équation régissant le temps de résolution d'un problème
	 * @param b un paramètre impliqué dans une équation régissant le temps de résolution d'un problème
	 * @param d un paramètre impliqué dans une équation régissant le temps de résolution d'un problème
	 */
	public void setTimeParam(double t, double p, double b, double d) {
		Parameters.t=t;
		Parameters.p=p;
		Parameters.b=b;
		Parameters.d=d;
	}

	/**
	 * Définit les paramètres liés à l'apprentissage par comptage
	 * @param counting le delta à ajouter à la force d'association problème-réponse dans la mémoire après un comptage
	 * @param retrieving le delta à ajouter à la force d'association problème-réponse dans la mémoire après une récupération
	 */
	public void setReinforcementParam(double counting, double retrieving) {
		Parameters.counting_reinforcement=counting;
		Parameters.retrieving_reinforcement=retrieving;
	}

	/**
	 * Défiint les paramètres de définition de la prise de décision lors du choix comptage vs récupération
	 * @param alpha un paramètre impliqué dans le calcul du temps
	 * @param omega un paramètre impliqué dans le calcul du temps
	 */
	public void setDTparam(double alpha, double omega){
		Parameters.alphaDT=alpha;
		Parameters.omegaDT=omega;
	}


	/**
	 * Modifie le paramètre initialPractice sans modifier les valeurs des cases du tableau practice
	 * @param j la valeur du paramètre initialPractice
	 */
	public void setinitialPracticeWithoutReset(int j) {
		Parameters.initialPractice=j;
	}

	/**
	 * Modifie le paramètre de rationalité
	 * @param j la valeur du paramètre rationalityParameter
	 */
	public void setRationality(double j) {
		Parameters.rationalityParameter=j;
	}

	/**
	 * Modifie le paramètre increasePractice
	 * @param j la valeur du paramètre increasePractice
	 */
	public void setincreasePractice(double j) {
		Parameters.increasePractice=j;
	}


	/**
	 * Modifie le paramètre increaseStrength
	 * @param j la valeur du paramètre increaseStrength
	 */
	public void setincreaseStrength(double j) {
		Parameters.increaseStrength=j;
	}

	/**
	 * Modifie le paramètre de specificity
	 * @param sp  la valeur du paramètre specificity
	 */
	public void setSpecificity(double sp) {
		Parameters.specificity=sp;
	}

	/**
	 * Modifie le paramètre de weigthIncrease
	 * @param wi la valeur du paramètre weigthIncrease
	 */
	public void setWeightIncrease(double wi) {
		Parameters.weightIncrease=wi;
	}

	/**
	 * Modifie le paramère de decisionDeterminism
	 * @param val la valeur du paramètre decisionDeterminism
	 */
	public void setDecisionDeterminism(double val) {
		Parameters.decisionDeterminism=val;
	}

	/**
	 * Modifie le paramètre de errorDiscount
	 * @param val la valeur du paramètre errorDiscount
	 */
	public void setErrorDiscount(double val) {
		Parameters.errorDiscount=val;
	}

	/**
	 * Récupère le temps pour passer d'une lettre L à sa voisine L+1
	 * @param lettre la lettre de départ (L)
	 * @return le temps pris pour passer de la lettre de départ à la lettre d'arrivée (L -> L+1)
	 */
	public double getTime(String lettre) {
		try {
			practice[(int)(lettre.charAt(0)) - 65]=practice[(int)(lettre.charAt(0)) - 65]+Parameters.increasePractice;
			return Parameters.a+Parameters.b*Math.exp(-practice[(int)(lettre.charAt(0)) - 65] / Parameters.d);
		}
		catch(ArrayIndexOutOfBoundsException e) {
			try {
				practice[(int)(lettre.charAt(0)) - 97]=practice[(int)(lettre.charAt(0)) - 97]+Parameters.increasePractice;
				return Parameters.a+Parameters.b*Math.exp(-practice[(int)(lettre.charAt(0)) - 97] / Parameters.d);
			}
			catch(ArrayIndexOutOfBoundsException ex) {
				return Parameters.a+Parameters.b*Math.exp(-practice[(int)(lettre.charAt(0)) - 97] / Parameters.d);
			}
		}
	}

	/**
	 * Retourne le profil du participant
	 * @return le profil du participant
	 */
	public String getProfil() {
		return profil;
	}

	/**
	 * Ajoute un problème à la liste des problèmes que le modèle doit résoudre
	 * @param problem le problème à résoudre
	 * @return le temps pris par le modèle pour résoudre le problème
	 */
	public double addProblem(Problem problem) {
		problemList.add(problem);
		problem.solve();
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
	 * Simule une session de plusieurs problèmes pour un addend choisi
	 * @param addend l'addend des problèmes choisi
	 * @param charList la liste des caractères possibles comme augend
	 * @param feedback si le problème dispose d'un feedback ou pas
	 * @param nbSim le nombre de problème par session
	 * @param problemType le type de probleme ("free" ou "instructed")
	 * @return un tableau contenant le nombre d'erreurs et le temps total pris pour résoudre les 288 problèmes
	 */
	public double[] session(int addend, List<String> charList, boolean feedback, int nbSim, String problemType) {
		session++;
		printSession();
		int errors=0;
		double time=0;
		for(int a=0;a<288;a++) {
			String randomLetter = (String)charList.get(new Random().nextInt(charList.size()));
			String problemName = randomLetter + "+" + addend;
			if(problemType.equals("instructed")) {
				double random = Math.random();
				problemName=problemName+"="+((random<0.5)?((char)(randomLetter.charAt(0)+addend)):((char)(randomLetter.charAt(0)+addend+1)));
			}
			else if(!problemType.equals("free"))
				System.out.println("Problem type has to be \"instructed\" or \"free\".");
			try {
				Problem problem = new Problem(problemName ,this, feedback);
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
	 * Simule une session de plusieurs problèmes d'addends entre 2 et 5 d'augend de A à F
	 * @param charList la liste des caractères posssibles comme augend
	 * @param feedback si le problème dispose d'un feedback ou pas
	 * @param nbSim le nombre de simulation en une session
	 * @param problemType le type de probleme ("free" ou "instructed")
	 * @return un tableau du nombre d'erreur et du temps total pris par le modèle pour résoudre toute la session
	 */
	public double[] session(List<String> charList, boolean feedback, int nbSim, String problemType) {
		session++;
		List<String> allProblems = new ArrayList<>();
		for (String letter : charList) {
			for (int number = 2; number <= 5; number++) {
				allProblems.add(letter + "+" + number);
			}
		}
		List<String> shuffledProblems = new ArrayList<>(allProblems);
		Collections.shuffle(shuffledProblems);

		int problemIndex = 0;
		//printSession();
		int errors=0;
		double time=0;
		for(int a=0;a<nbSim;a++) {
			if (problemIndex >= shuffledProblems.size()) {
				// Une fois toutes les combinaisons utilisées, re-mélanger
				Collections.shuffle(shuffledProblems);
				problemIndex = 0;
			}
			String randomLetter = (String)charList.get(new Random().nextInt(charList.size()));
			int randomNumber = (int) ((Math.random() * (5 - 2 + 1)) + 2);
			String problemName = randomLetter + "+" + randomNumber;
			if(problemType.equals("free"))
				problemName = shuffledProblems.get(problemIndex++);
			if(problemType.equals("instructed")) {
				double random = Math.random();
				problemName=problemName+"="+((random<0.5)?((char)(randomLetter.charAt(0)+randomNumber)):((char)(randomLetter.charAt(0)+randomNumber+1)));
			}
			else if(!problemType.equals("free"))
				System.out.println("Problem type has to be \"instructed\" or \"free\".");
			try {
				Problem problem = new Problem(problemName ,this, feedback);
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
	 * Simule une session de 288 problèmes d'addends entre 2 et 5 d'augend de A à F en condition libre
	 * @param charList la liste des caractères posssibles comme augend
	 * @return un tableau du nombre d'erreur et du temps total pris par le modèle pour résoudre toute la session
	 */
	public double[] session(List<String> charList) {
		return session(charList, true, 288, "free");
	}

	/**
	 * Simule une session de plusieurs problèmes d'addends entre 2 et 5 d'augend de A à F en condition libre
	 * @param charList la liste des caractères posssibles comme augend
	 * @param nbSim le nombre de simulation par session
	 * @return un tableau du nombre d'erreur et du temps total pris par le modèle pour résoudre toute la session
	 */
	public double[] session(List<String> charList, int nbSim) {
		return session(charList, true, 288, "free");
	}

	/**
	 * Simule une session de 288 problèmes d'addends entre 2 et 5 d'augend de A à F en condition libre
	 * @param charList la liste des caractères posssibles comme augend
	 * @param feedback si les problèmes révèlent la réponse au modèle après résolution
	 * @return un tableau du nombre d'erreur et du temps total pris par le modèle pour résoudre toute la session
	 */
	public double[] session(List<String> charList, boolean feedback) {
		return session(charList, feedback, 288, "free");
	}

	/**
	 * Simule une session de 288 problèmes d'addends entre 2 et 5 d'augend de A à F
	 * @param charList la liste des caractères posssibles comme augend
	 * @param feedback si les problèmes révèlent la réponse au modèle après résolution
	 * @param problemType le type de problème à résoudre ("free" ou "instructed")
	 * @return un tableau du nombre d'erreur et du temps total pris par le modèle pour résoudre toute la session
	 */
	public double[] session(List<String> charList, boolean feedback, String problemType) {
		return session(charList, feedback, 288, problemType);
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


	public void setSwitchingCost(double switchingCost) {
		Parameters.switching_cost=switchingCost;
	}

	public void resetMemory() {
		// Réinitialiser la mémoire des réponses
		cleanAnswerMemory();

		// Réinitialiser la mémoire procédurale
		cleanProcedureMemory();

		// Réinitialiser la pratique des lettres
		for (int i = 0; i < practice.length; i++) {
			practice[i] = Parameters.initialPractice;
		}

		// Réinitialiser la liste des problèmes
		problemList.clear();

		// Réinitialiser la session
		session = 0;

		// Réinitialiser le coût de switching et stratégie précédente si nécessaire
		Parameters.switching_cost = 0;
		Parameters.previous_strat = 'p';
		Parameters.other_strat = 0;

		// Réinitialiser le profil (optionnel)
		// profil = null;
	}

	public void meanPractice(int nbAugend) {
		double[] practice = Arrays.copyOfRange(this.practice, 0, nbAugend);
		double sum = 0;
		for (double val : practice) {
			sum += val;
		}
		double mean = sum/practice.length;
		System.out.println("Moyenne de practice à la session " + session + " est de " + mean);

	}

}
