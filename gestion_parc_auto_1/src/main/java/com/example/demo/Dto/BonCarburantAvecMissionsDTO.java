package com.example.demo.Dto;

import java.util.Date;
import java.util.List;

public class BonCarburantAvecMissionsDTO {
    private Long id;
    private Date dateAchat;
    private Double montant;
    private Double quantite;
    private String responsable;
    private String vehiculeImmatriculation;
    private List<MissionCarburantDTO> missions;

    public BonCarburantAvecMissionsDTO(Long id, Date dateAchat, Double montant, Double quantite,
                                      String responsable, String vehiculeImmatriculation,
                                      List<MissionCarburantDTO> missions) {
        this.id = id;
        this.dateAchat = dateAchat;
        this.montant = montant;
        this.quantite = quantite;
        this.responsable = responsable;
        this.vehiculeImmatriculation = vehiculeImmatriculation;
        this.missions = missions;
    }

    // Getters & setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Date getDateAchat() { return dateAchat; }
    public void setDateAchat(Date dateAchat) { this.dateAchat = dateAchat; }

    public Double getMontant() { return montant; }
    public void setMontant(Double montant) { this.montant = montant; }

    public Double getQuantite() { return quantite; }
    public void setQuantite(Double quantite) { this.quantite = quantite; }

    public String getResponsable() { return responsable; }
    public void setResponsable(String responsable) { this.responsable = responsable; }

    public String getVehiculeImmatriculation() { return vehiculeImmatriculation; }
    public void setVehiculeImmatriculation(String vehiculeImmatriculation) { this.vehiculeImmatriculation = vehiculeImmatriculation; }

    public List<MissionCarburantDTO> getMissions() { return missions; }
    public void setMissions(List<MissionCarburantDTO> missions) { this.missions = missions; }
}

