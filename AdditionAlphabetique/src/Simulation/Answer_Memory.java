package Simulation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import Exceptions.*;

/**
 * La classe Answer_Memory permet de créer la mémoire des réponses des problèmes arithmético-alphabétique
 * @author Mathias
 * @version 1.0
 */
public class Answer_Memory extends Model{

	private static final Answer_Memory instance = new Answer_Memory();
	Map<String, Answer> memory ;
	Map<String, Double> time;
	double[][] lastTimeAddend;

	/**
	 * Constructeur de la mémoire de réponses
	 */
	private Answer_Memory() {
		memory=new HashMap<String,Answer>();
		time=new HashMap<String,Double>();
		lastTimeAddend = new double[][] {{0,0},{0,0},{0,0},{0,0}};
	}

	/**
	 * Affiche le contenu de la mémoire des réponses
	 */
	public void printMemory() {
		System.out.println(memory);
	}

	/**
	 * Retourne la mémoire des réponses
	 * @return la Map des réponses
	 */
	public Map<String, Answer> getMemory(){
		return memory;
	}

	/**
	 * Retourne les derniers temps de résolution pour chaque problème rencontré
	 * @return la Map des derniers temps de résolution pour chaque problème rencontré
	 */
	public Map<String, Double> getTimes(){
		return time;
	}

