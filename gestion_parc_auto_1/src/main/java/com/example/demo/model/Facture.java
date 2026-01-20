package com.example.demo.model;


import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

@Entity
public class Facture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String numeroFacture;

    @Temporal(TemporalType.DATE)
    private Date dateFacture;

    private double  montantHT;
    private double  montantTVA;
    private double  montantTTC;

    @Enumerated(EnumType.STRING)
    private EtatFacture etat;
    
    public enum EtatFacture {
        PAYEE, NON_PAYEE, EN_ATTENTE
    }
    // Relation avec Commande
    @OneToOne
    @JoinColumn(name = "commande_id")
    @JsonManagedReference
    private Commande commande;

    // Getters & Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumeroFacture() {
        return numeroFacture;
    }

    public void setNumeroFacture(String numeroFacture) {
        this.numeroFacture = numeroFacture;
    }

    public Date getDateFacture() {
        return dateFacture;
    }

    public void setDateFacture(Date dateFacture) {
        this.dateFacture = dateFacture;
    }

    
    public void setMontantTVA(int montantTVA) {
        this.montantTVA = montantTVA;
    }



    public double getMontantHT() {
		return montantHT;
	}

	public void setMontantHT(double montantHT) {
		this.montantHT = montantHT;
	}

	public double getMontantTVA() {
		return montantTVA;
	}

	public void setMontantTVA(double montantTVA) {
		this.montantTVA = montantTVA;
	}

	public double getMontantTTC() {
		return montantTTC;
	}

	public void setMontantTTC(double montantTTC) {
		this.montantTTC = montantTTC;
	}

	public void setMontantTTC(int montantTTC) {
        this.montantTTC = montantTTC;
    }

 

    public EtatFacture getEtat() {
		return etat;
	}

	public void setEtat(EtatFacture etat) {
		this.etat = etat;
	}

	public Commande getCommande() {
        return commande;
    }

    public void setCommande(Commande commande) {
        this.commande = commande;
    }
}

