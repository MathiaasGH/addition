package Estimations;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

public class problem_maker_and_CSC_reader {

	public static Map<String, List<String>>[] split_jdd(String filePath, boolean isInstructed, paramEstimation instance) {
		String line;
		String delimiter = ",";
		boolean firstLine = true;
		String currentParticipant = null;
		String currentProfil = null;

		Map<String, List<String>> problems_by_participants_breakers = new HashMap<>();
		Map<String, List<String>> problems_by_participants_non_breakers = new HashMap<>();
		List<String> problemList_for_current_participant = new ArrayList<>();

		String[] augendList = new String[] {};
		String[] addendList = new String[] {};

		String problemName=null;
		
		if (!isInstructed) {
			augendList = augendList(instance);
			addendList = addendList(instance);
		}

		try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
			while ((line = br.readLine()) != null) {
			    String[] values = line.split(delimiter);

			    if (firstLine) {
			        firstLine = false;
			        continue;
			    }

			    // Générer le nom du problème
			    if (isInstructed) {
			        problemName = values[4].toLowerCase() + "+" + values[5] + "=" + values[6].toLowerCase();
			    } else {
			        String randomNumber = addendList[new Random().nextInt(addendList.length)];
			        String randomLetter = augendList[new Random().nextInt(augendList.length)];
			        problemName = randomLetter + "+" + randomNumber;
			    }

			    // Si on change de participant
			    if (values[1] != null && !values[1].equals(currentParticipant)) {
			        if (currentParticipant != null) {
			            if ("b".equals(currentProfil)) {
			                problems_by_participants_breakers.put(currentParticipant, problemList_for_current_participant);
			            } else {
			                problems_by_participants_non_breakers.put(currentParticipant, problemList_for_current_participant);
			            }
			        }

			        // Réinitialiser pour le nouveau participant
			        currentProfil = values[10];
			        currentParticipant = values[1];
			        problemList_for_current_participant = new ArrayList<>();
			    }

			    // Ajouter le problème à la liste (toujours !)
			    problemList_for_current_participant.add(problemName);
			}


			// Ajouter le dernier participant lu
			if (currentParticipant != null) {
				problemList_for_current_participant.add(problemName);
				if (currentProfil != null && currentProfil.equals("b")) {
					problems_by_participants_breakers.put(currentParticipant, problemList_for_current_participant);
				} else if (currentProfil != null) {
					problems_by_participants_non_breakers.put(currentParticipant, problemList_for_current_participant);
				}
			}

			return new Map[] { problems_by_participants_breakers, problems_by_participants_non_breakers };

		} catch (IOException e) {
			System.err.println(e.getMessage());
			return null;
		}
	}

	public static boolean doWeKnowAboutProfils(String filePath) {
		String line;
		String delimiter = ",";
		boolean firstLine = true;

		try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
			while ((line = br.readLine()) != null) {
				String[] values = line.split(delimiter);

				if (firstLine) {
					// Ignorer la première ligne
					firstLine = false;
					continue;
				}

				if (values[10] != null && (values[10].equals("b") || values[10].equals("nb"))) {
					return true;
				} else {
					return false;
				}
			}
			return false;

		} catch (IOException e) {
			System.err.println(e.getMessage());
			return false;
		}
	}

	public static String[] augendList(paramEstimation instance) {
	    String input = instance.augendList;
	    
	    if (input == null || input.trim().isEmpty()) {
	        throw new IllegalArgumentException("Illegal entry.");
	    }

	    String[] parts = input.split(",");
	    for (int i = 0; i < parts.length; i++) {
	        parts[i] = parts[i].trim();
	        if (parts[i].isEmpty()) {
	            throw new IllegalArgumentException("Illegal entry.");
	        }
	    }
	    return parts;
	}

	
	public static String[] addendList(paramEstimation instance) {
	    String input = instance.addendList;

	    if (input == null || input.trim().isEmpty()) {
	        throw new IllegalArgumentException("Illegal entry.");
	    }

	    String[] parts = input.split(",");
	    for (int i = 0; i < parts.length; i++) {
	        parts[i] = parts[i].trim();
	        if (parts[i].isEmpty()) {
	            throw new IllegalArgumentException("Illegal entry.");
	        }
	    }
	    return parts;
	}



	public static boolean isItInstructed(String filePath) { 
		String line;
		String delimiter = ",";
		boolean firstLine=true;
		try (BufferedReader br = new BufferedReader(new FileReader(filePath))){
			while((line = br.readLine()) != null) {
				String[] values = line.split(delimiter);
				if (firstLine) { 
					// Ignorer la première ligne 
					firstLine = false; 
					continue;
				}
				if(values[7]!=null && (values[7].equals("t") || values[7].equals("t+1")))
					return true;
				else return false;
			} 
			return false; 
		} catch (IOException e){
			System.err.println(e.getMessage());
			return false; 
		}
	}

	public static void main(String[] args) {
		// Map<String, List<String>> problemMap_breakers =
		//     CSV_reader.split_jdd("C:/Users/Propriétaire/OneDrive/Bureau/MIASHS/Stages/LPNC/r-studio/jugementEmpiriques.csv")[0];
		// List<String> participantName_breakers = new ArrayList<>(problemMap_breakers.keySet());
		// System.out.println(participantName_breakers);
		// System.out.println(CSV_reader.split_jdd("C:/Users/Propriétaire/OneDrive/Bureau/MIASHS/Stages/LPNC/r-studio/ANT_merged_raw.csv"));
	}

}
