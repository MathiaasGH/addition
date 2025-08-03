package Model;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import Exceptions.*;

/**
 * La classe Problem permet de créer un problème arithmético-alphabétique
 * @author Mathias DEVILLIERS
 * @version 1.0
 */
public class Problem {

	boolean feedback;
	String problemType;
	String name;
	String letterAnswer;
	String instructedAnswer;
	double time;
	Model model;
	List<String> usedRules;
	char leftOperand;
	char rightOperand;
	char operator;
	char target;
	String strategyChosen;
	List<String> historyRetrieved;
	double decisionTime;

	/**
	 * Constructeur d'un problème
	 * @param name la chaine de caractère du problème (ex : "A+3")
	 * @param model le modèle qui doit résoudre le problème
	 * @throws ProblemException 
	 */
	public Problem(String name, Model model) throws ProblemException {
		this(name, model, true);
	}

	/**
	 * Constructeur d'un problème
	 * @param name la chaine de caractère du problème (ex : "A+3")
	 * @param model le modèle qui doit résoudre le problème
	 * @param feedback si le modèle a un feedback de la bonne réponse ou non
	 * @throws ProblemException 
	 */
	public Problem(String name, Model model, boolean feedback) throws ProblemException {
		this.feedback=feedback;
		historyRetrieved= new ArrayList<String>();
		this.name=name;
		time=0;
		letterAnswer=null;
		this.model=model;
		strategyChosen="null";
		usedRules= new ArrayList<String>();
		try {
			initialize();
		}
		catch(ProblemException e) {
			System.out.println(e.getMessage());
		}
	}
	
	/**
	 * Résouds le problème
	 */
	public void solve() {
		//Je crée un chunk avec l'augend, l'opérateur, l'addend, la réponse (null au début), le temps (0 au début) et je le relis à ce problème
		new Chunk(String.valueOf(leftOperand), String.valueOf(rightOperand), letterAnswer, time, String.valueOf(operator), this);
	}

	/**
	 * Initialise les paramètres du problèmes
	 * @throws ProblemException quand le nom du problème ne correspond par aux normes d'un problème (ex : "F%Z" n'est pas un problème)
	 */
	private void initialize() throws ProblemException {
		if(name.length()>5) {
			throw new ProblemNameException("Usage : \"LetterOpNumber\". Example : \"A+3\"\nError on " + name);
		}
		else {
			if(name.length()==3 || name.length()==5) {
				problemType=name.length()==3?"free":"instructed";
				//Je récupère l'augend, l'addend et l'opérateur
				leftOperand = name.charAt(0);
				operator = name.charAt(1);
				rightOperand= name.charAt(2);
				//Si l'augend n'est pas une lettre je lève une excpetion
				if(leftOperand<65 || (leftOperand>90 && leftOperand<97) || leftOperand>122) 
					throw new ProblemNameException("Usage : First character of the problem " + name + " has to be a letter. Example : \"A..\" ");
				//Si l'opérateur n'est ni un +, ni un -, ni un *, ni un / alors je lève une exception
				if(operator != '+' && operator != '-' && operator != '*' && operator != '/')
					throw new ProblemNameException("Usage : Second character of the problem " + name + " has to be an operand (+,-,*,/). Example : \".+.\"");
				//Si l'addend n'est pas un nombre entre 1 et 5, alors je lève une exception
				if(rightOperand<48 || rightOperand>53) 
					throw new ProblemNameException("Usage : Third character of the problem " + name + " has to be a number. Example : \"..3\" ");
				if(name.length()==5) {
					//Si le quatrième caractère n'est pas un '=' je lève une exception
					if(name.charAt(3)!='=')
						throw new ProblemNameException("Usage : Fourth character of the problem " + name + " has to be an equals sign. Example : \"...=.\" ");
					target=name.charAt(4);
					//Si la lettre cible n'est pas une lettre je lève une exception
					if(target<65 || (target>90 && target<97) || target>122) 
						throw new ProblemNameException("Usage : Fifth character of the problem " + name + " has to be a letter. Example : \"....C\" ");				
				}
			}
			else if(name.length()<3){
				//Un nom de problème ne doit pas dépasser 3 lettres (".+.")
				throw new ProblemNameException("Usage : \"LetterOpNumber\". Example : \"A+3\"\nError on " + name);
			}
		}
	}


	/**
	 * Définit la réponse au problème lorsque le problème est résolu en mémorisant la réponse
	 * @param answer la réponse trouvée
	 * @param time le temps pris pour résoudre le problème
	 */
	protected void setAnswer(String answer, double time) {
		setAnswer(answer,time,true);
	}

