	package Estimations;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Exceptions.ProblemException;
import Model.Model;
import Model.Problem;

public class jugement {
	
	public static double mean(List<Double> list) {
		double total=0;
		for(int i=0;i<list.size();i++) {
			total=total+list.get(i);
		}
		return total/list.size();
	}
	
	 public static void main(String[] args) {
	        List<String> augends = Arrays.asList("c", "f", "i");
	        List<Integer> addends = Arrays.asList(2, 3, 4, 5);
	        Map<String, Map<Integer, List<Double>>> map_time = new HashMap<>();
	        Map<String, Map<Integer, List<Double>>> map_erreur= new HashMap<>();

	        Map<Integer, List<Double>> tempMapB = new HashMap<>();
	        for (int i = 2; i <= 5; i++) {
	            tempMapB.put(i, new ArrayList<>());
	        }
	        Map<Integer, List<Double>> tempMapB_erreur = new HashMap<>();
	        for (int i = 2; i <= 5; i++) {
	            tempMapB_erreur.put(i, new ArrayList<>());
	        }
	        map_erreur.put("b", tempMapB_erreur);
	        map_time.put("b", tempMapB);

	        Map<Integer, List<Double>> tempMapNB = new HashMap<>();
	        for (int i = 2; i <= 5; i++) {
	            tempMapNB.put(i, new ArrayList<>());
	        }
	        Map<Integer, List<Double>> tempMapNB_erreur = new HashMap<>();
	        for (int i = 2; i <= 5; i++) {
	            tempMapNB_erreur.put(i, new ArrayList<>());
	        }
	        map_erreur.put("nb", tempMapNB_erreur);
	        map_time.put("nb", tempMapNB);
//	        System.out.println(map_time);
//	        System.out.println(map_erreur);
	        List<String> results = new ArrayList<>();

	        for (String a : augends) {
	            int aValue = a.charAt(0) - 'a' + 1; // Convertit la lettre en valeur numérique
	            for (int b : addends) {
	                int vraiValue = aValue + b;
	                int fauxValue = vraiValue + 1;

	                // Convertir la valeur en lettre (boucler si >26)
	                String vraiLetter = Character.toString((char)('a' + (vraiValue - 1) % 26));
	                String fauxLetter = Character.toString((char)('a' + (fauxValue - 1) % 26));

	                results.add(a + "+" + b + "=" + vraiLetter);
	                results.add(a + "+" + b + "=" + fauxLetter);
	            }
	        }

//	        // Affichage
//	        for (String s : results) {
//	            System.out.println(s);
//	        }
	        
	        double increase_practice=.55;
	        double t =3000;
	        double p=30;
	        double b=700;
	        double d=325;
	        double rationality=.005;
	        double counting_reinforcement=.37;
	        double retrieving_reinforcement=.5;
        
	        Model model = new Model();
	        model.setincreasePractice(increase_practice);
	        model.setReinforcementParam(counting_reinforcement, retrieving_reinforcement);
	        model.setRationality(rationality);
	        model.setTimeParam(t, p, b, d);
	        
	        for(int run=0;run<500;run++) {
	        	model.cleanAnswerMemory();
				model.cleanProcedureMemory();
				model=new Model("breaker");
		        model.setincreasePractice(increase_practice);
		        model.setReinforcementParam(counting_reinforcement, retrieving_reinforcement);
		        model.setRationality(rationality);
		        model.setTimeParam(t, p, b, d);
				Collections.shuffle(results);
				int pbintrabloc=0;
				for (int pb = 0; pb < results.size()*4; pb++) {
					if(pbintrabloc==results.size()) {
						pbintrabloc=0;
						Collections.shuffle(results);
					}
					try {
						Problem problem = new Problem(results.get(pbintrabloc), model);
						double timeProblem = model.addProblem(problem);
						map_time.get("b").get(Integer.valueOf(problem.getAddend())-48).add(timeProblem);
						map_erreur.get("b").get(Integer.valueOf(problem.getAddend())-48).add(problem.error()==true?1:0+0.0);
						pbintrabloc++;
					} catch (ProblemException e) {
						e.printStackTrace();
					}
				}
	        }
	        
	        for(int run=0;run<500;run++) {
	        	model.cleanAnswerMemory();
				model.cleanProcedureMemory();
				model=new Model("nonbreaker");

		        model.setincreasePractice(increase_practice);
		        model.setReinforcementParam(counting_reinforcement, retrieving_reinforcement);
		        model.setRationality(rationality);
		        model.setTimeParam(t, p, b, d);
				Collections.shuffle(results);
				int pbintrabloc=0;
				for (int pb = 0; pb < results.size()*4; pb++) {
					if(pbintrabloc==results.size()) {
						pbintrabloc=0;
						Collections.shuffle(results);
					}
					try {
						Problem problem = new Problem(results.get(pbintrabloc), model);
						double timeProblem = model.addProblem(problem);
						map_time.get("nb").get(Integer.valueOf(problem.getAddend())-48).add(timeProblem);
						map_erreur.get("nb").get(Integer.valueOf(problem.getAddend())-48).add(problem.error()==true?1:0+0.0);
						pbintrabloc++;
					} catch (ProblemException e) {
						e.printStackTrace();
					}
				}
	        }
	        
	        for(int i=2;i<6;i++) {
	        	List<Double> temp_map = map_time.get("b").get(i);
	        	map_time.get("b").put(i, Arrays.asList(mean(temp_map)));
	        	List<Double> temp_map_2 = map_erreur.get("b").get(i);
	        	map_erreur.get("b").put(i, Arrays.asList(mean(temp_map_2)));
	        	List<Double> temp_map_3 = map_time.get("nb").get(i);
	        	map_time.get("nb").put(i, Arrays.asList(mean(temp_map_3)));
	        	List<Double> temp_map_4 = map_erreur.get("nb").get(i);
	        	map_erreur.get("nb").put(i, Arrays.asList(mean(temp_map_4)));
	        }
//	        

	        System.out.println(map_time);
	        System.out.println(map_erreur);
	        
	        String csvFile = "kyria" + ".csv";
			File file = new File(csvFile);
			
	        try {
				// Je crée le fichier
				if (!file.exists()) {
					file.createNewFile();
				}

				//J'écris les données récoltées dans le fichier
				try (FileWriter writer = new FileWriter(file)) {
					writer.append("Addend, accuracy, rt, profil\n");
					for(int add=2;add<6;add++)
					writer.append(add+"," + map_erreur.get("b").get(add).get(0) +","+ map_time.get("b").get(add).get(0) + ",b\n");
					for(int add=2;add<6;add++)
						writer.append(add+"," + map_erreur.get("nb").get(add).get(0) +","+ map_time.get("nb").get(add).get(0) + ",nb\n");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
	        
	        
	    }	
}
