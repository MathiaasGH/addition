package Tests;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import Exceptions.ProblemException;
import Simulation.Model;
import Simulation.Problem;

public class Contigue {
	public static void main(String[] args) {
		Model model = new Model();
		List<Double> timeList= new ArrayList<Double>();
		List<Boolean> errorList= new ArrayList<Boolean>();
		List<Integer> addendList= new ArrayList<Integer>();
		List<Integer> sessionList= new ArrayList<Integer>();
		List<String> participantList = new ArrayList<String>();
		List<String> strategyList = new ArrayList<String>();
		List<String> profilList = new ArrayList<String>();


		//Rules.getInstance().setWeight(0.2, -0.8);
		double time = System.currentTimeMillis();
		for(int i=0;i<100;i++) {
			model.cleanAnswerMemory();
			model.cleanProcedureMemory();
			model=new Model("breaker");
			for(int session=1; session<11; session++) {
				model.session=session;
				//model.cleanAnswerMemory();
				//model.cleanProcedureMemory();
				//model=new Model();
				//System.out.println("*********************\nSESSION " + session + " \n*******************");
				for(int a=0;a<288;a++) {
					try {
					int randomLetter = (int) ((Math.random() * (107 - 101)) + 101);
					int randomNumber = (int) ((Math.random() * (5 - 2 + 1)) + 2);
					Problem problem = new Problem((char)(randomLetter) + "+" + randomNumber , model);
					double timeProblem = model.addProblem(problem);
					timeList.add(timeProblem);
					errorList.add(problem.error());
					addendList.add(randomNumber);
					sessionList.add(session);
					participantList.add("participant " + i);
					strategyList.add(problem.getStrategyUsed());
					profilList.add(model.getProfil());
					}
					catch(ProblemException e) {
						System.out.println(e + "\nProgram stopped.");
						return;
					}
					
					//System.out.println(problem.getName() + " "  + problem.isRetrieved());
				}
				//model.cleanAnswerMemory();
				//model.cleanProcedureMemory();
				//model=new Model();
			}
		}
		
		System.out.println("Took " + (System.currentTimeMillis() - time) + "ms");
		//int errors2=model.session(2);

		String csvFile = "problemContigue.csv";
		File file = new File(csvFile);

		try {
			// Créer le fichier s'il n'existe pas
			if (!file.exists()) {
				file.createNewFile();
				System.out.println("Fichier créé : " + file.getAbsolutePath());
			}

			// Écriture dans le fichier
			try (FileWriter writer = new FileWriter(file)) {
				writer.append("addend, time, error, session, particpant, strategy, profil\n");
				// Écrire quelques lignes de données
				for(int i=0; i<addendList.size();i++) {
					writer.append(addendList.get(i) + "," + timeList.get(i) + "," + errorList.get(i) + "," + sessionList.get(i) + "," +  participantList.get(i) + "," + strategyList.get(i) + "," + profilList.get(i) + "\n");
				}
			}

			System.out.println("Le fichier CSV a été écrit avec succès !");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}