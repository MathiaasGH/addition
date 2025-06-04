package Simulation;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import Exceptions.*;
import Simulation.Procedure_Memory.inf;

/**
 * La classe Procedure_Memory permet de gérer les règles utilisées et utilisables pour résoudre un problème (chunk)
 * @author Mathias
 * @version 1.0
 */
public class Procedure_Memory extends Model{
	private static final Procedure_Memory instance = new Procedure_Memory();
	Map<cond[], Action> rules;

	/**
	 * Constructeur de la classe Procedure_Memory
	 */
	private Procedure_Memory() {
		rules = Rules.getInstance().getRules();
	}

	/**
	 * Choisis la règle à appliquer sur le chunk courant
	 * @param chunk le chunk à résoudre
	 * @return le nom de la règle à utiliser
	 * @throws ConditionRuleException
	 * @throws NoSuchRuleException
	 */
	public String findAction(Chunk chunk) throws ConditionRuleException, NoSuchRuleException{
		//Je cherche les règles utilisables
		Map<Action, Double> suitableRules= findSuitableRules(chunk);
		//Je choisis une règle parmi les règles utilisables
		String actionChosen = actionChosen(suitableRules).getName();
		//System.out.println(actionChosen + " was chosen ." + chunk.letter + "+" + chunk.number + " chunk");
		return actionChosen;
	}

	/**
	 * Cherche des règles utilisables pour résoudre le chunk courant parmi toutes les règles utilisables
	 * @param chunk le chunk à résoudre
	 * @return toutes les règles utilisables ainsi que leur activation
	 * @throws ConditionRuleException
	 * @throws NoSuchRuleException
	 */
	public Map<Action, Double> findSuitableRules(Chunk chunk) throws ConditionRuleException, NoSuchRuleException{
		List<Action> suitableRules = new LinkedList<Action>();
		String operand=chunk.operand;
		String number=chunk.number;
		String letter=chunk.letter;
		//Je regarde si toutes les conditions de chaque règle matche avec le chunk courant ou pas
		for (cond[] conditions : rules.keySet()) {
			boolean match=false;
			for(cond individualCond : conditions) {
				String left = individualCond.op1;
				if(left.equals("operand"))
					left=operand;
				else if(left.equals("letter"))
					left=letter;
				else if(left.equals("number"))
					left=number;
				else if(left.equals("production"))
					left=String.valueOf(estimationTimeProduction(chunk));
				else if(left.equals("answer"))
					left=String.valueOf(estimationTimeAnswer(chunk));
				else if(left.equals("strategy"))
					left=chunk.isStrategyChosen();
				else
					throw new ConditionRuleException("Condition rule's usage : param1 : \"operand\", \"number\" or \"letter\" ; parmam2 : a number or an operand. Misuse on {" + individualCond.toString() + "} condition.");
				Object right = individualCond.op2;

				if(right instanceof String) {
					double rightValue = (right.equals("production")?
							Double.valueOf(estimationTimeProduction(chunk)) : 
								Double.valueOf(estimationTimeAnswer(chunk)));
					match=
/** Cas d'égalité */		(individualCond.getClass().getSimpleName().equals("eq"))?(left.equals((String)(right))): (
/** Cas de supériorité */	(individualCond.getClass().getSimpleName().equals("sup"))?(Double.valueOf(left)>rightValue): (
/** Cas d'inferiorité*/		(individualCond.getClass().getSimpleName().equals("inf"))?(rightValue>Double.valueOf(left)):
												(individualCond.getClass().getSimpleName().equals("infeq"))?(rightValue>=Double.valueOf(left)): false));

					if(right.equals("answer")) {
						if(rightValue==-1 || Double.valueOf(left)==-1)
							match=true;
					}
					else if(right.equals("production")) {
						if(rightValue==-1 || Double.valueOf(left)==-1) 
							match=false;
					}
				}

				else if(right instanceof Integer) {
					match=
/** Cas d'égalité */		(individualCond.getClass().getSimpleName().equals("eq"))?(Integer.valueOf(left)==(Integer)right): (
/** Cas de supériorité */	(individualCond.getClass().getSimpleName().equals("sup"))?(Integer.valueOf(left)>(Integer)right): (
/** Cas d'inferiorité*/		(individualCond.getClass().getSimpleName().equals("inf"))?((Integer)right>Integer.valueOf(left)):
												(individualCond.getClass().getSimpleName().equals("infeq"))?(Integer)right>=Integer.valueOf(left):false));
				}
				else if(right instanceof Boolean) {
					boolean bLeft = left.equals("true")?true:false;
					match= (bLeft==(Boolean)right);
				}
				else 
					throw new ConditionRuleException("A condition has to be an eq, a sup, a inf or a infeq condition. Misuse on {" + individualCond.toString() + "} condition.");
				if(!match)
					//Si au moins une condition de la règle courante ne matche pas avec le chunk, alors ce n'est pas une règle utilisable
					break;
			}
			if(match)
				//Si toutes les conditions de la règle courante matchent avec le chunk, alors c'est une règle utilisable
				suitableRules.add(rules.get(conditions));
		}
		//Je calcule l'activation de chaque règle utilisable
		Map<Action, Double> suitableRulesActivations = new HashMap<Action, Double>();
		double sumExpActivation = 0;
		for(Action action : suitableRules) {
			double activation =  activation(action, suitableRules);
			suitableRulesActivations.put(action, activation);
			sumExpActivation = sumExpActivation + Math.exp(activation*decisionDeterminism) ;
		}
		for(Action action : suitableRules) {
			double activation = suitableRulesActivations.get(action);
			suitableRulesActivations.put(action, retrievalProbability(activation, suitableRules, sumExpActivation));
		}
		return suitableRulesActivations;
	}

