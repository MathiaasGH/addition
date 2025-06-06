package Estimations;

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
import java.util.Random;

import Exceptions.ProblemException;
import Simulation.Model;
import Simulation.Problem;

public class parametersInstructed_3{


	public static void main(String[] args) {
		int lastPrintedProgress = -1;
		int iterationCount = 0;
		//int totalIterations = 11 * 11 * 20 * 20 * 4;

	    int totalParamComb = 2 * 11 * 20 * 20; // = 48,400
	    int totalLines = totalParamComb * 4;   // 193,600 lignes CSV

	  
		int totalIterations = totalLines;

		Model model = new Model();
		char addend;
		double timeProblem;
		int err;
		String participant;
		Map<String, Double> timeMap = new HashMap<String,Double>();
		Map<String, Integer> errorMap = new HashMap<String, Integer>();
		int[] nbPassages = new int[4];
		char[] letters = {'c', 'f', 'i'};

		String csvFile = "estimationInstructed3.csv";
		File file = new File(csvFile);

		try {
			// Créer le fichier s'il n'existe pas
			if (!file.exists()) {
				file.createNewFile();
				System.out.println("Fichier créé : " + file.getAbsolutePath());
			}

			// Écriture dans le fichier
			try (FileWriter writer = new FileWriter(file)) {
				writer.append("decisionDeterminism, errorDiscount, weightIncrease, increaseStrength, Addend, accuracy, rt\n");
				double timeBefore = System.currentTimeMillis();
				boolean oneSimulationDone=false;

				//0.6 et 0.7
//				for(double decisionDeterminism=0.5; decisionDeterminism<=1.5; decisionDeterminism=Math.round((decisionDeterminism+0.1)*10.0)/10.0) {
				for(double decisionDeterminism=0.9; decisionDeterminism<=1.0; decisionDeterminism=Math.round((decisionDeterminism+0.1)*10.0)/10.0) {

				for(double errorDiscount=0;errorDiscount<=0.1;errorDiscount=Math.round((errorDiscount+0.01)*100.0)/100.0){
						for(double wi=0.1;wi<=2;wi=Math.round((wi+0.1)*10.0)/10.0) {
							for(double incrStrength=0.1;incrStrength<=2;incrStrength=Math.round((incrStrength+0.1)*10.0)/10.0) {
								for(int i=0;i<10;i++) {
									model.cleanAnswerMemory();
									model.cleanProcedureMemory();
									model=new Model(decisionDeterminism, errorDiscount, wi, 0, 1, 0, incrStrength, "breaker");
									for(int a=0;a<288;a++) {
										try {
											char randomLetter = letters[new Random().nextInt(letters.length)];
											int randomNumber = (int) ((Math.random() * (5 - 2 + 1)) + 2);
											String problemName = randomLetter + "+" + randomNumber;
											double rd = Math.random();
											problemName=problemName+"="+((rd<0.5)?((char)(randomLetter+randomNumber)):((char)(randomLetter+randomNumber+1)));
											Problem problem = new Problem(problemName , model);
											timeProblem = model.addProblem(problem);
											addend = problem.getAddend();
											err = (problem.error()==true?1:0);
											if(nbPassages[addend-2-48]!=0) {
												double time = timeMap.get(addend+"");
												timeMap.put(addend+"", time+timeProblem);
												int error = errorMap.get(addend+"");
												errorMap.put(addend+"",error+err);
											}
											else {
												timeMap.put(addend+"", timeProblem);
												errorMap.put(addend+"",err);

											}
											nbPassages[addend-2-48]++;
										}
										catch(ProblemException e) {
											System.out.println(e + "\nProgram stopped.");
											return;
										}
									}
									int progress = (int) ((iterationCount * 100.0) / totalIterations);
									if (progress != lastPrintedProgress & progress%2==0) {
									    System.out.println("Progression : " + progress + "%");
									    lastPrintedProgress = progress;
									}

								}
								for(int ad=2;ad<6;ad++) {
									double e=(double)(errorMap.get(ad+"")+0.0)/nbPassages[ad-2];
									double ti=(double)(timeMap.get(ad+"")+0.0)/nbPassages[ad-2];
									writer.append(String.format(Locale.US,"%.2f,%.2f,%.2f,%2f,%d,%.5f,%.2f\n",
											decisionDeterminism, errorDiscount, wi,incrStrength, ad, e, ti));
									iterationCount++;
								}
								timeMap=new HashMap<String, Double>();
								errorMap=new HashMap<String,Integer>();
								nbPassages=new int[4];
								if (!oneSimulationDone) {
								    double timeAfter = System.currentTimeMillis();
								    double timeOneBlock = timeAfter - timeBefore;
								      double estimatedTotalTimeSec = (timeOneBlock / 1000.0) * totalParamComb;

								    System.out.println("Un bloc de 10 simulations a pris " + timeOneBlock / 1000.0 + " secondes.");
								    System.out.println("Estimation du temps total : " +
								        (int)(estimatedTotalTimeSec / 3600) + "h" +
								        (int)((estimatedTotalTimeSec % 3600) / 60) + "min" +
								        (int)(estimatedTotalTimeSec % 60) + "s");

								    long millis = System.currentTimeMillis();
								    LocalDateTime dateTime = Instant.ofEpochMilli(millis)
								            .atZone(ZoneId.systemDefault())
								            .toLocalDateTime();

								    System.out.println("Il est actuellement " + dateTime.getHour() + "h " +
								            dateTime.getMinute() + "min " + dateTime.getSecond() + "s");

								    oneSimulationDone = true;
								}

							}

						}
					}
				}
			}
			System.out.println("Itérations effectuées : " + iterationCount + " / " + totalIterations);

			System.out.println("Le fichier CSV a été écrit avec succès !");
			long millis = System.currentTimeMillis();

			LocalDateTime dateTime = Instant.ofEpochMilli(millis)
					.atZone(ZoneId.systemDefault()) // ou ZoneId.of("Europe/Paris")
					.toLocalDateTime();

			int heures = dateTime.getHour();
			int minutes = dateTime.getMinute();
			int secondes = dateTime.getSecond();

			System.out.println("Les simulations se sont finies à " + heures + "h " + minutes + "min " + secondes + "s");

		}catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	
	
}
