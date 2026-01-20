package com.example.demo.Dto;

import com.example.demo.model.OrdreMission;

public class OrdreMissionDTO {
    private Long id;
    private String destination;
    private String motif;
    private String etat;
    private String utilisateurNom;
    private String vehiculeImmatriculation;
    private String dateDepart;
    private String dateRetour;
    private Double distanceEstimee;
    private Double carburantNecessaire;
    private Long vehiculeId;

    public OrdreMissionDTO() {
        // constructeur par défaut nécessaire pour Jackson
    }

    // Constructeur avec mapping depuis OrdreMission
    public OrdreMissionDTO(OrdreMission mission) {
        this.id = mission.getId();
        this.destination = mission.getDestination();
        this.motif = mission.getMotif();
        this.etat = mission.getEtat() != null ? mission.getEtat().name() : null;
        this.utilisateurNom = mission.getUtilisateur() != null ? mission.getUtilisateur().getNom() : null;

        if (mission.getVehicule() != null) {
            this.vehiculeImmatriculation = mission.getVehicule().getImmatriculation();
            this.vehiculeId = mission.getVehicule().getIdVehicule();
        }

        this.dateDepart = mission.getDateDepart() != null ? mission.getDateDepart().toString() : null;
        this.dateRetour = mission.getDateRetour() != null ? mission.getDateRetour().toString() : null;

        this.distanceEstimee = mission.getDistanceEstimee();
        this.carburantNecessaire = mission.getCarburantNecessaire();
    }

    // Getters & Setters

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

    public String getMotif() {
        return motif;
    }

    public void setMotif(String motif) {
        this.motif = motif;
    }

    public String getEtat() {
        return etat;
    }

    public void setEtat(String etat) {
        this.etat = etat;
    }

    public String getUtilisateurNom() {
        return utilisateurNom;
    }

    public void setUtilisateurNom(String utilisateurNom) {
        this.utilisateurNom = utilisateurNom;
    }

    public String getVehiculeImmatriculation() {
        return vehiculeImmatriculation;
    }

    public void setVehiculeImmatriculation(String vehiculeImmatriculation) {
        this.vehiculeImmatriculation = vehiculeImmatriculation;
    }

    public Long getVehiculeId() {
        return vehiculeId;
    }

    public void setVehiculeId(Long vehiculeId) {
        this.vehiculeId = vehiculeId;
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
}
