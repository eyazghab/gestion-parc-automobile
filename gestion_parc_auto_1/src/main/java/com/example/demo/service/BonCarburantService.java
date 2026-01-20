package com.example.demo.service;

import java.util.List;

import com.example.demo.Dto.BonCarburantCreateDTO;
import com.example.demo.Dto.BonCarburantDTO;
import com.example.demo.model.BonCarburant;
import com.example.demo.model.BonCarburant.EtatBon;
import com.example.demo.model.Carburant;
import com.example.demo.model.Vehicule;

public interface BonCarburantService {
	    BonCarburant addBon(BonCarburantCreateDTO bon); // Ajouter un bon carburant
	    List<BonCarburantDTO> getBonsByUtilisateur(Long utilisateurId);
	    List<BonCarburant> getBonsByVehicule(Long idVehicule);
	    List<BonCarburantDTO> getTousLesBons();
	    void supprimerBon(Long id);
	    double getConsommationTotale(Long vehiculeId); // Somme des quantités pour un véhicule
	 // Gestion des bons de carburant
	    BonCarburant demanderBon(Vehicule vehicule, Carburant carburant, Double quantite, Long utilisateurId);
	    BonCarburant validerBon(Long bonId, String action); // action = "ACCEPTE" / "REFUSE"
	    BonCarburant modifierQuantite(Long id, double quantite);
	    List<BonCarburant> findByEtat(EtatBon etat);
	    double verifierBon(Long bonId);

}
