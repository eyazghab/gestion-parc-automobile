package com.example.demo.service;

import com.example.demo.model.OrdreMission;
import com.example.demo.model.Vehicule;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AffectationIAService {

    /**
     * Optimiser l’affectation des véhicules aux ordres de mission
     * en minimisant la consommation estimée.
     */
    public Map<Long, Long> optimiserAffectations(List<Vehicule> vehicules, List<OrdreMission> missions) {
        if (vehicules.isEmpty() || missions.isEmpty()) {
            throw new RuntimeException("❌ Pas assez de données pour l’optimisation");
        }

        int n = Math.min(vehicules.size(), missions.size());
        double[][] couts = new double[n][n];

        for (int i = 0; i < n; i++) {
            Vehicule v = vehicules.get(i);

            for (int j = 0; j < n; j++) {
                OrdreMission mission = missions.get(j);

                // Calcul consommation estimée
                double distance = mission.getDistanceEstimee() != null ? mission.getDistanceEstimee() : 0.0;
                double conso = v.getConsommationMoyenne() != null ? v.getConsommationMoyenne() : 8.0; // défaut 8L/100km
                double cout = distance * (conso / 100.0);

                couts[i][j] = cout;
            }
        }

        int[] affectations = affecterParCoutMinimal(couts);

        // Associer {vehiculeId -> missionId}
        Map<Long, Long> resultat = new HashMap<>();
        for (int i = 0; i < n; i++) {
            int j = affectations[i];
            resultat.put(vehicules.get(i).getIdVehicule(), missions.get(j).getId());
        }

        return resultat;
    }

    /**
     * Algo d’affectation simple : chaque véhicule prend la mission avec le coût minimal.
     */
    private int[] affecterParCoutMinimal(double[][] costMatrix) {
        int n = costMatrix.length;
        int[] result = new int[n];
        Set<Integer> missionsPrises = new HashSet<>();

        for (int i = 0; i < n; i++) {
            double minCost = Double.MAX_VALUE;
            int minIndex = -1;

            for (int j = 0; j < n; j++) {
                if (!missionsPrises.contains(j) && costMatrix[i][j] < minCost) {
                    minCost = costMatrix[i][j];
                    minIndex = j;
                }
            }

            result[i] = minIndex;
            missionsPrises.add(minIndex);
        }

        return result;
    }
}
