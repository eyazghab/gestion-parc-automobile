package com.example.demo.Dto;

import java.util.Date;
import com.example.demo.model.BonCarburant.EtatBon;

public class BonCarburantDTO {

    private Long id;
    private Double montant;
    private Double quantite;
    private Date dateAchat;
    private String responsable;
    private VehiculeSimpleDTO vehicule;
    private String carburantNom;
    private String utilisateurNom;
    private EtatBon etat;

    public BonCarburantDTO() {}

    public BonCarburantDTO(Long id, Double montant, Double quantite, Date dateAchat,
                            String responsable, VehiculeSimpleDTO vehicule,
                            String carburantNom, String utilisateurNom,
                            EtatBon etat) {
        this.id = id;
        this.montant = montant;
        this.quantite = quantite;
        this.dateAchat = dateAchat;
        this.responsable = responsable;
        this.vehicule = vehicule;
        this.carburantNom = carburantNom;
        this.utilisateurNom = utilisateurNom;
        this.etat = etat;
    }

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Double getMontant() { return montant; }
    public void setMontant(Double montant) { this.montant = montant; }

    public Double getQuantite() { return quantite; }
    public void setQuantite(Double quantite) { this.quantite = quantite; }

    public Date getDateAchat() { return dateAchat; }
    public void setDateAchat(Date dateAchat) { this.dateAchat = dateAchat; }

    public String getResponsable() { return responsable; }
    public void setResponsable(String responsable) { this.responsable = responsable; }

    public VehiculeSimpleDTO getVehicule() { return vehicule; }
    public void setVehicule(VehiculeSimpleDTO vehicule) { this.vehicule = vehicule; }

    public String getCarburantNom() { return carburantNom; }
    public void setCarburantNom(String carburantNom) { this.carburantNom = carburantNom; }

    public String getUtilisateurNom() { return utilisateurNom; }
    public void setUtilisateurNom(String utilisateurNom) { this.utilisateurNom = utilisateurNom; }

    public EtatBon getEtat() { return etat; }
    public void setEtat(EtatBon etat) { this.etat = etat; }
}