	/**
	 * Définit la réponse au problème lorsque le problème est résolu en précisant si l'on veut mémoriser la réponse
	 * @param answer la réponse trouvée
	 * @param time le temps pris pour résoudre le problème
	 * @paramr memorizing si l'on mémorise la réponse ou pas
	 */
	protected void setAnswer(String answer, double time, boolean memorizing) {
		if(problemType.equals("free"))
			this.letterAnswer=answer;
		else{
			this.letterAnswer=String.valueOf(answer.charAt(0));
			this.instructedAnswer=answer.substring(1);
		}
		this.time=time;
		endProblem(memorizing);
	}

	/**
	 * Affiche la résolution du problème et ajuste la mémoire de réponse et la mémoire procédurale en conséquences
	 * @param memorizing boolean si on est allé jusqu'au bout de la résolution du problème (en cas de problème de type instructed, TRUE par défaut)
	 */
	public void endProblem(boolean memorizing) {
		time = time + Parameters.elementEncoding + Parameters.motorCommand + Parameters.comparison + decisionTime;
		//Commenter la ligne suivante pour retirer l'affichage de la réponse
		endMessage(memorizing);
		Procedure_Memory.getInstance().modifyWeigth(usedRules, error()?-1:1);
		if(memorizing) {
			if(feedback) {
				if(!error())
					Answer_Memory.getInstance().putMemory(leftOperand+"+"+rightOperand, letterAnswer, strategyChosen, time-decisionTime);
			}
			else
				Answer_Memory.getInstance().putMemory(leftOperand+"+"+rightOperand, letterAnswer, strategyChosen, time-decisionTime);
		}
		//Retirer le commentaire suivant pour afficher la trace
		//System.out.println(trace());
	}

	/**
	 * Affiche un message de résolution du problème
	 * @param memorizing boolean si on est allé jusqu'au bout de la résolution du problème (en cas de problème de type instructed, TRUE par défaut)
	 */
	public void endMessage(boolean memorizing) {
		if(problemType.equals("free"))
			System.out.println("Problem solved : " + name + "=" +  letterAnswer + " calculated in " + time + "ms " + (error()?"❌":"✅") + (strategyChosen.equals("production")?" by producing.":(" by retrieving " + historyRetrieved() + ".")));
		else
			System.out.println("Problem solved : " + name + " is " + instructedAnswer + " found in " + time + "ms " + (error()?"❌":"✅") +  (strategyChosen.equals("production")?(" by producing " + (memorizing?(leftOperand+"+"+rightOperand+"="+letterAnswer):("until " + target + " and realizing task was not finished, so " + leftOperand + "+" + rightOperand + "≠" + target + "."))):(" by retrieving " + historyRetrieved() + ".")) + " The trap type was " + getTrapType() + " and profil was " + model.profil);
	}

	/**
	 * Retourne "t" ou "t+1" en fonction du piège du problème. A n'utiliser que si le problème est de type "instructed"
	 * @return "t" ou "t+1"
	 */
	public String getTrapType() {
		return ((leftOperand+Integer.valueOf(rightOperand)-48)==target?"t":((leftOperand+Integer.valueOf(rightOperand)-48)==(target+1)?"t+1":((leftOperand+Integer.valueOf(rightOperand)-48)==(target-1)?"t-1":"other")));
	}

	/**
	 * Retourne la target du problème. A n'utiliser que si le problème est de type "instructed"
	 * @return la target
	 */
	public String getTarget() {
		return target+"";
	}


	/**
	 * Retourne le modèle qui résoud ce problème
	 * @return le modèle auquel est rattaché le problème
	 */
	protected Model getModel() {
		return model;
	}

	/**
	 * Retourne le nom du problème
	 * @return le nom du problème
	 */
	public String getName() {
		if(problemType.equals("instructed"))
			return leftOperand + "+" + rightOperand + "=" + target;
		return name;
	}

	/**
	 * Retourne le temps pris pour résoudre le problème
	 * @return le temps pris pour résoudre le problème
	 */
	protected double gtTime() {
		return time;
	}

	/**
	 * Reçoit et stocke une action de la mémoire procédurale dans la liste de toutes les actions (règles) utilisées
	 * @param action l'action à utiliser
	 */
	protected void receiveAction(String action) {
		usedRules.add(action);
	}

