package com.example.demo.model;


import java.time.LocalDate;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;

@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})

public class BonCarburant {
    
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double montant;      // Montant du bon
    private Double quantite;     // Quantité achetée
    @Temporal(TemporalType.DATE)
    private Date dateAchat;      // Date d'achat
    private String responsable;  // Nom de la personne
    private LocalDate dateDemande;     // Date de la demande
    private LocalDate dateValidation;  // Date de validation par l'admin
    @Enumerated(EnumType.STRING)
    private EtatBon etat = EtatBon.EN_ATTENTE; // valeur par défaut  EN_ATTENTE, ACCEPTE, REFUSE
    
    public enum EtatBon {
        EN_ATTENTE,
        ACCEPTE,
        REFUSE
    }
    // Relations
    @ManyToOne
    @JoinColumn(name = "vehicule_id")
    private Vehicule vehicule;

    @ManyToOne
    @JoinColumn(name = "carburant_id", nullable = false)
    private Carburant carburant;

    @ManyToOne
    @JoinColumn(name = "utilisateur_id", nullable = false)
    private Utilisateur utilisateur; 
    
    @ManyToOne
    @JoinColumn(name = "mission_id")
    private OrdreMission mission;
    
	public Double getMontant() {
		return montant;
	}

	public void setMontant(Double montant) {
		this.montant = montant;
	}

	public Double getQuantite() {
		return quantite;
	}

	public void setQuantite(Double quantite) {
		this.quantite = quantite;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getDateAchat() {
		return dateAchat;
	}

	public void setDateAchat(Date dateAchat) {
		this.dateAchat = dateAchat;
	}

	public String getResponsable() {
		return responsable;
	}

	public void setResponsable(String responsable) {
		this.responsable = responsable;
	}

	public Carburant getCarburant() {
		return carburant;
	}

	public void setCarburant(Carburant carburant) {
		this.carburant = carburant;
	}

	public Utilisateur getUtilisateur() {
		return utilisateur;
	}

	public void setUtilisateur(Utilisateur utilisateur) {
		this.utilisateur = utilisateur;
	}

	public Vehicule getVehicule() {
		return vehicule;
	}

	public void setVehicule(Vehicule vehicule) {
		this.vehicule = vehicule;
	}

	public LocalDate getDateDemande() {
		return dateDemande;
	}

	public void setDateDemande(LocalDate dateDemande) {
		this.dateDemande = dateDemande;
	}

	public LocalDate getDateValidation() {
		return dateValidation;
	}

	public void setDateValidation(LocalDate dateValidation) {
		this.dateValidation = dateValidation;
	}

	public EtatBon getEtat() {
		return etat;
	}

	public void setEtat(EtatBon etat) {
		this.etat = etat;
	}

	public OrdreMission getMission() {
		return mission;
	}

	public void setMission(OrdreMission mission) {
		this.mission = mission;
	}

	
	
}