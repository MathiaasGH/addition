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
import java.util.Map;

import Exceptions.ProblemException;
import Model.Model;
import Model.Problem;

public class est_Kyria_1 extends paramEstimation{

	public String nameNewFile;
	public String nameCSVfile;

	public void run() {
		augendList = "null";
		addendList = "null";


		boolean breakers;
		boolean isInstructed;

		breakers = problem_maker_and_CSC_reader.doWeKnowAboutProfils(nameCSVfile);
		isInstructed = problem_maker_and_CSC_reader.isItInstructed(nameCSVfile);
		if (!isInstructed)
			System.out.println("Your csv file " + nameCSVfile + " is not consistent in the order in which tasks are assigned.\nI will use these augends : " + augendList + "\n And theses addends : " + addendList);

		int nbSession = isInstructed ? 2 : 11;
		int nbParticipant = 20;

		// Paramètres

		double[] inP = new double[]{.2};
		double[] tTab = new double[]{3000,3200,2500,3750,4000};
		double[] pTab = new double[]{30,40,50,60,70};
		double[] bTab = new double[]{700,750,800,850,900};
		double[] dTab = new double[]{270,300,325,350,370};
		double[] rationalityTab = new double[]{.001,.0012,.0015,.0018,.002};
		double[] countingReinforcement = new double[]{.2, .4, .6,.8,1};
		double[] retrievingReinforcement = new double[]{.3, .5, .7,.9,1.1};

		int totalParamComb = inP.length * tTab.length * pTab.length * bTab.length * dTab.length * rationalityTab.length * countingReinforcement.length * retrievingReinforcement.length;
		int totalLines = totalParamComb * 4 * (nbSession - 1); // 4 addends * sessions
		int combDone = 0;
		int nextProgress = 2;
		
		
		// Lecture des données
		Map<String, List<String>>[] map;
		synchronized (this) {
			map = problem_maker_and_CSC_reader.split_jdd(nameCSVfile, isInstructed, this);
		}
		Map<String, List<String>> problemMap_breakers = map[0];
		Map<String, List<String>> problemMap_non_breakers = map[1];

		if (!breakers) {
			problemMap_breakers = problemMap_non_breakers;
		}

		List<String> participantName_breakers = new ArrayList<>(problemMap_breakers.keySet());
		List<String> participantName_non_breakers = new ArrayList<>(problemMap_non_breakers.keySet());

		if (!breakers) {
			participantName_breakers = participantName_breakers.subList(0, nbParticipant);
		} else {
			participantName_breakers = participantName_breakers.subList(0, nbParticipant / 2);
			participantName_non_breakers = participantName_non_breakers.subList(0, nbParticipant / 2);
		}

		String csvFile = nameNewFile + ".csv";
		File file = new File(csvFile);

		try {
			if (!file.exists()) {
				file.createNewFile();
				System.out.println("File created : " + file.getAbsolutePath());
			}

			try (FileWriter writer = new FileWriter(file)) {
				writer.append("increasePractice,t,p,b,d,rationality,countingReinforcement,retrievingReinforcement,Addend,accuracy,rt,session,profil\n");

				double timeBefore = System.currentTimeMillis();
				boolean oneSimulationDone = false;

				for (double incP : inP) {
					for (double t : tTab) {
						for (double p : pTab) {
							for (double b : bTab) {
								for (double d : dTab) {
									for (double rationality : rationalityTab) {
										for (double CR : countingReinforcement) {
											for (double RR : retrievingReinforcement) {

												// Structure pour stocker les résultats par session et addend
												// [session][addend][participant] = {time, error}
												double[][][] sessionTimes = new double[nbSession][4][nbParticipant];
												int[][][] sessionErrors = new int[nbSession][4][nbParticipant];
												int[][][] sessionCounts = new int[nbSession][4][nbParticipant];

												// Simulation pour les participants breakers
												for (int i = 0; i < nbParticipant/2; i++) {
													Model model = new Model("breaker");
													model.cleanAnswerMemory();
													model.cleanProcedureMemory();

													String participantID;
													List<String> problemList;
													String profil = "breaker";
													participantID = participantName_breakers.get(i);
													problemList = problemMap_breakers.get(participantID);


													model.setincreasePractice(incP);
													model.setTimeParam(t, p, b, d);
													model.setRationality(rationality);
													model.setReinforcementParam(CR, RR);

													// Simulation pour chaque session
													for (int session = 1; session < nbSession; session++) {
														Map<String, Double> timeMap = new HashMap<>();
														Map<String, Integer> errorMap = new HashMap<>();
														int[] nbPassages = new int[4];

														for (int a = 0; a < 288; a++) {
															try {
																Problem problem = new Problem(problemList.get(a), model);
																double timeProblem = model.addProblem(problem);
																char addend = problem.getAddend();
																int err = (problem.error() == true ? 0 : 1);

																String addendStr = String.valueOf(addend);
																if (nbPassages[addend - 2 - 48] != 0) {
																	timeMap.put(addendStr, timeMap.get(addendStr) + timeProblem);
																	errorMap.put(addendStr, errorMap.get(addendStr) + err);
																} else {
																	timeMap.put(addendStr, timeProblem);
																	errorMap.put(addendStr, err);
																}
																nbPassages[addend - 2 - 48]++;
															} catch (ProblemException e) {
																System.out.println(e + "\nProgram stopped.");
																return;
															}
														}

														// Stockage des résultats moyens par addend pour ce participant/session
														for (int ad = 2; ad < 6; ad++) {
															if (nbPassages[ad - 2] > 0) {
																sessionTimes[session][ad - 2][i] = timeMap.get(String.valueOf(ad)) / nbPassages[ad - 2];
																sessionErrors[session][ad - 2][i] = errorMap.get(String.valueOf(ad));
																sessionCounts[session][ad - 2][i] = nbPassages[ad - 2];
															}
														}
													}
												}


												if(breakers) {
													// Calcul des moyennes et écriture dans le fichier
													for (int session = 1; session < nbSession; session++) {
														for (int ad = 2; ad < 6; ad++) {
															double avgTime = 0;
															double avgError = 0;
															int totalCount = 0;

															for (int i = 0; i < nbParticipant/2; i++) {
																avgTime += sessionTimes[session][ad - 2][i];
																avgError += (double) sessionErrors[session][ad - 2][i] / sessionCounts[session][ad - 2][i];
																totalCount++;
															}

															avgTime /= totalCount;
															avgError /= totalCount;

															writer.append(incP + "," + t + "," + p + "," + b + "," + d + "," + rationality + "," + CR + "," + RR + "," + ad + "," + avgError + "," + avgTime + "," + session + "," + "b" + "\n");

														}
													}


													sessionTimes = new double[nbSession][4][nbParticipant];
													sessionErrors = new int[nbSession][4][nbParticipant];
													sessionCounts = new int[nbSession][4][nbParticipant];
												}



												//Simulations pour les participants nonbreakers
												
												for (int i = 0; i < nbParticipant/2; i++) {
													Model model = new Model("nonbreaker");
													model.cleanAnswerMemory();
													model.cleanProcedureMemory();

													String participantID;
													String profil = "nonbreaker";
													if(!breakers)
														participantID = participantName_breakers.get(i+nbParticipant/2); 
													else participantID = participantName_non_breakers.get(i); 
													List<String> problemList= problemMap_non_breakers.get(participantID);


													model.setincreasePractice(incP);
													model.setTimeParam(t, p, b, d);
													model.setRationality(rationality);
													model.setReinforcementParam(CR, RR);

													// Simulation pour chaque session
													for (int session = 1; session < nbSession; session++) {
														Map<String, Double> timeMap = new HashMap<>();
														Map<String, Integer> errorMap = new HashMap<>();
														int[] nbPassages = new int[4];

														for (int a = 0; a < 288; a++) {
															try {
																Problem problem = new Problem(problemList.get(a), model);
																double timeProblem = model.addProblem(problem);
																char addend = problem.getAddend();
																int err = (problem.error() == true ? 0 : 1);

																String addendStr = String.valueOf(addend);
																if (nbPassages[addend - 2 - 48] != 0) {
																	timeMap.put(addendStr, timeMap.get(addendStr) + timeProblem);
																	errorMap.put(addendStr, errorMap.get(addendStr) + err);
																} else {
																	timeMap.put(addendStr, timeProblem);
																	errorMap.put(addendStr, err);
																}
																nbPassages[addend - 2 - 48]++;
															} catch (ProblemException e) {
																System.out.println(e + "\nProgram stopped.");
																return;
															}
														}

														// Stockage des résultats moyens par addend pour ce participant/session
														int participant = i;
														if(!breakers)
															participant = participant + nbParticipant/2;
														for (int ad = 2; ad < 6; ad++) {
															if (nbPassages[ad - 2] > 0) {
																sessionTimes[session][ad - 2][participant] = timeMap.get(String.valueOf(ad)) / nbPassages[ad - 2];
																sessionErrors[session][ad - 2][participant] = errorMap.get(String.valueOf(ad));
																sessionCounts[session][ad - 2][participant] = nbPassages[ad - 2];
															}
														}
													}
												}

												if(breakers) {
													// Calcul des moyennes et écriture dans le fichier
													for (int session = 1; session < nbSession; session++) {
														for (int ad = 2; ad < 6; ad++) {
															double avgTime = 0;
															double avgError = 0;
															int totalCount = 0;

															for (int i = 0; i < nbParticipant/2; i++) {
																avgTime += sessionTimes[session][ad - 2][i];
																avgError += (double) sessionErrors[session][ad - 2][i] / sessionCounts[session][ad - 2][i];
																totalCount++;
															}

															avgTime /= totalCount;
															avgError /= totalCount;

															writer.append(incP + "," + t + "," + p + "," + b + "," + d + "," + rationality + "," + CR + "," + RR + "," + ad + "," + avgError + "," + avgTime + "," + session + "," + "nb" + "\n");

														}
													}


													sessionTimes = new double[nbSession][4][nbParticipant];
													sessionErrors = new int[nbSession][4][nbParticipant];
													sessionCounts = new int[nbSession][4][nbParticipant];
												}
												else {
													// Calcul des moyennes et écriture dans le fichier
													for (int session = 1; session < nbSession; session++) {
														for (int ad = 2; ad < 6; ad++) {
															double avgTime = 0;
															double avgError = 0;
															int totalCount = 0;

															for (int i = 0; i < nbParticipant; i++) {
																avgTime += sessionTimes[session][ad - 2][i];
																avgError += (double) sessionErrors[session][ad - 2][i] / sessionCounts[session][ad - 2][i];
																totalCount++;
															}

															avgTime /= totalCount;
															avgError /= totalCount;

															writer.append(incP + "," + t + "," + p + "," + b + "," + d + "," + rationality + "," + CR + "," + RR + "," + ad + "," + avgError + "," + avgTime + "," + session + "," + "NA" + "\n");

														}
													}
												}
												if (!oneSimulationDone) {
													double timeAfter = System.currentTimeMillis();
													double timeOneBlock = timeAfter - timeBefore;
													double estimatedTotalTimeSec = (timeOneBlock / 1000.0) * totalParamComb;

													System.out.println("First parameter combination took " + timeOneBlock / 1000.0 + " seconds.");
													System.out.println("Total time estimation : " +
															(int) (estimatedTotalTimeSec / 3600) + "h" +
															(int) ((estimatedTotalTimeSec % 3600) / 60) + "min" +
															(int) (estimatedTotalTimeSec % 60) + "s");

													LocalDateTime dateTime = Instant.ofEpochMilli(System.currentTimeMillis())
															.atZone(ZoneId.systemDefault())
															.toLocalDateTime();

													System.out.println("Current time : " + dateTime.getHour() + "h " +
															dateTime.getMinute() + "min " + dateTime.getSecond() + "s");
													oneSimulationDone = true;
												}
												
												combDone++;
												int progress = (int) ((combDone * 100.0) / totalParamComb);
												if (progress >= nextProgress) {
												    System.out.println("Progress : " + progress + "% (" + combDone + "/" + totalParamComb + " combinations)");
												    nextProgress += 2;
												}

											}
										}
									}
								}
							}
						}
					}
				}
			}

			System.out.println("The CSV file was written successfully !");
			LocalDateTime dateTime = Instant.ofEpochMilli(System.currentTimeMillis())
					.atZone(ZoneId.systemDefault())
					.toLocalDateTime();

			System.out.println("Ended at " + dateTime.getHour() + "h " + dateTime.getMinute() + "min " + dateTime.getSecond() + "s");

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		est_Kyria_1 instance = new est_Kyria_1();
		instance.nameCSVfile="jugementEmpiriques.csv";
		instance.nameNewFile="estimationKyria_1";
		instance.run();

	}
}