package Simulation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import Model.*;
import Exceptions.*;

/**
 * Classe permettant de simuler des participants à partir de la description d'une tâche expérimentale.
 * A utiliser à partir de l'application R
 * @author Mathias DEVILLIERS
 */
public class Simulation_nofile {
	public static void main(String[] args) {
		//J'initialise le modèle et les listes dans lesquels je vais stocker les données que je vais récolter
		Model model = new Model();
		List<Double> timeList= new ArrayList<Double>();
		List<Double> overlapList= new ArrayList<Double>();
		List<Boolean> errorList= new ArrayList<Boolean>();
		List<Integer> addendList= new ArrayList<Integer>();
		List<String> augendList= new ArrayList<String>();
		List<Integer> sessionList= new ArrayList<Integer>();
		List<String> participantList = new ArrayList<String>();
		List<String> strategyList = new ArrayList<String>();
		List<String> profilList = new ArrayList<String>();
		List<String> trap = new ArrayList<String>();
		List<Integer> blocList = new ArrayList<Integer>();

		int nb_session=0;
		List<List<String>> problem_list = new ArrayList<List<String>>();
		List<Integer> nb_bloc = new ArrayList<Integer>();
		String simulationType = "all";
		String file_name = "defaultname_file";
		String[] parameters={"0.5","3750","40","800","325","0.0018","0.4","0.9"};
		int nb_breakers=0;
		int nb_nonbreakers=0;
		int nb_run=0;

		if(args.length==6) {	
			nb_session = Integer.valueOf(args[0]);
			String filePath = args[1];
			StringBuilder notice = new StringBuilder();
			//Je récupère la notice de construction des problèmes des participants par session que R m'a envoyé
			try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
				String line;
				while ((line = br.readLine()) != null) {
					notice.append(line);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			String problem_list_string = notice.toString();
			
			//Je récupère les problèmes par bloc que R m'a transmit
			String[] problem_list_tab = problem_list_string.split("new_sess");
			String nb_bloc_string = args[2];
			String[] nb_bloc_tab = nb_bloc_string.split(",");
			for(int i=0;i<nb_bloc_tab.length;i++) {
				nb_bloc.add(Integer.valueOf(nb_bloc_tab[i]));
			}
			
			//Je recrée le set original de problèmes (par bloc et par session) qui me servira a simuler des personnes
			file_name = args[3];
			parameters = args[4].split(",");
			System.out.println("size_problem_list:" + problem_list_tab.length);
			for(int i=0; i<problem_list_tab.length; i++) {
				problem_list.add(new ArrayList<String>());
				String[] probs = problem_list_tab[i].split(",");
				for(int j=0; j<probs.length;j++) {
					if(i!=0)
						if(j!=0)
							problem_list.get(i).add(probs[j]);
						else {

						}
					else {
						problem_list.get(i).add(probs[j]);

					}
				}
				System.out.println("problem_list:" + i);
			}

			String nb_participant = args[5];
			String[] nb_particpant_split = nb_participant.split(",");
			nb_breakers = Integer.valueOf(nb_particpant_split[0]);
			nb_nonbreakers = Integer.valueOf(nb_particpant_split[1]);
			nb_run = Integer.valueOf( nb_particpant_split[2]);
			if(nb_breakers!=0 & nb_nonbreakers!=0)
				simulationType="all";
			else if(nb_breakers!=0)
				simulationType="breakers";
			else if(nb_nonbreakers!=0)
				simulationType="nonbreakers";
			else
				simulationType="other";
		}
		else {
			return;
		}
		//Je définis les paramètres du modèle
		double inceasePractice = Double.valueOf(parameters[0]);
		double t = Double.valueOf(parameters[1]);
		double p = Double.valueOf(parameters[2]);
		double b = Double.valueOf(parameters[3]);
		double d = Double.valueOf(parameters[4]);
		double rationality = Double.valueOf(parameters[5]);
		double counting_reinforcement = Double.valueOf(parameters[6]);
		double retrieving_reinforcement = Double.valueOf(parameters[7]);

		model.setincreasePractice(inceasePractice);
		model.setRationality(rationality);
		model.setReinforcementParam(counting_reinforcement, retrieving_reinforcement);
		model.setTimeParam(t, p, b, d);

		int startB=0;
		int endB=nb_breakers;
		int startNB=0;
		int endNB=nb_nonbreakers;
		//Je simule chaque participant à partir des problèmes du jeu de base que j'ai construit en randomisant les problèmes (breakers)
		if(simulationType.equals("breakers") || simulationType.equals("all")) {
			for(int i=startB;i<endB;i++) {
				for(int run=1; run<=nb_run; run++) {
					model.cleanAnswerMemory();
					model.cleanProcedureMemory();
					model=new Model("breaker");
					for(int session=1; session<nb_session+1; session++) {
						int nb_bloc_current_session = nb_bloc.get(session-1); 
						int bloc=1;
						model.session=session;
						List<String> shuffledProblems;
						shuffledProblems = new ArrayList<>(problem_list.get(session-1));
						Collections.shuffle(shuffledProblems);
						int problemIndex = 0;
						for (int a = 0; a < shuffledProblems.size()*nb_bloc_current_session; a++) {
							if (problemIndex >= shuffledProblems.size()) {
								//Je remélange pour créer un nouveau bloc
								Collections.shuffle(shuffledProblems);
								problemIndex = 0;
								bloc++;
							}
							String problemString = shuffledProblems.get(problemIndex++);
							try {
								Problem problem = new Problem(problemString, model);
								overlapList.add(problem.overlap());
								double timeProblem = model.addProblem(problem);
								timeList.add(timeProblem);
								errorList.add(problem.error());
								addendList.add(Integer.parseInt(problemString.split("\\+")[1].charAt(0)+""));
								sessionList.add(session);
								participantList.add("participant " + i);
								strategyList.add(problem.getStrategyUsed());
								profilList.add(model.getProfil());
								augendList.add(problem.getAugend());
								trap.add(problem.getTrapType());
								blocList.add(bloc);
							} catch (ProblemException e) {
								System.out.println(e + "\nProgram stopped.");
							}
						}
					}
				}
				System.out.println("nb_participant:" + (Integer.valueOf(i)+1));
			}
		}

		//Je simule chaque participant à partir des problèmes du jeu de base que j'ai construit en randomisant les problèmes (non-breakers)
		if(simulationType.equals("nonbreakers") || simulationType.equals("all")) {
			if(simulationType.equals("all")) {
				startNB = endB;
				endNB = endNB+endB;
			}
			for(int i=startNB;i<endNB;i++) {
				for(int run=1; run<=nb_run; run++) {
				model.cleanAnswerMemory();
				model.cleanProcedureMemory();
				model=new Model("nonbreaker");
				for(int session=1; session<nb_session+1; session++) {
					int nb_bloc_current_session = nb_bloc.get(session-1); 
					int bloc=1;
					model.session=session;
					List<String> shuffledProblems;
					shuffledProblems = new ArrayList<>(problem_list.get(session-1));
					Collections.shuffle(shuffledProblems);
					int problemIndex = 0;
					for (int a = 0; a < shuffledProblems.size()*nb_bloc_current_session; a++) {
						if (problemIndex >= shuffledProblems.size()) {
							//Je remélange pour créer un nouveau bloc
							Collections.shuffle(shuffledProblems);
							problemIndex = 0;
							bloc++;
						}

						String problemString = shuffledProblems.get(problemIndex++);
						try {
							Problem problem = new Problem(problemString, model);
							overlapList.add(problem.overlap());
							double timeProblem = model.addProblem(problem);
							timeList.add(timeProblem);
							errorList.add(problem.error());
							addendList.add(Integer.parseInt(problemString.split("\\+")[1].charAt(0)+""));
							sessionList.add(session);
							participantList.add("participant " + i);
							strategyList.add(problem.getStrategyUsed());
							profilList.add(model.getProfil());
							augendList.add(problem.getAugend());
							trap.add(problem.getTrapType());
							blocList.add(bloc);
						} catch (ProblemException e) {
						}
					}
				}
			}
				System.out.println("nb_participant:" + (Integer.valueOf(i)+1));

			}

		}
		String csvFile = "./simulationFiles/" + file_name + ".csv";
		File file = new File(csvFile);

		try {
			// Je crée le fichier
			if (!file.exists()) {
				file.createNewFile();
			}

			//J'écris les données récoltées dans le fichier
			try (FileWriter writer = new FileWriter(file)) {
				writer.append("Augend, Addend, Bloc, Overlap, rt, error, True, session, participant, strategy, profil\n");
				for(int i=0; i<addendList.size();i++) {
					writer.append(augendList.get(i) + "," + addendList.get(i) + "," + blocList.get(i) + "," + overlapList.get(i) + "," + timeList.get(i) + "," + errorList.get(i) + "," + trap.get(i) + "," + sessionList.get(i) + "," + participantList.get(i) + "," + strategyList.get(i) + "," + profilList.get(i) + "\n");
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
