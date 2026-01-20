package com.example.demo.service.imp;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.Repository.BonCarburantRepository;
import com.example.demo.Repository.OrdreMissionRepository;
import com.example.demo.Repository.VehiculeRepository;
import com.example.demo.model.BonCarburant;
import com.example.demo.model.EtatMission;
import com.example.demo.model.EtatVehicule;
import com.example.demo.model.OrdreMission;
import com.example.demo.model.Vehicule;
import com.example.demo.service.VehiculeService;

import jakarta.transaction.Transactional;

@Service
public class VehiculeServiceImpl implements VehiculeService {

    @Autowired
    private VehiculeRepository vehiculeRepository;

    @Autowired
    private OrdreMissionRepository ordreMissionRepository;

    
    @Autowired
    private BonCarburantRepository bonCarburantRepository;
    @Override
    public Vehicule createVehicule(Vehicule vehicule) {
        return vehiculeRepository.save(vehicule);
    }

    @Override
    public Vehicule getVehiculeById(Long id) {
        return vehiculeRepository.findById(id).orElse(null);
    }

    @Override
    public List<Vehicule> getAllVehicules() {
        return vehiculeRepository.findAll();
    }

    @Override
    public Vehicule updateVehicule(Long id, Vehicule vehicule) {
        return vehiculeRepository.findById(id)
                .map(existing -> {
                    vehicule.setIdVehicule(id);
                    return vehiculeRepository.save(vehicule);
                })
                .orElse(null);
    }

    @Override
    public void deleteVehicule(Long id) {
        vehiculeRepository.deleteById(id);
    }

    @Override
    public Vehicule findByIdWithAllRelations(Long id) {
        return vehiculeRepository.findByIdWithRelations(id)
                .orElseThrow(() -> new RuntimeException("Véhicule non trouvé avec l'ID : " + id));
    }

    
@Override
public List<Vehicule> getVehiculesDisponibles(LocalDateTime dateDebut, LocalDateTime dateFin) {
    List<Vehicule> tous = vehiculeRepository.findAll();

    // Récupérer toutes les missions qui pourraient bloquer un véhicule
    List<OrdreMission> missionsBloquantes = ordreMissionRepository.findAll().stream()
        .filter(m -> {
            // On bloque uniquement les missions ACCEPTÉE ou EN_COURS
            EtatMission etat = m.getEtat();
            if (etat != EtatMission.ACCEPTEE && etat != EtatMission.EN_COURS) return false;

            // Dates prévues
            LocalDateTime dPrev = m.getDateDepart();
            LocalDateTime fPrev = m.getDateRetour();

            // Dates réelles si elles existent
            LocalDateTime dReelle = m.getDateDepartReelle() != null ? m.getDateDepartReelle() : dPrev;
            LocalDateTime fReelle = m.getDateRetourReelle() != null ? m.getDateRetourReelle() : fPrev;

            // Chevauchement : dateDebut <= fReelle && dateFin >= dReelle
            return !(dateFin.isBefore(dReelle) || dateDebut.isAfter(fReelle));
        })
        .collect(Collectors.toList());

    // Extraire les véhicules occupés
    Set<Long> vehiculesOccupes = missionsBloquantes.stream()
        .map(m -> m.getVehicule().getIdVehicule())
        .collect(Collectors.toSet());

    // Retourner les véhicules libres
    return tous.stream()
        .filter(v -> !vehiculesOccupes.contains(v.getIdVehicule()))
        .collect(Collectors.toList());
}




    @Override
    public List<Vehicule> getVehiculesLibres() {
        List<Vehicule> allVehicules = vehiculeRepository.findAll();
        return allVehicules.stream()
                .filter(this::isVehiculeLibre)
                .collect(Collectors.toList());
    }

   private boolean isVehiculeLibre(Vehicule vehicule) {
    List<OrdreMission> missions = ordreMissionRepository.findByVehicule(vehicule);
    LocalDateTime now = LocalDateTime.now();

    List<EtatMission> etatsOccupes = List.of(
        EtatMission.ACCEPTEE,
        EtatMission.EN_COURS
    );

    return missions.stream().noneMatch(m ->
        etatsOccupes.contains(m.getEtat()) &&
        (
            // chevauchement avec dates réelles
            (m.getDateDepartReelle() != null && m.getDateRetourReelle() != null &&
             !now.isBefore(m.getDateDepartReelle()) && !now.isAfter(m.getDateRetourReelle()))
            ||
            // chevauchement avec dates prévues (si réelles pas encore renseignées)
            (m.getDateDepart() != null && m.getDateRetour() != null &&
             !now.isBefore(m.getDateDepart()) && !now.isAfter(m.getDateRetour()))
        )
    );
}

    @Override
    public List<Vehicule> getVehiculesDisponiblesEtReserves() {
        return vehiculeRepository.findByEtatIn(
                Arrays.asList(EtatVehicule.DISPONIBLE, EtatVehicule.RESERVE)
        );
    }
    

        // 🔹 Calcul de la consommation moyenne du véhicule
        
       @Override
@Transactional
public Double calculerEtEnregistrerConsommationMoyenne(Long idVehicule) {
    Vehicule vehicule = vehiculeRepository.findById(idVehicule)
            .orElseThrow(() -> new RuntimeException("Véhicule non trouvé avec l’ID : " + idVehicule));

    // 🔹 Récupérer tous les bons acceptés pour ce véhicule
    List<BonCarburant> bons = bonCarburantRepository.findByVehiculeIdVehiculeAndEtat(
            idVehicule, BonCarburant.EtatBon.ACCEPTE);

    if (bons.isEmpty()) {
        vehicule.setConsommationMoyenne(null);
        vehiculeRepository.save(vehicule);
        return null;
    }

    // 🔹 Somme des litres consommés
    double totalLitres = bons.stream()
            .mapToDouble(BonCarburant::getQuantite)
            .sum();

    // 🔹 Somme des distances parcourues via les missions associées
    double totalDistance = bons.stream()
            .filter(b -> b.getMission() != null)
            .mapToDouble(b -> {
                Double d = b.getMission().getDistanceEstimee();
                return d != null ? d : 0.0;
            })
            .sum();

    Double consoMoyenne = null;
    if (totalDistance > 0) {
        consoMoyenne = (totalLitres / totalDistance) * 100; // L/100km
        vehicule.setConsommationMoyenne(consoMoyenne);
    } else {
        vehicule.setConsommationMoyenne(null);
    }

    vehiculeRepository.save(vehicule);
    return consoMoyenne;
}

    }



