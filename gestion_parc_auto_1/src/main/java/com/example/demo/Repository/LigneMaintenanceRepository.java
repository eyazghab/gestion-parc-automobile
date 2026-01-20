package com.example.demo.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.LigneMaintenance;
@Repository
public interface LigneMaintenanceRepository extends JpaRepository<LigneMaintenance, Long>{
	// Récupérer toutes les lignes d'une maintenance
    List<LigneMaintenance> findByMaintenanceIdMaintenance(Long maintenanceId);
}
