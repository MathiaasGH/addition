package Model;

import java.util.ArrayList;
import java.util.List;

import Exceptions.ProblemException;

/**
 * Classe pour apprendre les bases du modèle
 * @author Mathias DEVILLIERS
 * @version 1.0
 */
public class Exemples_model {
	public static void main(String[] args) {
		//Pour créer un participant, j'instancie un modèle (ici, je spécifie son profil "breaker").
		//To create a participant, instantiate a model (here, I specify its “breaker” profile).
		Model participant = new Model("breaker");
		try {
			//Pour créer un problem, je spécifie son énoncé et je le rattache à un participant. Attention : toutes les lettres doivent être écrites en minuscule.
			//To create a problem, I specify its statement and link it to a participant. Please note: all letters must be written in lowercase.
			Problem problem = new Problem("a+2=c", participant);
			//Pour résoudre un probleme, je l'ajoute à la pile des problèmes à résoudre d'un participant.
			//To resolve a problem, I add it to a participant's stack of issues to be resolved.
			participant.addProblem(problem);
			//Je peux afficher des statistiques relatives à un problem résolu.
			//I can print some statistics about a resolved problem.
			problem.statistics();
			//Je peux simuler des sessions de 288 problèmes personnalisés avec les addends 2,3,4,5 (avec feedback et de type "instructed" (instructed = "a+2=c" vs free = "a+2")).
			//I can simulate 288 sessions of customized problems with the following addends : 2,3,4,5 (with feedback and "instructed" type (instructed = "a+2=c" vs free = "a+2")).
			//Pour créer des sessions plus personnalisées, vous pouvez 1) les créer à la main ou 2) utiliser l'application réalisée disponible dans le dépôt Git (R)
			//To create more personalized sessions, you can either 1) create them manually or 2) use the application available in the Git repository (R).
			participant.session(new ArrayList<String>(List.of("a", "b")), true, "instructed");
			//Je peux réinitialiser le participant.
			//I can reset the participant.
			participant.cleanAnswerMemory();
			participant.cleanProcedureMemory();
			participant=new Model("breaker");
			//Je peux faire plusieurs autres choses et jouer avec le modèle, mais je pour cela, je vous laisse explorer les packages et adopter le code :)
			//I can do several other things and play around with the model, but for that, I'll let you explore the packages and adopt the code :)
			
		} catch (ProblemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
