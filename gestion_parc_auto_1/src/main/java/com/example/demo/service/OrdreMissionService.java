package com.example.demo.service;

import java.time.LocalDateTime;
import java.util.List;

import com.example.demo.Dto.MissionCarburantDTO;
import com.example.demo.Dto.OrdreMissionDTO;
import com.example.demo.model.EtatMission;
import com.example.demo.model.OrdreMission;

public interface OrdreMissionService {
	    List<OrdreMissionDTO> getAllOrdresMission();
	    OrdreMission getOrdreMissionById(Long id);
	    void deleteOrdreMission(Long id);
	    OrdreMission createOrdreMission(OrdreMission mission);
	    boolean isVehiculeEnMission(Long vehiculeId, LocalDateTime dateDepart, LocalDateTime dateRetour);
	    boolean isUtilisateurEnMission(Long utilisateurId, LocalDateTime dateDepart, LocalDateTime dateRetour);
	    List<OrdreMission> findByEtatIn(List<String> etats);
	    OrdreMission saveOrdreMission(OrdreMission ordreMission);
	    OrdreMission changerEtatEtNotifier(Long idMission, EtatMission nouvelEtat);
	    List<OrdreMissionDTO> getOrdresMissionByUtilisateurId(Long idUtilisateur);
	    OrdreMission annulerOrdreMission(Long idOrdreMission);
	 // Méthode pour mettre à jour les états des missions (EN_COURS, TERMINEE)
	    //void mettreAJourEtatDesMissions();
	    OrdreMission terminerMission(Long id);
	    OrdreMission demarrerMission(Long idMission);
	    List<OrdreMission> findByEtat(EtatMission etat);
	    OrdreMissionDTO assignVehicule(Long missionId, Long vehiculeId);
	    List<MissionCarburantDTO> getMissionsByBonId(Long bonId);

	    
}
    

