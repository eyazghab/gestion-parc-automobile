package com.example.demo.Dto;


import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import com.example.demo.model.OrdreMission;

public class MissionCarburantDTO {
    private Long id;
    private String destination;
    private String utilisateurNom;
    private String dateDepart;
    private String dateRetour;
    private Double distanceEstimee;
    private Double carburantNecessaire;
    

    public MissionCarburantDTO(Long id, String destination, String utilisateurNom, String dateDepart,
                               String dateRetour, Double distanceEstimee, Double carburantNecessaire) {
        this.id = id;
        this.destination = destination;
        this.utilisateurNom = utilisateurNom;
        this.dateDepart = dateDepart;
        this.dateRetour = dateRetour;
        this.distanceEstimee = distanceEstimee;
        this.carburantNecessaire = carburantNecessaire;
    }

	

	public Long getId() {
		return id;
	}



	public void setId(Long id) {
		this.id = id;
	}



	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	

	public String getDateDepart() {
		return dateDepart;
	}



	public void setDateDepart(String dateDepart) {
		this.dateDepart = dateDepart;
	}



	public String getDateRetour() {
		return dateRetour;
	}



	public void setDateRetour(String dateRetour) {
		this.dateRetour = dateRetour;
	}



	public Double getDistanceEstimee() {
		return distanceEstimee;
	}

	public void setDistanceEstimee(Double distanceEstimee) {
		this.distanceEstimee = distanceEstimee;
	}

	public Double getCarburantNecessaire() {
		return carburantNecessaire;
	}

	public void setCarburantNecessaire(Double carburantNecessaire) {
		this.carburantNecessaire = carburantNecessaire;
	}
    public String getUtilisateurNom() {
        return utilisateurNom;
    }

    public void setUtilisateurNom(String utilisateurNom) {
        this.utilisateurNom = utilisateurNom;
    }
 // ✅ Constructeur à partir de l’entité OrdreMission
    public MissionCarburantDTO(OrdreMission mission) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        this.id = mission.getId();
        this.destination = mission.getDestination();

        // suppose que OrdreMission a un champ utilisateur avec nom
        this.utilisateurNom = mission.getUtilisateur() != null ? mission.getUtilisateur().getNom() : null;

        this.dateDepart = mission.getDateDepart() != null ? mission.getDateDepart().format(formatter) : null;
        this.dateRetour = mission.getDateRetour() != null ? mission.getDateRetour().format(formatter) : null;

        this.distanceEstimee = mission.getDistanceEstimee();
        this.carburantNecessaire = mission.getCarburantNecessaire();
    }
}
