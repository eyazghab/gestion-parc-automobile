package com.example.demo.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

@Entity
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")

public class MouvemmentStock {
	 @Id@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	 @Temporal(TemporalType.TIMESTAMP) // pour garder date + heure
	 private Date date;
    private int quantite;
    private String type; // entrée ou sortie
    private int quantiteAvant;
    private int quantiteApres;
    private String motif;
    private String commantaire;
    private int ancienPrix;  
    private int nouveauPrix;
    //Les Relations 
    @ManyToOne
    @JoinColumn(name = "stock_id")
    private Stock stock;
    @ManyToOne
    @JoinColumn(name = "article_id")
    private Article article;

    
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

	public int getQuantite() {
		return quantite;
	}

	public void setQuantite(int quantite) {
		this.quantite = quantite;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getQuantiteAvant() {
		return quantiteAvant;
	}

	public void setQuantiteAvant(int quantiteAvant) {
		this.quantiteAvant = quantiteAvant;
	}

	public int getQuantiteApres() {
		return quantiteApres;
	}

	public void setQuantiteApres(int quantiteApres) {
		this.quantiteApres = quantiteApres;
	}

	public String getMotif() {
		return motif;
	}

	public void setMotif(String motif) {
		this.motif = motif;
	}

	public String getCommantaire() {
		return commantaire;
	}

	public void setCommantaire(String commantaire) {
		this.commantaire = commantaire;
	}
	
	public void setStock(Stock stock) {
		this.stock = stock;
	}

	public Article getArticle() {
		return article;
	}

	public void setArticle(Article article) {
		this.article = article;
	}

	public Stock getStock() {
		return stock;
	}

	public int getAncienPrix() {
		return ancienPrix;
	}

	public void setAncienPrix(int ancienPrix) {
		this.ancienPrix = ancienPrix;
	}

	public int getNouveauPrix() {
		return nouveauPrix;
	}

	public void setNouveauPrix(int nouveauPrix) {
		this.nouveauPrix = nouveauPrix;
	}

	
	
}