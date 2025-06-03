package Simulation;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import Exceptions.*;

/**
 * La classe Problem permet de créer un problème arithmético-alphabétique
 * @author Mathias
 * @version 1.0
 */
public class Problem extends Model{
	
	String name;
	String answer;
	double time;
	Model model;
	List<String> usedRules;
	char leftOperand;
	char rightOperand;
	char operator;
	String strategyChosen;
	List<String> historyRetrieved;
	
	/**
	 * Constructeur d'un problème
	 * @param name la chaine de caractère du problème (ex : "A+3")
	 * @param model le modèle qui doit résoudre le problème
	 * @throws ProblemException 
	 */
	public Problem(String name, Model model) throws ProblemException {
		if(profil.equals("breaker")) {
			switching_cost=100;
		}
		else switching_cost=1000;
		historyRetrieved= new ArrayList<String>();
		this.name=name;
		time=0;
		answer=null;
		this.model=model;
		strategyChosen="null";
		usedRules= new ArrayList<String>();
		try {
		createChunk();
		}
		catch(ProblemException e) {
			System.out.println(e.getMessage());
		}
	}
	
	/**
	 * Créer un chunk à partir du problème courant
	 * @throws ProblemException quand le nom du problème ne correspond par aux normes d'un problème (ex : "F%Z" n'est pas un problème)
	 */
	private void createChunk() throws ProblemException {
		//Un nom de problème ne doit pas dépasser 3 lettres (".+.")
		if(name.length()>3) {
			throw new ProblemNameException("Usage : \"LetterOpNumber\". Example : \"A+3\"");
		}
		else {
			//Je récupère l'augend, l'addend et l'opérateur
			leftOperand = name.charAt(0);
			operator = name.charAt(1);
			rightOperand= name.charAt(2);
			//Si l'augend n'est pas une letre je lève une excpetion
			if(leftOperand<65 || (leftOperand>90 && leftOperand<97) || leftOperand>122) 
				throw new ProblemNameException("Usage : First character of the problem " + name + " has to be a letter. Example : \"A..\" ");
			//Si l'opérateur n'est ni un +, ni un -, ni un *, ni un / alors je lève une exception
			if(operator != '+' && operator != '-' && operator != '*' && operator != '/')
				throw new ProblemNameException("Usage : Middle character of the problem " + name + " has to be an operand (+,-,*,/). Example : \".+.\"");
			//Si l'addend n'est pas un nombre entre 1 et 5, alors je lève une exception
			if(rightOperand<48 || rightOperand>53) 
				throw new ProblemNameException("Usage : Last character of the problem " + name + " has to be a number. Example : \"..3\" ");
			//Je crée un chunk avec l'augend, l'opérateur, l'addend, la réponse (null au début), le temps (0 au début) et je le relis à ce problème
			new Chunk(String.valueOf(leftOperand), String.valueOf(rightOperand), answer, time, String.valueOf(operator), this);
		}
	}
	

	/**
	 * Définit la réponse au problème lorsque le problème est résolu
	 * @param answer la réponse trouvée
	 * @param time le temps pris pour résoudre le problème
	 */
	protected void setAnswer(String answer, double time) {
		this.answer=answer;
		this.time=time;
		endProblem();
	}
	
	/**
	 * Affiche la résolution du problème et ajuste la mémoire de réponse et la mémoire procédurale en conséquences
	 */
	public void endProblem() {
		time = time + elementEncoding + motorCommand + comparison;
		System.out.println("Problem solved : " + name + "=" +  answer + " calculated in " + time + "ms " + (error()?"❌":"✅") + (strategyChosen.equals("production")?" by producing.":(" by retrieving " + historyRetrieved() + ".")));
		Procedure_Memory.getInstance().modifyWeigth(usedRules, error()?-1:1);
		if(!error())
			Answer_Memory.getInstance().putMemory(name, answer, time);
		//System.out.println(trace());
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
		return !String.valueOf((char)(Integer.valueOf(leftOperand)+Integer.valueOf(String.valueOf(rightOperand)))).equals(answer);
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
		return name + "=" + answer;
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
	
	public String getStrategyUsed() {
		return strategyChosen;
	}
	
	public String getAnswer() {
		return answer;
	}

	public String getProfil() {
		return profil;
	}
}
