package Simulation;
import java.util.Arrays;

import Exceptions.*;

/**
 * La classe Chunk permet d'initialiser un chunk à résoudre
 * @author Mathias
 * @version 1.0
 */
public class Chunk {

	String letter;
	String number; 
	String answer;
	double time;
	String operand;
	Problem problem;
	boolean strategyChosen=false;

	/**
	 * Constructeur d'un chunk
	 * @param letter la lettre (augend) du chunk à résoudre
	 * @param number le nombre (addend) du chunk à résoudre
	 * @param answer la réponse trouvée au chunk
	 * @param time le temps pris pour résoudre ce chunk
	 * @param operand l'operateur du chunk (+)
	 * @param problem le problème auquel est rattaché le chunk
	 */
	public Chunk(String letter, String number, String answer, double time, String operand, Problem problem) {
		//System.out.println("New chunk created " + number + " " + letter);
		this.letter=letter;
		this.number=number;
		this.answer=answer;
		this.time=time;
		this.operand=operand;
		this.problem=problem;

		if(!letter.equals("z"))
			solve();
		else {
			problem.receiveAction("z reach");
			problem.setAnswer(letter, time);
		}

	}

	/**
	 * Résoud le chunk en choisissant la stratégie à adopter (passer par la mémoire procédurale ou passer par la mémoire des réponses)
	 */
	private void solve() {
		try {
			//Je choisis la stratégie à adopter
			if(problem.strategyChosen.equals("null")) {
				problem.strategyChosen = Procedure_Memory.getInstance().findAction(this);
			}
			//Je résouds en conséquences
			if(problem.strategyChosen.equals("production")) {
				production();
			}
			else if(problem.strategyChosen.equals("answer")) {
				retrieveAnswer();
			}

		}
		catch(ConditionRuleException e) {
			System.out.println(e.getMessage());
		}
		catch(NoSuchRuleException e) {
			System.out.println(e.getMessage());

		}
	}

	/**
	 * Résoud le chunk en passant par la mémoire procédurale
	 */
	protected void production() {
		try {
			//Je cherche la règle à utiliser et je l'ajoute à l'historique des règles de mon problème
			String production = Procedure_Memory.getInstance().findAction(this);
			problem.receiveAction(production);
			if(production.equals("pop_goal")) {
				//Je définis la réponse
				if(problem.problemType.equals("instructed")) {
					boolean target_reached = (char)(letter.charAt(0))==problem.target;
					if(!target_reached) 
						problem.setAnswer(letter +"false", time);
					else
						problem.setAnswer(letter +"true", time);

				}
				else 
					problem.setAnswer(letter, time);
			}
			else if(production.equals("increment") || production.equals("incrementOnlyLetter")){
				Parameters.previous_strat='p';
				if(problem.problemType.equals("instructed")) {
					//Je regarde si la target est atteinte
					boolean target_reached = (char)(letter.charAt(0)+1)==problem.target;
					//Je regarde si l'addend vaut 0
					boolean is_addend_null = String.valueOf(Integer.valueOf(number)-1).equals("0");
					if((target_reached && !is_addend_null)) {
						problem.setAnswer(letter + "false", time, false);
						return;
					}
				}
			}
			if(production.equals("increment")) {	
				//Je crée un nouveau chunk avec la lettre voisine, en décrémentant le compteur et en ajoutant le temps pris pour incrémenter
				new Chunk(String.valueOf((char)(letter.charAt(0)+1)), String.valueOf(Integer.valueOf(number)-1), null, time + problem.getModel().getTime(letter), operand, problem);
				//else 
				//	new Chunk(String.valueOf((char)(letter.charAt(0)+2)), String.valueOf(Integer.valueOf(number)-1), null, time + problem.getModel().getTime(letter), operand, problem);
			}
			else if(production.equals("incrementOnlyLetter")) {
				//Je crée un nouveau chunk avec la lettre voisine en ajoutant le temps pris pour incrémenter
				new Chunk(String.valueOf((char)(letter.charAt(0)+1)), number, null, time + problem.getModel().getTime(letter), operand, problem);
				//else
				//	new Chunk(String.valueOf((char)(letter.charAt(0)+2)), number, null, time + problem.getModel().getTime(letter), operand, problem);
			}
		}
		catch(ConditionRuleException e) {
			System.out.println(e.getMessage());
		}
		catch(NoSuchRuleException e) {
			System.out.println(e.getMessage());

		}
	}

	/**
	 * Résoud un chunk en passant par la mémoire des réponses
	 */
	protected void retrieveAnswer() {
		try {
			if(!number.equals("0")) {
				//Si l'addend n'est pas 0 alors je cherche la réponse dans la mémoire de réponses
				String directAnswer[] = Answer_Memory.getInstance().findAnswer(this);
				if(directAnswer == null || directAnswer[0].length()==0 || directAnswer[0].equals(letter) || directAnswer[0].charAt(0)< number.charAt(0)) {
					//Si je n'ai pas trouvé la réponse, je calcule via la mémoire procédurale
					problem.strategyChosen="production";
					production();
				}
				else {
					String ans = directAnswer[0];
					String probResolved = directAnswer[1];
					Parameters.previous_strat='a';
					problem.receiveAction("answerRetrieved");
					//Si je dois trouver la réponse
					//Je donne au problème le sous problème que je viens de résoudre
					problem.addRetrieved(probResolved+"="+ans);
					if(problem.problemType.equals("free")) {
						//Si j'ai trouvé une réponse, je calcule le pas entre la nouvelle réponse et l'augend pour savoir de combien j'ai avancé, puis je crée le nouvel augend
						//int newNumber = Integer.valueOf(number)-(Math.abs(Integer.valueOf(letter.charAt(0)) - Integer.valueOf(ans.charAt(0))));
						//Et je crée un nouveau chunk avec cette différence
						//if(newNumber<0)
							new Chunk(ans, String.valueOf(0), null, time + 0, operand, problem);
						//else
						//	new Chunk(ans, String.valueOf(newNumber), null, time + 0, operand, problem);
						return;
					}
					//Si je dois comparer une réponse
					else {
						String instructedAnswer = ans.equals(problem.target+"")?"true":"false";
						problem.setAnswer(ans+instructedAnswer, time);
					}
				}
			}
			else {
				//Pour pop_goal
				production();
			}
		}
		catch(CantRetrieveAnswerInAnswerMemoryException e) {
			System.out.println(e.getMessage());
		}
	}

	/**
	 * Ajoute du temps au temps actuel pris pour résoudre le chunk
	 * @param time le temps à ajouter
	 */
	protected void addTime(double time) {
		this.time = this.time + time;
	}

	/**
	 * Retourne la stratégie choisie pour résoudre le chunk
	 * @return la stratégie choisie
	 */
	protected String isStrategyChosen() {
		return problem.strategyChosen.equals("null")?"false":"true";
	}
}

