package com.example.demo.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.Suivi;
import com.example.demo.model.Vehicule;

public interface SuiviRepository extends JpaRepository<Suivi, Long> {
	  // Retourne tous les suivis d’un véhicule
    List<Suivi> findByVehicule_IdVehicule(Long idVehicule);
    List<Suivi> findByVehicule(Vehicule vehicule);
    // Optionnel : récupérer directement le suivi le plus récent
    // Tous les suivis d’un véhicule, triés du plus récent au plus ancien
    List<Suivi> findByVehiculeIdVehiculeOrderByDateDerniersuiviDesc(Long idVehicule);

    // Récupérer uniquement le dernier suivi
    Optional<Suivi> findTopByVehiculeIdVehiculeOrderByDateDerniersuiviDesc(Long idVehicule);
    
    // 🔹 Récupère tous les suivis d’un véhicule triés par date décroissante
    List<Suivi> findByVehiculeOrderByDateDerniersuiviDesc(Vehicule vehicule);
}
