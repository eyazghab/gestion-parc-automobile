package com.example.demo.Repository;

import com.example.demo.model.AlerteHistorique;
import com.example.demo.model.Utilisateur;
import com.example.demo.model.Vehicule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
@Repository
public interface AlerteHistoriqueRepository extends JpaRepository<AlerteHistorique, Long> {
    Optional<AlerteHistorique> findByVehiculeAndDestinataireAndMessageAndDateEnvoiAfter(
        Vehicule vehicule, Utilisateur destinataire, String message, LocalDateTime after);
}
