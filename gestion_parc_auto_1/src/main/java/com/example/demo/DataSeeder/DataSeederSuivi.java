package com.example.demo.DataSeeder;

import com.example.demo.Repository.SuiviRepository;
import com.example.demo.Repository.VehiculeRepository;
import com.example.demo.Repository.AlerteHistoriqueRepository;
import com.example.demo.model.Suivi;
import com.example.demo.model.Vehicule;
import com.example.demo.model.AlerteHistorique;
import com.example.demo.model.EtatVehicule;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Order(9)
@Configuration
public class DataSeederSuivi {

	@Bean
	CommandLineRunner initSuiviTest(
	        SuiviRepository suiviRepository,
	        VehiculeRepository vehiculeRepository,
	        AlerteHistoriqueRepository alerteRepo
	) {
	    return args -> {
	        List<Vehicule> vehicules = vehiculeRepository.findAll();

	        for (int i = 0; i < vehicules.size(); i++) {
	            Vehicule v = vehicules.get(i);
	            Suivi suivi = new Suivi();
	            suivi.setVehicule(v);
	            suivi.setDescription("Suivi de test pour le véhicule " + v.getImmatriculation());

	            LocalDate now = LocalDate.now();
	            LocalDate dateSuivi = now.plusDays(i); // Décalage par i jours
	            suivi.setDateSuivi(dateSuivi);
	            suivi.setDateDerniersuivi(dateSuivi);
	            suivi.setKilometreActuel(v.getKilometrageActuel());

	            // --- Champs aléatoires pour test ---
	            suivi.setKilometresDepuisFreins((int) (Math.random() * 25000));
	            suivi.setKilometresDepuisVidange((int) (Math.random() * 18000));
	            suivi.setDureeVieBatterie((int) (Math.random() * 48));
	            suivi.setNombreAccidents((int) (Math.random() * 2));
	            suivi.setNombrePannes((int) (Math.random() * 2));

	            // --- Données techniques ---
	            suivi.setFrequence(5000);
	            suivi.setDureeEstimé(1);
	            suivi.setDateTemp(dateSuivi.plusMonths(1));
	            suivi.setDateKm(dateSuivi.plusDays(90));

	            // --- État initial du véhicule ---
	            v.setEtat(EtatVehicule.DISPONIBLE);

	            suiviRepository.save(suivi);
	            System.out.println("✅ Suivi créé pour véhicule: " + v.getImmatriculation());

	            // --- Génération d'alertes spécifiques ---
	            if (suivi.getKilometresDepuisVidange() >= 15000) {
	                AlerteHistorique vidange = new AlerteHistorique();
	                vidange.setVehicule(v);
	                vidange.setMessage("⚠️ Vidange à effectuer pour " + v.getImmatriculation());
	                vidange.setDateEnvoi(LocalDateTime.now());
	                vidange.setDestinataire(null);
	                alerteRepo.save(vidange);
	                System.out.println("🚨 Alerte Vidange créée pour: " + v.getImmatriculation());
	            }

	            if (suivi.getKilometresDepuisFreins() >= 20000) {
	                AlerteHistorique freins = new AlerteHistorique();
	                freins.setVehicule(v);
	                freins.setMessage("⚠️ Vérification des freins nécessaire pour " + v.getImmatriculation());
	                freins.setDateEnvoi(LocalDateTime.now());
	                freins.setDestinataire(null);
	                alerteRepo.save(freins);
	                System.out.println("🚨 Alerte Freins créée pour: " + v.getImmatriculation());
	            }

	            if (suivi.getDureeVieBatterie() > 24) {
	                AlerteHistorique batterie = new AlerteHistorique();
	                batterie.setVehicule(v);
	                batterie.setMessage("⚠️ Batterie à remplacer pour " + v.getImmatriculation());
	                batterie.setDateEnvoi(LocalDateTime.now());
	                batterie.setDestinataire(null);
	                alerteRepo.save(batterie);
	                System.out.println("🚨 Alerte Batterie créée pour: " + v.getImmatriculation());
	            }

	            if (suivi.getNombreAccidents() > 0) {
	                AlerteHistorique accident = new AlerteHistorique();
	                accident.setVehicule(v);
	                accident.setMessage("⚠️ Accident signalé pour " + v.getImmatriculation());
	                accident.setDateEnvoi(LocalDateTime.now());
	                accident.setDestinataire(null);
	                alerteRepo.save(accident);
	                System.out.println("🚨 Alerte Accident créée pour: " + v.getImmatriculation());
	            }

	            if (suivi.getNombrePannes() > 0) {
	                AlerteHistorique panne = new AlerteHistorique();
	                panne.setVehicule(v);
	                panne.setMessage("⚠️ Panne détectée pour " + v.getImmatriculation());
	                panne.setDateEnvoi(LocalDateTime.now());
	                panne.setDestinataire(null);
	                alerteRepo.save(panne);
	                System.out.println("🚨 Alerte Panne créée pour: " + v.getImmatriculation());
	            }
	        }
	    };
	}
}