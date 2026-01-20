package com.example.demo.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

@Entity
public class Commande {
	 @Id@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	private int numeroCommande;
	 @Temporal(TemporalType.DATE)
    private Date date;
	 private double montant_ht;
	 private double montant_tva;
	 private double montant_ttc;
    private String commentaire;
    @Temporal(TemporalType.DATE)
    private Date date_creation;
    @Temporal(TemporalType.DATE)
    private Date date_modification;
    @Enumerated(EnumType.STRING)
    private StatutCommande statut;
    private boolean actif = true;
    private String justificationAnnulation;
    // les relations : 
    @ManyToOne
    @JoinColumn(name = "fournisseur_id")
    private Fournisseur fournisseur;


    @OneToMany(mappedBy = "commande", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<LigneCommande> lignes= new ArrayList<>();
    
    @OneToOne(mappedBy = "commande", cascade = CascadeType.ALL)
    @JsonBackReference
    private Facture facture;
    
    public enum StatutCommande {
        EN_ATTENTE, VALIDEE, REJETEE, LIVREE, ANNULEE,EN_COURS

    }
 // Getters & Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public int getNumeroCommande() {
		return numeroCommande;
	}

	public void setNumeroCommande(int numeroCommande) {
		this.numeroCommande = numeroCommande;
	}

	public double getMontant_ht() { return montant_ht; }
	public void setMontant_ht(double montant_ht) { this.montant_ht = montant_ht; }

	public double getMontant_tva() { return montant_tva; }
	public void setMontant_tva(double montant_tva) { this.montant_tva = montant_tva; }

	public double getMontant_ttc() { return montant_ttc; }
	public void setMontant_ttc(double montant_ttc) { this.montant_ttc = montant_ttc; }

	public String getCommentaire() {
		return commentaire;
	}

	public void setCommentaire(String commentaire) {
		this.commentaire = commentaire;
	}

	public Date getDate_creation() {
		return date_creation;
	}

	public void setDate_creation(Date date_creation) {
		this.date_creation = date_creation;
	}

	public Date getDate_modification() {
		return date_modification;
	}

	public void setDate_modification(Date date_modification) {
		this.date_modification = date_modification;
	}
	public void setFournisseur(Fournisseur fournisseur) {
		this.fournisseur = fournisseur;
	}

	public List<LigneCommande> getLignes() {
		return lignes;
	}

	public void setLignes(List<LigneCommande> lignes) {
		this.lignes = lignes;
	}

	public Facture getFacture() {
		return facture;
	}

	public void setFacture(Facture facture) {
		this.facture = facture;
	}

	public Fournisseur getFournisseur() {
		return fournisseur;
	}

	public StatutCommande getStatut() {
		return statut;
	}

	public void setStatut(StatutCommande statut) {
		this.statut = statut;
	}

	public boolean isActif() {
		return actif;
	}

	public void setActif(boolean actif) {
		this.actif = actif;
	}

	public String getJustificationAnnulation() {
		return justificationAnnulation;
	}

	public void setJustificationAnnulation(String justificationAnnulation) {
		this.justificationAnnulation = justificationAnnulation;
	}
	
}