	/**
	 * Choisi une règle à utiliser parmi toutes les règles utilisables
	 * @param suitableRules
	 * @return
	 * @throws NoSuchRuleException
	 */
	private Action actionChosen(Map<Action, Double> suitableRules) throws NoSuchRuleException {
		List<Double> probas = new LinkedList(suitableRules.values());
		List<Action> actions = new LinkedList(suitableRules.keySet());
		List<Double> sortedProbas = new LinkedList();
		List<Action> sortedActions= new LinkedList();
		double minProba = Double.MAX_VALUE;
		int idxMin=0;
		int currentIdx=0;
		//Je trie mes listes d'action et de probabilité à la main en fonction de leur proba
		//Je me permets de trier à la main car j'ai peu d'actions. Si on rajoute des actions à l'avenir,
		//Il faudrait réfléchir à une meilleure solution de tri
		while(!probas.isEmpty()) {
			minProba = Double.MAX_VALUE;
			idxMin=0;
			currentIdx=0;
			for(Double proba : probas) {
				if(proba<minProba) {
					minProba=proba;
					idxMin=currentIdx;
				}
				currentIdx++;
			}
			sortedProbas.add(probas.remove(idxMin));
			sortedActions.add(actions.remove(idxMin));
		}
		//Je transforme la distribution des probabilités en distribution cumulative (je clusterise)
		double[] probasArray = new double[sortedProbas.size()];
		for(int i=0;i<probasArray.length; i++) {
			probasArray[i]=sortedProbas.get(i);
		}
		for(int i=0;i<probasArray.length; i++) {
			if(i!=0)
				probasArray[i]=probasArray[i-1] + probasArray[i];
			if(Double.isNaN(probasArray[i])) {
				probasArray[i]=0.0;
			}
		}
		//Je choisis un nombre aléatoire entre 0 et 1
		double randomNumber = Math.random();
		for(int i=0;i<probasArray.length;i++) {
			//Je regarde dans quel cluster est tombé le nombre aléatoire
			if(randomNumber<=probasArray[i]) {
				//Je choisis donc l'action correspondante au cluster choisi
				return sortedActions.get(i);
			}
		}

		throw new NoSuchRuleException("No rule was found.");
	}

	/**
	 * Modifie les poids des règles passées en paramètres
	 * @param rules les règles à modifier
	 * @param error 1 si la réponse était une erreur, 0 sinon
	 */
	protected void modifyWeigth(List<String> rules, int error) {
		for (Action action:this.rules.values()) {
			if(rules.contains(action.getName()))
				action.setWeigth(action.getWeight()+error*errorDiscount);
		}
	}

	/**
	 * Calcule la probabilité de récupérer une règle en fonction du seuil d(activation, et des règles utilisables
	 * @param activation le seuil d'activation
	 * @param suitableRules les règles utilisables
	 * @param sumExpActivation la somme des exponentielles des activations
	 * @return la probabilité de récupérer une règle
	 */
	private double retrievalProbability(double activation, List<Action> suitableRules, double sumExpActivation) {
		if(Double.isInfinite(Math.exp(decisionDeterminism*activation)) && Double.isInfinite(sumExpActivation))
			return 1;
		else if(Double.isInfinite(sumExpActivation))
			return 0.01;
		else if(Double.isInfinite(Math.exp(decisionDeterminism*activation)))
			return 1;
		return Math.exp(decisionDeterminism*activation)/sumExpActivation;
	}

	/**
	 * Caclule l'activation d'une règle en fonction des règles utilisables
	 * @param action la règle dont on doit calculer l'activation
	 * @param suitableRules les règles utilisables
	 * @return l'activation de la règle
	 */
	private double activation(Action action, List<Action> suitableRules) {
		boolean isGoodRule = action.isGoodRule;
		double mean=0;
		int total=0;
		for(Action act : suitableRules) {
			if(act.isGoodRule == isGoodRule) {
				mean=mean+act.getWeight();
				total++;
			}
		}
		return mean/total;
	}

