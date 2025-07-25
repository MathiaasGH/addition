package Estimations;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import Model.Model;
import Model.Problem;

public class EstimateSwitchingCost {

    public static void main(String[] args) throws IOException {
        String inputFile = "observed_times.csv";
        String outputFile = "estimation_results.csv";

        // Format : profile -> addend -> observed mean time
        Map<String, Map<Integer, Double>> observedTimes = new HashMap<>();

        // Lecture du fichier CSV d'observations
        try (BufferedReader br = new BufferedReader(new FileReader(inputFile))) {
            String line = br.readLine(); // Skip header
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                String profile = parts[0];
                int addend = Integer.parseInt(parts[1]);
                double meanTime = Double.parseDouble(parts[2]);

                observedTimes.putIfAbsent(profile, new HashMap<>());
                observedTimes.get(profile).put(addend, meanTime);
            }
        }

        try (FileWriter writer = new FileWriter(outputFile)) {
            writer.append("profile,switching_cost,rmse\n");

            for (String profile : observedTimes.keySet()) {
                Map<Integer, Double> obsTimes = observedTimes.get(profile);

                for (double switchingCost = 0.0; switchingCost <= 5000.0; switchingCost += 10.0) {
                    double rmse = simulateAndEvaluate(profile, switchingCost, obsTimes);
                    writer.append(String.format(Locale.US, "%s,%.2f,%.4f\n", profile, switchingCost, rmse));
                }
            }

            System.out.println("Estimation terminée. Résultats enregistrés dans " + outputFile);
        }
    }

    public static double simulateAndEvaluate(String profile, double switchingCost, Map<Integer, Double> observedTimes) {
        Model model = new Model(profile); // Initialise selon ton constructeur
        model.setSwitchingCost(switchingCost); // À adapter à ton implémentation

        Map<Integer, List<Double>> simulatedTimes = new HashMap<>();
        for (int addend = 2; addend <= 5; addend++) {
            simulatedTimes.put(addend, new ArrayList<>());
        }

        for (int i = 0; i < 50; i++) { // nombre de répétitions
            model.resetMemory(); // Réinitialiser mémoire modèle si nécessaire
            for (int a = 0; a < 50; a++) {
                int randLetter = (int) ((Math.random() * (107 - 101)) + 101); // e à j
                for (int addend = 2; addend <= 5; addend++) {
                    try {
                        Problem p = new Problem((char) randLetter + "+" + addend, model);
                        double time = model.addProblem(p);
                        simulatedTimes.get(addend).add(time);
                    } catch (Exception e) {
                        System.out.println("Erreur : " + e.getMessage());
                    }
                }
            }
        }

        // Moyennes simulées
        double sumSquaredError = 0;
        for (int addend : observedTimes.keySet()) {
            List<Double> times = simulatedTimes.get(addend);
            double meanSim = times.stream().mapToDouble(Double::doubleValue).average().orElse(0);
            double meanObs = observedTimes.get(addend);
            sumSquaredError += Math.pow(meanSim - meanObs, 2);
        }

        return Math.sqrt(sumSquaredError / observedTimes.size());
    }
}