	/**
	 * Cherche une réponse dans la mémoire des réponses associée à un chunk
	 * @param chunk le chunk à résoudre
	 * @return
	 * @throws CantRetrieveAnswerInAnswerMemoryException
	 */
	public String findAnswer(Chunk chunk) throws CantRetrieveAnswerInAnswerMemoryException{
		int other_strat=0;
		if(previous_strat=='p') {
			other_strat=1;
		}
		else {
			other_strat=0;
		}
		//Je reconstruis le nom du chunk à résoudre
		String problemName = chunk.letter + "+" + chunk.number;
		//Je crée une liste de taille 26 (26 lettres de l'alphabet)
		ArrayList<Answer>[] weightList = new ArrayList[26];
		for (int i = 0; i < 26; i++) { 
			weightList[i] = new ArrayList<Answer>(); 
		}
		//Je remplis cette liste de toutes les réponses mémorisées ainsi que leur poids à l'indice correspondant à la lettre (a->0 ; b->1 ...)
		for(Map.Entry<String, Answer> entry: memory.entrySet()) {
			weightList[Integer.valueOf(entry.getValue().getName().charAt(0))-97].add(new Answer(entry.getKey(), entry.getValue().getWeight()));
		}
		//Je calcule l'activation de chaque réponse
		Map<String, Double> activationMap = new HashMap<String, Double>();
		for(int i=0; i<26;i++) {
			if(weightList[i]==null || weightList[i].size()==0) {
				//J'ignore les cases où il n'y a pas de réponse
			}
			else {
				//J'ajoute la lettre (réponse potentielle) et son score d'activation
				activationMap.put(String.valueOf((char)(i+97)), activation(weightList[i],problemName));
			}
		}
		//Je récupère un seuil aléatoire issu d'une normale pour déterminer si une réponse peut être récupérée ou non
		Random random = new Random();
		double treshold = rt_mu + rt_sd* random.nextGaussian();
		List<String> potentialAnswers = new ArrayList<String>();
		for(Map.Entry<String, Double> entry: activationMap.entrySet()) {
			//Si on dépasse le seuil aléatoire alors la réponse est potentiellement la réponse à notre problème
			if(entry.getValue()>=treshold)
				potentialAnswers.add(entry.getKey());
		}
		//Si on n'a pas de réponse potentielle alors on retourne null
		if(potentialAnswers.size()==0)
			return null;
		//Si il n'y a qu'une seule réponse potentielle alors je calcule le temps pris pour récupérer cette réponse et je l'ajoute
		//au temps pris pour résoudre le chunk, puis le retourne la réponse trouvée
		else if(potentialAnswers.size()==1) {
			char answer = potentialAnswers.get(0).charAt(0);
			char augend = chunk.letter.charAt(0);
			String problemSolved = augend + "+" + (answer-augend);
			//System.out.println(memory);
			//System.out.println(problemName);
			double practice=0;
			if(memory.get(problemSolved)!=null)
				practice=memory.get(problemSolved).getPractice();
			
			chunk.addTime(n+t*Math.exp(-practice/p));
			return potentialAnswers.get(0);
		}
		else {
			//Si il y a plus d'une potentielle réponse, alors je stocke la probabilité de récupérer chaque réponse 
			Map<String, Double> probabilityMap = new HashMap<String, Double>();
			for(String potentialAnswer : potentialAnswers) {
				probabilityMap.put(potentialAnswer, probability(potentialAnswer,potentialAnswers,activationMap));
			}
			List<Double> probabilities = new ArrayList<Double>(probabilityMap.values());
			//Je trie les réponses en fonction de leur probabilité
			for(String potentialAnswer : potentialAnswers) {
				probabilities.add(probability(potentialAnswer,potentialAnswers,activationMap));
			}
			Collections.sort(probabilities);
			//Je ne regarde que leur probabilité, et je découpe toutes les probabilités en suivant une méthode softmax
			double[] probasArray = new double[probabilities.size()];
			for(int i=0;i<probasArray.length; i++) {
				probasArray[i]=probabilities.get(i);
			}
			for(int i=0;i<probasArray.length; i++) {
				if(i!=0)
					probasArray[i]=probasArray[i-1] + probasArray[i];
			}
			//Je pioche un nombre aléatoire entre 0 et 1
			double randomNumber = Math.random();
			//Je regarde dans quel cluster a atteri le nombre aléatoire
			for(int i=0;i<probasArray.length;i++) {
				if(randomNumber<=probasArray[i]) {
					//Je récupère la réponse qui est associée au cluster de probabilité dans lequel est tombé le nombre aléatoire
					//Je calcule le temps pris pour récupérer la réponse, je l'ajoute au temps du chunk 
					//et je retourne la réponse
					if(i!=0)
						for (Map.Entry<String, Double> entry : probabilityMap.entrySet()) {
							if (Math.abs(entry.getValue()-(probasArray[i]-probasArray[i-1])) <=0.01) {
								char answer = entry.getKey().charAt(0);
								char augend = chunk.letter.charAt(0);
								String problemSolved = augend + "+" + (answer-augend);
								double practice=0;
								if(chunk.problem.condition.equals("NCSC"))
									problemSolved = augend + "+" + ((answer-augend)/2);
								if(memory.get(problemSolved)!=null)
									practice=memory.get(problemSolved).getPractice();
								chunk.addTime(n+t*Math.exp(-practice/p));
								return entry.getKey();
							}
						}
					else for (Map.Entry<String, Double> entry : probabilityMap.entrySet()) {
						if (entry.getValue().equals(probasArray[i])) {
							char answer = entry.getKey().charAt(0);
							char augend = chunk.letter.charAt(0);
							String problemSolved = augend + "+" + (answer-augend);
							double practice=0;
							if(chunk.problem.condition.equals("NCSC"))
								problemSolved = augend + "+" + ((answer-augend)/2);
							if(memory.get(problemSolved)!=null)
								practice=memory.get(problemSolved).getPractice();
							chunk.addTime(n+t*Math.exp(-practice/p));
							return entry.getKey();
						}
					}
				}
			}
			//Sinon, je sais qu'il y a des réponses potentielles pour une raison quelconque, je ne peux récupérer la réponse (n'est pas censé arriver)
			throw new CantRetrieveAnswerInAnswerMemoryException("There are probabilty's answers superior than treshold on answer memory but i can't retrieve them.");
		}

	}

	/**
	 * Calcule la probabilité de récupérer une réponse
	 * @param a la réponse testée
	 * @param potentialAnswers la liste des réponses potentielles
	 * @param activationMap la liste des activations de toutes les réponses
	 * @return la probabilité de récupérer la réponse testée
	 */
	private double probability(String a, List<String> potentialAnswers, Map<String, Double> activationMap) {
		double somme = 0;
		for(String answer : potentialAnswers) {
			double activation = activationMap.get(answer);
			somme += Math.exp(decisionDeterminism*activation);
		}
		return (Math.exp(decisionDeterminism*activationMap.get(a))/somme);
	}

