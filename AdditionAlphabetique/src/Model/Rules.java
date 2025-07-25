package Model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Model.Procedure_Memory.*;

/**
 * La classe Rules permet de créer des règles de résolution d'un problème arithmético-alphabétique
 * @author Mathias DEVILLIERS
 * @version 1.0
 */
public class Rules{
	Map<cond[], Action> rules;
	
	private static final Rules instance = new Rules(Parameters.goodRule, Parameters.malRule);

	/**
	 * Constructeur pour initialiser les 5 règles de base
	 * @param goodRule la valeur du poids initial des bonnes règles
	 * @param malRule la valeur du poids initial des mauvauses règles
	 */
	private Rules(double goodRule, double malRule) {
		rules = new HashMap<cond[], Action>();
		//Une règle est une liste de conditions à vérifier, un nom et un poids
		rules.put(new cond[] {new eq<Boolean>("strategy", true), new eq<String>("operand","+"), new sup<Integer>("number",0)}, new Action("incrementOnlyLetter",malRule, false));
		rules.put(new cond[] {new eq<Boolean>("strategy", true),new eq<String>("operand", "+"), new sup<Integer>("number",0)}, new Action("increment", goodRule, true));
		rules.put(new cond[] {new eq<Boolean>("strategy", true),new eq<String>("operand","+"), new eq<Integer>("number",0)}, new Action("pop_goal",goodRule, true));
		rules.put(new cond[] {new eq<Boolean>("strategy", false),new inf<String>("production","answer")}, new Action("production", goodRule, true));
		rules.put(new cond[] {new eq<Boolean>("strategy", false),new infeq<String>("answer","production")}, new Action("answer", goodRule, true));

	}
	
	/**
	 * Réinitialise les règles
	 */
	public void clean() {
		rules = new HashMap<cond[], Action>();
		rules.put(new cond[] {new eq<Boolean>("strategy", true), new eq<String>("operand","+"), new sup<Integer>("number",0)}, new Action("incrementOnlyLetter",Parameters.malRule, false));
		rules.put(new cond[] {new eq<Boolean>("strategy", true),new eq<String>("operand", "+"), new sup<Integer>("number",0)}, new Action("increment", Parameters.goodRule, true));
		rules.put(new cond[] {new eq<Boolean>("strategy", true),new eq<String>("operand","+"), new eq<Integer>("number",0)}, new Action("pop_goal",Parameters.goodRule, true));
		rules.put(new cond[] {new eq<Boolean>("strategy", false),new inf<String>("production","answer")}, new Action("production", Parameters.goodRule, true));
		rules.put(new cond[] {new eq<Boolean>("strategy", false),new infeq<String>("answer","production")}, new Action("answer", Parameters.goodRule, true));
	}
	
	/**
	 * Retourne l'unique instance de Rules
	 * @return l'instance de Rules
	 */
	public static Rules getInstance() {
		return instance;
	}
	
	/**
	 * Retourne la Map des règles
	 * @return la Map des règles
	 */
	public Map<cond[], Action> getRules(){
		return rules;
	}
	
	/**
	 * Définit les poids des bonnes et des mauvaises règles
	 * @param goodRule le nouveau poids pour les bonnes règles
	 * @param malRule le nouveau poids pour les mauvaises règles
	 */
	public void setWeight(double goodRule, double malRule) {
		List<Action> actions = new ArrayList<Action>(rules.values());
		for(Action action : actions) {
			if(action.isGoodRule)
				action.setWeigth(goodRule);
			else
				action.setWeigth(malRule);
		}
	}
	
	/**
	 * Affiche le nom de chaque règle ainsi que son poids associé
	 * @return la nom de chaque règle ainsi que son poids associé
	 */
	public String toString() {
		String string ="";
		List<cond[]> key = new ArrayList<cond[]>(rules.keySet());
		for(cond[] condition : key) {
			string = string + rules.get(condition).name + "=" + rules.get(condition).weight + " / ";
		}
		return string;
	}
}
