package Simulation;
import Exceptions.*;

/**
 * La classe Chunk permet d'initialiser un chunk à résoudre
 * @author Mathias
 * @version 1.0
 */
public class Chunk extends Model{

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

		if(!letter.equals("z") && !letter.equals("y"))
			solve();
		else {
			problem.setAnswer(letter, time);
		}

	}

	/**
	 * Résoud le chunk en choisissant la stratégie à adopter (passer par la mémoire procédurale ou passer par la mémoire des réponses)
	 */
	private void solve() {
		try {
			//Je choisis la stratégie à adopter
			String productionRule = Procedure_Memory.getInstance().findAction(this);
			if(productionRule.equals("production") || productionRule.equals("answer"))
				strategyChosen=true;
			//Je résouds en conséquences
			if(productionRule.equals("production")) {
				production();
			}
			else if(productionRule.equals("answer")) {
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
			if(production.equals("pop_goal"))
				//Je définis la réponse
				problem.setAnswer(letter, time);
			else if(production.equals("increment")) {
				//if(problem.condition.equals("CSC"))
					//Je crée un nouveau chunk avec la lettre voisine, en décrémentant le compteur et en ajoutant le temps pris pour incrémenter
					new Chunk(String.valueOf((char)(letter.charAt(0)+1)), String.valueOf(Integer.valueOf(number)-1), null, time + problem.getModel().getTime(letter, problem), operand, problem);
				//else 
				//	new Chunk(String.valueOf((char)(letter.charAt(0)+2)), String.valueOf(Integer.valueOf(number)-1), null, time + problem.getModel().getTime(letter), operand, problem);
			}
			else if(production.equals("incrementOnlyLetter")) {
				//if(problem.condition.equals("CSC"))
					//Je crée un nouveau chunk avec la lettre voisine en ajoutant le temps pris pour incrémenter
					new Chunk(String.valueOf((char)(letter.charAt(0)+1)), number, null, time + problem.getModel().getTime(letter, problem), operand, problem);
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
				String directAnswer = Answer_Memory.getInstance().findAnswer(this);
				if(directAnswer == null || directAnswer.length()==0 || directAnswer.equals(letter) || directAnswer.charAt(0)< number.charAt(0)) {
					//Si je n'ai pas trouvé la réponse, je calcule via la mémoire procédurale
					production();
				}
				else {
					problem.receiveAction("answerRetrieved");
					//if(problem.condition.equals("CSC")) {
						//Je donne au problème le sous problème que je viens de résoudre
						problem.addRetrieved(letter+"+"+(Integer.valueOf(number) - ((int)(letter.charAt(0)-96) + (Integer.valueOf(number)) - ((int)(directAnswer.charAt(0)-96))))+"="+directAnswer);
						problem.isAnswerRetrieved="yes";
						//Si j'ai trouvé une réponse, je calcule le pas entre la nouvelle réponse et l'augend pour savoir de combien j'ai avancé, puis je crée le nouvel augend
						int newNumber = Integer.valueOf(number)-(Math.abs(Integer.valueOf(letter.charAt(0)) - Integer.valueOf(directAnswer.charAt(0))));
						//Et je crée un nouveau chunk avec cette différence
						if(newNumber<0)
							new Chunk(directAnswer, String.valueOf(0), null, time + 0, operand, problem);
						else
							new Chunk(directAnswer, String.valueOf(newNumber), null, time + 0, operand, problem);
						return;
					//}
					/*else {
						//Je donne au problème le sous problème que je viens de résoudre
						problem.addRetrieved(letter+"+"+ (Integer.valueOf(number) - (((int)(letter.charAt(0)-96) + 2*(Integer.valueOf(number))) - ((int)(directAnswer.charAt(0)-96)))/2) + "=" + directAnswer);
						problem.isAnswerRetrieved="yes";
						//Si j'ai trouvé une réponse, je calcule le pas entre la nouvelle réponse et l'augend pour savoir de combien j'ai avancé, puis je crée le nouvel augend
						int newNumber = Integer.valueOf(number)-(Math.abs(Integer.valueOf(letter.charAt(0)) - Integer.valueOf(directAnswer.charAt(0))))/2;
						//Et je crée un nouveau chunk avec cette différence
						if(newNumber<0)
							new Chunk(directAnswer, String.valueOf(0), null, time + 0, operand, problem);
						else
							new Chunk(directAnswer, String.valueOf(newNumber), null, time + 0, operand, problem);
						return;
					}*/
				}
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
		return String.valueOf(strategyChosen);
	}
}

