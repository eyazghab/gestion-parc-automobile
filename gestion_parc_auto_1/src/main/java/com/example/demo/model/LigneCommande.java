package com.example.demo.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Transient;

@Entity
public class LigneCommande {
	 @Id@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int quantite;
    private String description; 
    private int quantiteRecu;
    private Float taux_tva;
    private Float remise;
    private int montant_ht;
    private int montant_ttt;
    @Temporal(TemporalType.DATE)
    private Date date_livraison_prevue;
    private int PrixUnitaire;
    @Transient
    private Long idArticleTemp;
    
 // Relation ManyToOne avec Article
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "article_id")
    private Article article;

    @ManyToOne
    @JoinColumn(name = "commande_id")
    @JsonBackReference
    private Commande commande;

    // Getters & Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

	public int getQuantite() {
		return quantite;
	}

	public void setQuantite(int quantite) {
		this.quantite = quantite;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getQuantiteRecu() {
		return quantiteRecu;
	}

	public void setQuantiteRecu(int quantiteRecu) {
		this.quantiteRecu = quantiteRecu;
	}

	public Float getTaux_tva() {
		return taux_tva;
	}

	public void setTaux_tva(Float taux_tva) {
		this.taux_tva = taux_tva;
	}

	public Float getRemise() {
		return remise;
	}

	public void setRemise(Float remise) {
		this.remise = remise;
	}

	public int getMontant_ht() {
		return montant_ht;
	}

	public void setMontant_ht(int montant_ht) {
		this.montant_ht = montant_ht;
	}

	public int getMontant_ttt() {
		return montant_ttt;
	}

	public void setMontant_ttt(int montant_ttt) {
		this.montant_ttt = montant_ttt;
	}

	public Date getDate_livraison_prevue() {
		return date_livraison_prevue;
	}

	public void setDate_livraison_prevue(Date date_livraison_prevue) {
		this.date_livraison_prevue = date_livraison_prevue;
	}
	public void setArticle(Article article) {
		this.article = article;
	}
	public void setCommande(Commande commande) {
		this.commande = commande;
	}

	public Article getArticle() {
		return article;
	}

	public Commande getCommande() {
		return commande;
	}

	public int getPrixUnitaire() {
		return PrixUnitaire;
	}

	public void setPrixUnitaire(int prixUnitaire) {
		PrixUnitaire = prixUnitaire;
	}

	public Long getIdArticleTemp() {
		return idArticleTemp;
	}

	public void setIdArticleTemp(Long idArticleTemp) {
		this.idArticleTemp = idArticleTemp;
	}
	
}
