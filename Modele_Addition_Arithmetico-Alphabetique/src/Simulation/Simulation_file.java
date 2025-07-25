package Simulation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import Exceptions.ProblemException;
import Model.Model;
import Model.Problem;

/**
 * Classe permettant de simuler des participants en lisant des problèmes issus de vrais participants (à utiliser à partir de l'application R)
 * @author Mathias DEVILLIERS
 */
public class Simulation_file {
	
	public static void main(String[] args) {
		//J'initialise le model et les listes dans lesquels je stocke les informations que je relèverai
		Model model = new Model();
		List<Double> timeList= new ArrayList<Double>();
		List<Boolean> errorList= new ArrayList<Boolean>();
		List<Character> addendList= new ArrayList<Character>();
		List<Integer> sessionList= new ArrayList<Integer>();
		List<String> participantList = new ArrayList<String>();
		List<String> strategyList = new ArrayList<String>();
		List<String> profilList = new ArrayList<String>();
		List<Double> overlapList= new ArrayList<Double>();
		List<String> augendList = new ArrayList<String>();
		List<Integer> runList = new ArrayList<Integer>();

		String simulationType = "all";
		String file_name = "defaultname_file";
		String[] parameters={"0.5","3750","40","800","325","0.0018","0.4","0.9"};
		int nb_breakers=0;
		int nb_nonbreakers=0;
		int nb_run=0;
		//J'initialise une map des problèmes des participants
		Map<String, Map<String,List<String>>> map_participant = new LinkedHashMap<String, Map<String,List<String>>>();
		if(args.length==4) {
			String filePath = args[0];
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

			String noticeConstruction = notice.toString();
			//Pour chaque participant, 
			String[] notice_per_participant = noticeConstruction.split("new_participant");
			System.out.println("size_fill_map:" + notice_per_participant.length);
			for(int i=1; i<notice_per_participant.length; i++) {
				//Je récupère les problèmes par session
				String[] cut_by_sess = notice_per_participant[i].split("new_session");
				String[] cut_problems = cut_by_sess[1].split("new_problems");
				if(!map_participant.containsKey(cut_by_sess[0])) {

					Map<String, List<String>> map_problems = new LinkedHashMap<String, List<String>>();
					map_problems.put(cut_problems[0], Arrays.asList(cut_problems[1].split(",")));

					map_participant.put(cut_by_sess[0], map_problems);
				}
				else {
					map_participant.get(cut_by_sess[0]).put(cut_problems[0], Arrays.asList(cut_problems[1].split(",")));
				}
				System.out.println("fill_map:" + i);
			}
			file_name=args[2];
			String parameter_string = args[1];
			parameters = parameter_string.split(",");
			String nb_participant = args[3];
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
		}
		//Je définis les paramètres
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


		List<String> participantID = new ArrayList<String>(map_participant.keySet());
		//Je simule les breakers autant de fois que R m'a demandé et pour chaque participant et fais chaque run
		if(simulationType.equals("breakers") || simulationType.equals("all")) {
			for(int i=startB;i<endB;i++) {
				for(int run=1; run<=nb_run; run++) {
					model.cleanAnswerMemory();
					model.cleanProcedureMemory();
					model=new Model("breaker");
					String participant;
					int nbSess;
					participant = participantID.get(i);
					nbSess = map_participant.get(participant).keySet().size()+1;
					for(int session=1; session<nbSess; session++) {
						model.session=session;
						List<String> problems_list;
						int nbProblems;

						problems_list = (List<String>)(map_participant.get(participant).get(""+session));
						nbProblems = problems_list.size();


						for (int a = 0; a < nbProblems; a++) {
							String problemString = problems_list.get(a);
							try {
								Problem problem = new Problem(problemString, model);
								overlapList.add(problem.overlap());
								double timeProblem = model.addProblem(problem);
								timeList.add(timeProblem);
								errorList.add(problem.error());
								addendList.add(problem.getAddend());
								sessionList.add(session);
								participantList.add(participant);
								strategyList.add(problem.getStrategyUsed());
								augendList.add(problem.getAugend());
								profilList.add(model.getProfil());
								runList.add(run);
							} catch (ProblemException e) {
							}
						}
					}
				}
				System.out.println("nb_participant:" + (Integer.valueOf(i)+1));
			}
		}

		//Je simule les non-breakers autant de fois que R m'a demandé et pour chaque participant et fais chaque run
		if(simulationType.equals("nonbreakers") || simulationType.equals("all")) {
			if(simulationType.equals("all")) {
				startNB = endB;
				endNB = endNB+endB;
			}
			for(int i=startNB;i<endNB;i++) {
				for(int run=1; run<=nb_run; run++) {
					model.cleanAnswerMemory();
					model.cleanProcedureMemory();
					model=new Model("breaker");
					String participant;
					int nbSess;
					participant = participantID.get(i);
					nbSess = map_participant.get(participant).keySet().size()+1;
					for(int session=1; session<nbSess; session++) {
						model.session=session;
						List<String> problems_list;
						int nbProblems;

						problems_list = (List<String>)(map_participant.get(participant).get(""+session));
						nbProblems = problems_list.size();


						for (int a = 0; a < nbProblems; a++) {
							String problemString = problems_list.get(a);
							try {
								Problem problem = new Problem(problemString, model);
								overlapList.add(problem.overlap());
								double timeProblem = model.addProblem(problem);
								timeList.add(timeProblem);
								errorList.add(problem.error());
								addendList.add(problem.getAddend());
								sessionList.add(session);
								participantList.add(participant);
								strategyList.add(problem.getStrategyUsed());
								augendList.add(problem.getAugend());
								profilList.add(model.getProfil());
								runList.add(run);
							} catch (ProblemException e) {
								System.out.println(e + "\nProgram stopped.");
							}
						}
					}
				}
				System.out.println("nb_participant:" + (Integer.valueOf(i)+1));
			}
		}
		
		String csvFile = "./simulationFiles/" + file_name + ".csv";
		File file = new File(csvFile);

		model.printPractice();

		try {
			//Je crée le fichier
			if (!file.exists()) {
				file.createNewFile();
			}

			//J'écris les données que j'ai récolté dans le fichier
			try (FileWriter writer = new FileWriter(file)) {
				writer.append("Augend, Addend, Overlap, rt, error, session, participant, run, strategy, profil\n");
				for(int i=0; i<addendList.size();i++) {
					writer.append(augendList.get(i) + "," +addendList.get(i) + "," + overlapList.get(i) + "," + timeList.get(i) + "," + errorList.get(i) + "," + sessionList.get(i) + "," + participantList.get(i) + "," + runList.get(i) + "," + strategyList.get(i) + "," + profilList.get(i) + "\n");
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

