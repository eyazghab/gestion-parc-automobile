package com.example.demo.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import com.example.demo.model.EtatVehicule;
import com.example.demo.model.Maintenance;
import com.example.demo.model.OrdreMission;
import com.example.demo.model.Vehicule;

public interface VehiculeRepository extends JpaRepository<Vehicule, Long> {

    @EntityGraph(attributePaths = {
        "assurances",
        "maintenances",
        "parking",
        "utilisateur",
        "documents",
        "bonsCarburant",
        "sinistres",
        "modele",
        "typeCarrosserie",
        "suivis"
    })
    @Query("SELECT v FROM Vehicule v " +
            "LEFT JOIN FETCH v.assurances " +
            "LEFT JOIN FETCH v.maintenances " +
            "LEFT JOIN FETCH v.suivis " +
            "WHERE v.idVehicule = :id")
     Optional<Vehicule> findByIdWithRelations(@Param("id") Long id);

     @Query("SELECT DISTINCT v FROM Vehicule v " +
            "LEFT JOIN FETCH v.assurances " +
            "LEFT JOIN FETCH v.maintenances " +
            "LEFT JOIN FETCH v.suivis")
     List<Vehicule> findAllWithRelations();
    List<Vehicule> findByIdVehiculeNotIn(List<Long> ids);
    List<Vehicule> findByEtat(EtatVehicule etat);
    List<Vehicule> findByIdVehiculeNotInAndEtat(Set<Long> ids, EtatVehicule etat);
    @Query("SELECT DISTINCT v FROM Vehicule v " +
    	       "JOIN v.missions m " +
    	       "WHERE v.etat = 'DISPONIBLE' " +
    	       "AND m.etat IN ('TERMINEE', 'ANNULEE', 'REFUSEE')")
    	List<Vehicule> findVehiculesDisponibleWithEtatMissionTermineeOrAnnuleeOrRefusee();
    Optional<Vehicule> findByImmatriculation(String immatriculation);
    List<Vehicule> findByEtatIn(List<EtatVehicule> etats);

    
}