	/**
	 * Retourne si la réponse est juste ou pas
	 * @return si la réponse trouvée est correcte ou non
	 */
	public boolean error() {
		if(problemType.equals("free"))
			return !String.valueOf((char)(Integer.valueOf(leftOperand)+Integer.valueOf(String.valueOf(rightOperand)))).equals(letterAnswer);
		else {
			return ((char)(leftOperand+(Integer.valueOf(rightOperand)-48))==target?(instructedAnswer.equals("true")?false:true):(instructedAnswer.equals("false")?false:true));
		}
	}

	/**
	 * Retourne l'addend du problème
	 * @return l'addend du problème
	 */
	public char getAddend() {
		return rightOperand;
	}

	/**
	 * Affiche le nom du problème ainsi que sa réponse trouvée
	 */
	public String toString() {
		return name + "=" + letterAnswer;
	}

	/**
	 * Retourne l'historique de toutes les règles utilisées pour résoudre ce problème
	 * @return la liste de toutes les règles utilisées
	 */
	public List<String> trace() {
		return usedRules;
	}

	/**
	 * Retourne l'affichage de tous les bouts de réponse récupérés depuis la mémoire de réponse
	 * @return une chaine de caractères
	 */
	public String historyRetrieved() {
		StringBuilder retour = new StringBuilder();
		int size = historyRetrieved.size();
		for (int i = 0; i < size; i++) {
			retour.append(historyRetrieved.get(i));
			if (i < size - 1) {
				retour.append(", then ");
			}
		}
		return retour.toString();
	}


	/**
	 * Ajoute un bout de réponse récupéré depuis la mémoire de réponses dans l'historique de tous les bouts 
	 * de réponse récupérés
	 * @param str
	 */
	public void addRetrieved(String str) {
		historyRetrieved.add(str);
	}

	/**
	 * Retourne l'historique de tous les bouts de réponse récupéré depuis la mémoire de réponse
	 * @return l'historique de tous les bouts de réponse
	 */
	public List<String> getHistoryRetrieved(){
		return historyRetrieved;
	}

	/**
	 * Retourne la stratégie utilisée pour résoudre le problème
	 * @return la stratégie utilisée pour résoudre le problème
	 */
	public String getStrategyUsed() {
		return strategyChosen;
	}

	/**
	 * Retourne la réponse sous format lettre
	 * @return la réponse sous format lettre
	 */
	public String getLetterAnswer() {
		return letterAnswer;
	}

	/**
	 * Retourne la réponse sous format boolean (à n'utiliser qu'en cas de problème de type instructed)
	 * @return
	 */
	public String getInsructedAnswer() {
		return instructedAnswer;
	}

	/**
	 * Retourne le profil du participant
	 * @return le profil du participant
	 */
	public String getProfil() {
		return model.profil;
	}

	/**
	 * Retourne l'augend du problème (= la lettre de début du problème)
	 * @return l'augend du problème
	 */
	public String getAugend() {
		return (leftOperand+"").toUpperCase();
	}
	
	/**
	 * Calcule un pourcentage de chevauchement de ce problème par rapport au tableau du comptage connu
	 * @return le pourcentage de chevauchement par rapport aux autres problèmes travaillés
	 */
	public double overlap() {
		int idx = Integer.valueOf(leftOperand)-'a'+1;
		int step = Integer.valueOf(rightOperand)-Integer.valueOf('0');
		double totalPractice = 0;
		double[] practiceTab = model.practice;
		for(int i=0; i<step; i++, idx++) {
			totalPractice  = totalPractice + practiceTab[idx-1];
		}
		double maxPractice = maxTab(practiceTab);		
		if(maxPractice==0)
			return 0;
		return ((double)(totalPractice)+0.0)/(maxPractice*step)*100;
	}
	
	/**
	 * Retourne la valeur maximale d'un tableau de double
	 * @param tab le tableau 
	 * @return la valeur maximale du tableau
	 */
	public static double maxTab(double[] tab) {
		if(tab == null || tab.length==0)
			return 0;
		double max = tab[0];
		for(double nb : tab) {
			if(nb>=max) {
				max=nb;
			}
		}
		return max;
	}
	
	/**
	 * Affiche des statistiques à propos de la résolution de ce problème
	 */
	public void statistics() {
		System.out.println("Name : " + name + "\nProblem type : " + problemType + "\nAnswer : " + letterAnswer + (problemType.equals("instructed")?(" then " + instructedAnswer):("")) + "\nTime : " + time + "\nStrategy chosen : " + strategyChosen);
	}
}