	/**
	 * Retourne l'unique instance de Procedure_Memory
	 * @return l'instance courante
	 */
	public static Procedure_Memory getInstance() {
		return instance;
	}

	/**
	 * Réinitialise les règles
	 */
	public void clean() {
		Rules.getInstance().clean();
		rules = Rules.getInstance().getRules();
	}

	public static class cond<type> {
		String op1;
		type op2;
		protected cond(String op1, type op2) {
			this.op1=op1;
			this.op2=op2;
		}

		public String toString() {
			return op1 + " noCondType " + op2;
		}
		public boolean equals(cond condition) {
			return condition.op1.equals(op1) && op2.equals(condition.op2);
		}
	}
	public static class eq<type> extends cond{
		protected eq(String op1, type op2) {
			super(op1,op2);
		}
		public String toString() {
			return op1 + " = " + op2;
		}
	}
	public static class inf<type> extends cond{
		protected inf(String op1, type op2) {
			super(op1,op2);
		}
		public String toString() {
			return op1 + " < " + op2;
		}
	}
	public static class infeq<type> extends cond{
		protected infeq(String op1, type op2) {
			super(op1,op2);
		}
		public String toString() {
			return op1 + " <= " + op2;
		}
	}
	protected static class sup<type> extends cond{
		protected sup(String op1, type op2) {
			super(op1,op2);
		}
		public String toString() {
			return op1 + " > " + op2;
		}
	}
	public static class Action {
		String name;
		double weight;
		boolean isGoodRule;
		protected Action(String name, double weight, boolean isGoodRule) {
			this.name=name;
			this.weight=weight;
			this.isGoodRule=isGoodRule;
		}
		protected String getName() {
			return name;
		}
		protected double getWeight() {
			return weight;
		}
		protected void setWeigth(double newWeight){
			weight=newWeight;
		}
		public String toString() {
			return name + "/ " + weight + ((isGoodRule)?"/ good-rule":"/ mal-rule");
		}
		protected boolean isGoodRule() {
			return isGoodRule;
		}

	}

	/**
	 * Estime le temps de récupérer une réponse en passant par la mémoire des réponses
	 * @param chunk le chunk à résoudre
	 * @return le temps estimé à résoudre ce chunk en passant par la mémoire des réponses
	 */
	private double estimationTimeAnswer(Chunk chunk) {
		double practice=0;
		if(previous_strat=='p') {
			other_strat=1;
		}
		else other_strat=0;
		//System.out.print(previous_strat);
		if(Answer_Memory.getInstance().getMemory().get(chunk.letter + "+" + chunk.number)!=null && Answer_Memory.getInstance().getMemory().get(chunk.letter + "+" + chunk.number).size()!=0) {
			//Si au moins une réponse existe dans la mémoire de réponse, je calcule le temps que ca prendrait pour la récupérer (moyenne des réponses existantes)
			List<Answer_Memory.Answer> ans = Answer_Memory.getInstance().getMemory().get(chunk.letter + "+" + chunk.number);
			int nbAnswer = 0;
			for(Answer_Memory.Answer answer : ans) {
				practice = practice + answer.getPractice();
				nbAnswer++;
			}
			practice=practice/nbAnswer;
			return n+t*Math.exp(-practice/p) + elementEncoding + motorCommand + comparison + switching_cost*other_strat;
		}
		else {
			//Sinon j'estime que le temps que ca me prendrait c'est le temps moyen que je prends a calculer le probleme
			Map<String, Double> times = Answer_Memory.getInstance().getTimes();
			if(times == null)
				return -1;
			if(times.get(chunk.letter+"+"+chunk.number) == null || times.get(chunk.letter+"+"+chunk.number)==0)
				return -1;
			
			return times.get(chunk.letter+"+"+chunk.number) + elementEncoding + motorCommand + comparison + switching_cost*(other_strat-1)*(-1);
		}

	}

	/**
	 * Estime le temps de récupérer une réponse en passant par la mémoire procédurale
	 * @param chunk le chunk à résoudre
	 * @return le temps estimé à résoudre ce chunk en passant par la mémoire procédurale
	 */
	private double estimationTimeProduction(Chunk chunk) {
		if(previous_strat=='p') {
			other_strat=0;
		}
		else other_strat=1;
		
		//Si je ne l'ai jamais travaillé, je ne sais pas
		if(Answer_Memory.getInstance().getLastTime(Integer.valueOf(chunk.number)) == 0)
			return -1;
		//Sinon c'est le temps moyen pris pour résoudre un problème de même addend
		return Answer_Memory.getInstance().getLastTime(Integer.valueOf(chunk.number))+switching_cost*other_strat;
	}

	/**
	 * Retourne le contenu des règles
	 */
	public String toString() {
		return Rules.getInstance().toString();
	}

}


