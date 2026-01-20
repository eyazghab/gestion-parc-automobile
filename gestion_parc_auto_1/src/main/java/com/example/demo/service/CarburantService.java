package com.example.demo.service;

import java.util.List;

import com.example.demo.Dto.MissionCarburantDTO;
import com.example.demo.Dto.UtilisateurAvecBonsDTO;
import com.example.demo.model.Carburant;
import com.example.demo.model.Vehicule;

public interface CarburantService {
	 List<Carburant> getAllCarburants();
	    Carburant addCarburant(Carburant carburant);
	    Carburant getCarburantById(Long id);
	    void deleteCarburant(Long id);
	 // Calcul du carburant nécessaire pour un véhicule
	    double calculCarburantNecessaire(Vehicule vehicule);
	    List<MissionCarburantDTO> getMissionsAvecCarburant(Vehicule vehicule);
	    List<UtilisateurAvecBonsDTO> getBonsParUtilisateur();

}
