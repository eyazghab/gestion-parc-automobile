package com.example.demo.Repository;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.model.BonCarburant;
import com.example.demo.model.EtatMission;
import com.example.demo.model.OrdreMission;
import com.example.demo.model.Vehicule;

public interface OrdreMissionRepository extends JpaRepository<OrdreMission, Long> {
	List<OrdreMission> findByUtilisateurIdUtilisateur(Long idUtilisateur);
    List<OrdreMission> findByEtatIn(List<EtatMission> etats);
    List<OrdreMission> findByVehiculeIdVehiculeAndEtatIn(Long vehiculeId, List<EtatMission> etats);
    List<OrdreMission> findByUtilisateurIdUtilisateurAndEtatIn(Long utilisateurId, List<EtatMission> etats);
    boolean existsByVehiculeAndDateDepartLessThanEqualAndDateRetourGreaterThanEqual(
    	    Vehicule vehicule, LocalDate dateRetour, LocalDate dateDepart);
    List<OrdreMission> findByDateDepartLessThanEqualAndDateRetourGreaterThanEqual(LocalDateTime fin, LocalDateTime debut);
    List<OrdreMission> findByVehiculeAndEtat(Vehicule vehicule, EtatMission etat);
    List<OrdreMission> findByVehicule(Vehicule vehicule);
    List<OrdreMission> findByEtat(EtatMission etat);
    @Query("SELECT o FROM OrdreMission o " +
    	       "WHERE o.dateDepart < :dateFin " +
    	       "AND o.dateRetour > :dateDebut")
    	List<OrdreMission> findMissionsChevauchantes(
    	        @Param("dateDebut") LocalDateTime dateDebut,
    	        @Param("dateFin") LocalDateTime dateFin);

    List<OrdreMission> findByVehiculeIdVehicule(Long idVehicule);

    // Récupérer les missions entre deux dates pour un véhicule
    List<OrdreMission> findByVehicule_IdVehiculeAndDateDepartBetweenOrderByDateDepartAsc(
        Long vehiculeId,
        LocalDateTime debut,
        LocalDateTime fin
    );
    @Query("SELECT b.mission FROM BonCarburant b WHERE b.id = :bonId")
    OrdreMission findMissionByBonId(Long bonId);

}




