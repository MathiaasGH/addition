package Tests;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import Exceptions.ProblemException;
import Simulation.Model;
import Simulation.Problem;

public class NonContigueInstructed {
	public static void main(String[] args) {
		Model model = new Model();
		List<String> participant = new ArrayList<String>();
		List<String> augend= new ArrayList<String>();
		List<Integer> addend= new ArrayList<Integer>();
		List<String> response = new ArrayList<String>();
		List<String> trap = new ArrayList<String>();
		List<String> accuracy= new ArrayList<String>();
		List<Double> timeL = new ArrayList<Double>();
		List<String> profil = new ArrayList<String>();
		List<String> strategy = new ArrayList<String>();
		char[] letters = {'c', 'f', 'i'};

		//Rules.getInstance().setWeight(0.2, -0.8);
		double time = System.currentTimeMillis();
		for(int i=0;i<1000;i++) {
			model.cleanAnswerMemory();
			model.cleanProcedureMemory();
			if(i<500)
				model=new Model("breaker");
			else
				model=new Model("nonbreaker");
			//model.cleanAnswerMemory();
			//model.cleanProcedureMemory();
			//model=new Model();
			//System.out.println("*********************\nSESSION " + session + " \n*******************");
			for(int a=0;a<288;a++) {
				Random random = new Random();
				char randomLetter = letters[random.nextInt(letters.length)];
				int randomNumber = (int) ((Math.random() * (5 - 2 + 1)) + 2);
				try {
					String problemName = randomLetter + "+" + randomNumber;
					double rd = Math.random();
					problemName=problemName+"="+((rd<0.5)?((char)(randomLetter+randomNumber)):((char)(randomLetter+randomNumber+1)));
					Problem problem = new Problem(problemName , model);
					double timeProblem = model.addProblem(problem);
					participant.add((i+1)+"");
					augend.add(problem.getAugend());
					addend.add(randomNumber);
					response.add(problem.getTarget().toUpperCase());
					trap.add(problem.getTrapType());
					accuracy.add((problem.error()==true?"0":"1"));
					timeL.add(timeProblem);
					if(model.profil.equals("breaker"))
						profil.add("b");
					else
						profil.add("nb");
					strategy.add(problem.getStrategyUsed());

				}
				catch(ProblemException e) {
					System.out.println(e + "\nProgram stopped.");
				}
				//System.out.println(problem.getName() + " "  + problem.isRetrieved());
				//model.cleanAnswerMemory();
				//model.cleanProcedureMemory();
				//model=new Model();
			}
		}

		System.out.println(model);
		System.out.println("Took " + (System.currentTimeMillis() - time) + "ms");
		//int errors2=model.session(2);

		String csvFile = "problemNonContigueInstructed.csv";
		File file = new File(csvFile);

		model.printPractice();

		try {
			// Créer le fichier s'il n'existe pas
			if (!file.exists()) {
				file.createNewFile();
				System.out.println("Fichier créé : " + file.getAbsolutePath());
			}

			// Écriture dans le fichier
			try (FileWriter writer = new FileWriter(file)) {
				writer.append("Participant, Augend, Addend, Response, True, accuracy, rt, profil, strategy\n");
				// Écrire quelques lignes de données
				for(int i=0; i<addend.size();i++) {
					writer.append(participant.get(i) + "," + augend.get(i) + "," + addend.get(i) + "," + response.get(i) + "," + trap.get(i) + "," + accuracy.get(i) + "," + timeL.get(i)  + "," + profil.get(i) + "," + strategy.get(i) + "\n");
				}
			}
			System.out.println("Le fichier CSV a été écrit avec succès !");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

