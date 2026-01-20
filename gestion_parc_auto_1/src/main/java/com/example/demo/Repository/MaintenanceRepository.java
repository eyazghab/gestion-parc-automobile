package com.example.demo.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.demo.Dto.MaintenanceDTO;
import com.example.demo.model.Maintenance;
import com.example.demo.model.TypeMaintenance;
import com.example.demo.model.Vehicule;
@Repository
public interface MaintenanceRepository extends JpaRepository<Maintenance, Long> {
	
    // Récupère toutes les maintenances avec fetch join sur les lignes
    @Query("SELECT DISTINCT m FROM Maintenance m LEFT JOIN FETCH m.lignes")
    List<Maintenance> findAllWithLignes();
    // Dernière maintenance d’un type donné pour un véhicule
    Optional<Maintenance> findTopByVehiculeAndTypeMaintenanceLibelleOrderByDateMaintenanceDesc(Vehicule v, String libelle);
    List<Maintenance> findByVehiculeOrderByDateMaintenanceDesc(Vehicule v);
    Maintenance findTopByVehiculeOrderByDateMaintenanceDesc(Vehicule vehicule);

}