	/**
	 * Calcule l'activation de toute réponse en fonction du problème à résoudre
	 * @param weightList la liste des poids de toutes les réponses
	 * @param problemName le nom du problème (chunk) à résoudre
	 * @return l'activation de la réponse
	 */
	private double activation(List<Answer> weightList, String problemName) {
		double activation=0;
		for(Answer answer:weightList) {
			activation += answer.getWeight()*similarity(problemName,answer.getName());
		}	
		return activation;
	}

	/**
	 * Calcule la similarité entre deux problèmes
	 * @param problemName le problème qui ressemble au problème à résoure
	 * @param problemCurrentName le problème qui doit être résolu
	 * @return le score de similarité de deux problèmes
	 */
	private double similarity(String problemName, String problemCurrentName) {
		return Math.exp(-specificity*distance(problemName,problemCurrentName));
	}

	/**
	 * La distance entre deux problèmes
	 * @param problemName le problème qui ressemble au problème à résoure
	 * @param problemCurrentName le problème qui doit être résolu
	 * @return le score de similarité de deux problèmes
	 */
	private int distance(String problemName, String problemCurrentName) {
		int cpt=0;
		if(problemName.charAt(0)!=problemCurrentName.charAt(0))
			cpt++;
		if(problemName.charAt(1)!=problemCurrentName.charAt(1))
			cpt++;
		if(problemName.charAt(2)!=problemCurrentName.charAt(2))
			cpt++;
		return cpt*cpt;

	}

	/**
	 * Met en mémoire une réponse à un problème
	 * @param problemName le nom du problème résolu par la réponse
	 * @param answer la réponse du problème
	 * @param time le temps pris pour résoudre le problème
	 */
	protected void putMemory(String problemName, String answer, double time) {
		double weight = weightIncrease;
		Answer answerFromMemory = memory.get(problemName);
		if(answerFromMemory != null) {
			weight=weight+answerFromMemory.getWeight();
			answerFromMemory.setWeight(weight);
			answerFromMemory.practice();
		}
		else
			memory.put(problemName, new Answer(answer,weight));
		//Je fais la moyenne des temps pris pour résoudre ce problème et le stocke dans lastTimeAddend
		this.time.put(problemName, time);
		double howManyPracticed = lastTimeAddend[Integer.valueOf(problemName.charAt(2))-48-2][1];
		double lastTime = lastTimeAddend[Integer.valueOf(problemName.charAt(2))-48-2][0];
		lastTimeAddend[Integer.valueOf(problemName.charAt(2))-48-2]= new double[]{(lastTime*howManyPracticed+time)/(howManyPracticed+1),howManyPracticed+1};

	}

	/**
	 * Retourne l'unique instance de Answer_Memory
	 * @return l'instance courante de Answer_Memory
	 */
	public static Answer_Memory getInstance() {
		return instance;
	}

	protected class Answer{
		private String name;
		private double weight;
		private double assoStrength;
		private Answer(String name, double weight) {
			this.name=name;
			this.weight=weight;
			assoStrength=initialStrength;
		}
		public String toString() {
			return "(" + name + " | wei.:" + weight + " ; str.:" + assoStrength +")";
		}
		private String getName() {
			return name;
		}
		private double getWeight() {
			return weight;
		}
		private void setWeight(double weight) {
			this.weight=weight;
		}
		private void practice() {
			assoStrength= assoStrength+increaseStrength;
		}
		protected double getPractice() {
			return assoStrength;
		}
	}
	
	/**
	 * Réinitialise la mémoire de réponses
	 */
	public void clean() {
		memory=new HashMap<String,Answer>();
		time=new HashMap<String,Double>();
		lastTimeAddend = new double[][] {{0,0},{0,0},{0,0},{0,0}};
	}

	/**
	 * Retourne le contenu de la mémoire
	 */
	public String toString() {
		return memory.toString();
	}

	/**
	 * Récuère le temps moyen (appelé dernier temps) de résolution d'un problème d'addend fixé
	 * @param addend l'addend voulu
	 * @return le temps moyen de résolution d'un problème quelconque ayant l'addend demandé
	 */
	protected double getLastTime(int addend) {
		addend=addend-2;
		if(addend>4) 
			return 0;
		if(addend<0)
			return 0;
		return lastTimeAddend[addend][0];
	}
	
	public static void main(String[] args) {
	}
}
