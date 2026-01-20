package com.example.demo.Repository;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.BonCarburant;
import com.example.demo.model.BonCarburant.EtatBon;

@Repository
public interface BonCarburantRepository extends JpaRepository<BonCarburant, Long> {
    List<BonCarburant> findByVehiculeIdVehicule(Long idVehicule);
    List<BonCarburant> findByUtilisateurIdUtilisateur(Long utilisateurId);
    List<BonCarburant> findByEtat(EtatBon etat);
 // Récupérer le prochain bon pour le même véhicule après une date donnée
    Optional<BonCarburant> findFirstByVehicule_IdVehiculeAndDateAchatAfterOrderByDateAchatAsc(
    	    Long vehiculeId,
    	    java.util.Date dateAchat
    	);
        List<BonCarburant> findByMission_Id(Long missionId);
    
        List<BonCarburant> findByVehiculeIdVehiculeAndEtat(Long vehiculeId, BonCarburant.EtatBon etat);

}
