package com.example.demo.service.imp;


import com.example.demo.Dto.MissionCarburantDTO;
import com.example.demo.Dto.UtilisateurAvecBonsDTO;
import com.example.demo.Repository.BonCarburantRepository;
import com.example.demo.Repository.CarburantRepository;
import com.example.demo.Repository.OrdreMissionRepository;
import com.example.demo.model.BonCarburant;
import com.example.demo.model.Carburant;
import com.example.demo.model.EtatMission;
import com.example.demo.model.OrdreMission;
import com.example.demo.model.Vehicule;
import com.example.demo.service.CarburantService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CarburantServiceImpl implements CarburantService {

    private final CarburantRepository carburantRepository;
    private final OrdreMissionRepository missionRepo;
    private final BonCarburantRepository bonCarburantRepo;

    public CarburantServiceImpl(CarburantRepository carburantRepository,
    		OrdreMissionRepository missionRepo,BonCarburantRepository bonCarburantRepo) {
        this.carburantRepository = carburantRepository;
        this.missionRepo=missionRepo;
        this.bonCarburantRepo=bonCarburantRepo;
    }

    @Override
    public List<Carburant> getAllCarburants() {
        return carburantRepository.findAll();
    }

    @Override
    public Carburant addCarburant(Carburant carburant) {
        return carburantRepository.save(carburant);
    }

    @Override
    public Carburant getCarburantById(Long id) {
        return carburantRepository.findById(id).orElse(null);
    }

    @Override
    public void deleteCarburant(Long id) {
        carburantRepository.deleteById(id);
    }
 // ---------------- Calcul du carburant nécessaire ----------------
    @Override
    public double calculCarburantNecessaire(Vehicule vehicule) {
        // Récupérer toutes les missions validées pour ce véhicule
        List<OrdreMission> missions = missionRepo.findByVehiculeAndEtat(vehicule, EtatMission.ACCEPTEE);

        // Somme des distances estimées
        double distanceTotale = missions.stream()
                                        .mapToDouble(m -> m.getDistanceEstimee() != null ? m.getDistanceEstimee() : 0)
                                        .sum();

        // Consommation moyenne (L/100 km)
        double consommationMoyenne = vehicule.getConsommationMoyenne() != null ?
                                     vehicule.getConsommationMoyenne() : 8.0;

        // Calcul carburant nécessaire
        return (distanceTotale * consommationMoyenne) / 100.0;
    }
public List<MissionCarburantDTO> getMissionsAvecCarburant(Vehicule vehicule) {
    List<OrdreMission> missions = missionRepo.findByVehiculeAndEtat(vehicule, EtatMission.ACCEPTEE);

    double consommationMoyenne = vehicule.getConsommationMoyenne() != null ?
                                 vehicule.getConsommationMoyenne() : 8.0;

    return missions.stream()
            .map(m -> {
                Double distance = m.getDistanceEstimee() != null ? m.getDistanceEstimee() : 0.0;
                double carburant = distance * consommationMoyenne / 100.0;

                String nomUtilisateur = m.getUtilisateur() != null ? m.getUtilisateur().getNom() : "Inconnu";

                return new MissionCarburantDTO(
                        m.getId(),
                        m.getDestination(),
                        nomUtilisateur,
                        m.getDateDepart() != null ? m.getDateDepart().toString() : null,
                        m.getDateRetour() != null ? m.getDateRetour().toString() : null,
                        distance,
                        Double.valueOf(carburant) // 🔹 conversion vers Double
                );
            })
            .toList();
}

public List<UtilisateurAvecBonsDTO> getBonsParUtilisateur() {
    List<BonCarburant> bons = bonCarburantRepo.findAll(); // ou filtrer par état si besoin

    Map<Long, UtilisateurAvecBonsDTO> map = new HashMap<>();

    for (BonCarburant bon : bons) {
        if (bon.getUtilisateur() == null) continue;

        Long userId = bon.getUtilisateur().getIdUtilisateur();
        String nomUser = bon.getUtilisateur().getNom();

        // On transforme le bon en DTO MissionCarburantDTO pour ce véhicule
        List<MissionCarburantDTO> missions = bon.getVehicule() != null
                ? getMissionsAvecCarburant(bon.getVehicule())
                : Collections.emptyList();

        // Ajouter ce bon à l'utilisateur correspondant
        if (!map.containsKey(userId)) {
            map.put(userId, new UtilisateurAvecBonsDTO(userId, nomUser, new ArrayList<>()));
        }

        map.get(userId).getBons().addAll(missions);
    }

    return new ArrayList<>(map.values());
}
}

