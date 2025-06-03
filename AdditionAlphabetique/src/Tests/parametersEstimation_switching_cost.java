package Tests;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import Exceptions.ProblemException;
import Simulation.Model;
import Simulation.Problem;

public class parametersEstimation_switching_cost {





	public static void main(String[] args) {
		Model model = new Model();
		Map<Integer, Map<Integer, List<Double>>> time = new HashMap<Integer, Map<Integer, List<Double>>>();
		for(int i=1;i<5;i++) {
			Map<Integer, List<Double>> map = new HashMap<Integer, List<Double>>();
			for(int j=2;j<6;j++)
				map.put(j, new ArrayList<Double>());
			time.put(i,map);
		}
		for(int r=400;r<450;r++) {


			String csvFile = r+"switching_cost_problemsParameters.csv";
			File file = new File(csvFile);

			try {
				// Créer le fichier s'il n'existe pas
				if (!file.exists()) {
					file.createNewFile();
					System.out.println("Fichier créé : " + file.getAbsolutePath());
				}

				// Écriture dans le fichier
				try (FileWriter writer = new FileWriter(file)) {

					writer.append("switchingCost,addend,time,session\n");

					double timeBefore = System.currentTimeMillis();
					boolean oneSimulationDone=false;

					//0.6 et 0.7
					for(double switchingCost=0.0;switchingCost<2000.0;switchingCost=switchingCost+10.0) {
						for(int i=0;i<5;i++) {
							model.cleanAnswerMemory();
							model.cleanProcedureMemory();
							model=new Model(switchingCost);
							for(int session=1; session<5; session++) {
								for(int a=0;a<288;a++) {
									try {
										int randomLetter = (int) ((Math.random() * (107 - 101)) + 101);
										int randomNumber = (int) ((Math.random() * (5 - 2 + 1)) + 2);
										Problem problem = new Problem((char)(randomLetter) + "+" + randomNumber , model);
										double timeProblem = model.addProblem(problem);
										time.get(session).get(randomNumber).add(timeProblem);
									}
									catch(ProblemException e) {
										System.out.println(e + "\nProgram stopped.");
										return;
									}
								}													
							}
						}
						for(Integer sessions : time.keySet()) {
							Map<Integer, List<Double>> timeBySession = time.get(sessions);
							for(Integer addends : timeBySession.keySet()) {
								List<Double> times = timeBySession.get(addends);
								double meanTime = 0;
								for(int i=0;i<times.size();i++) {
									meanTime=meanTime+times.get(i);
								}
								meanTime=meanTime/times.size();
								writer.append(String.format(Locale.US, "%.2f,%d,%.2f,%d\n",
										switchingCost, addends, meanTime, sessions));
							}
						}
						//On nettoie la map des times
						time = new HashMap<Integer, Map<Integer, List<Double>>>();
						for(int i=1;i<5;i++) {
							Map<Integer, List<Double>> map = new HashMap<Integer, List<Double>>();
							for(int j=2;j<6;j++)
								map.put(j, new ArrayList<Double>());
							time.put(i,map);
						}

						if(!oneSimulationDone) {
							double timeAfter = System.currentTimeMillis();
							double timeOneSim = timeAfter - timeBefore;
							double timeTotal = timeOneSim/1000*10*2*4*4*5*4*4;
							System.out.println("La première simulation a été faite en " + timeOneSim + "ms, soit " + timeOneSim/1000 + "sec.\nLa simulation totale devra donc prendre environ " + timeTotal + "sec, soit " + (int)(timeTotal/3600) + "h" + (int)((timeTotal % 3600) / 60) +"min (modulo le multithreading).");
							long millis = System.currentTimeMillis();

							LocalDateTime dateTime = Instant.ofEpochMilli(millis)
									.atZone(ZoneId.systemDefault()) // ou ZoneId.of("Europe/Paris")
									.toLocalDateTime();

							int heures = dateTime.getHour();
							int minutes = dateTime.getMinute();
							int secondes = dateTime.getSecond();

							System.out.println("Il est actuellement " + heures + "h " + minutes + "min " + secondes + "s");
							oneSimulationDone=true;
						}

					}
				}







				System.out.println("Le fichier CSV a été écrit avec succès !");
				long millis = System.currentTimeMillis();

				LocalDateTime dateTime = Instant.ofEpochMilli(millis)
						.atZone(ZoneId.systemDefault()) // ou ZoneId.of("Europe/Paris")
						.toLocalDateTime();

				int heures = dateTime.getHour();
				int minutes = dateTime.getMinute();
				int secondes = dateTime.getSecond();


				System.out.println("Les simulations se sont finies à " + heures + "h " + minutes + "min " + secondes + "s");

			}

			catch (IOException e) {
				e.printStackTrace();
			}



		}
	}
}

