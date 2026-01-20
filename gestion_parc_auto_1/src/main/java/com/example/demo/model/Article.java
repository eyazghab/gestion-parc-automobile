package com.example.demo.model;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})

public class Article {
	 @Id@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idArticle;
	@Column(unique=true, nullable=false)
    private String reference;
    private String nom;
    private String description; 
    private int prix;
    private boolean actif = true;
    @Column(nullable = true)
    private String imageUrl;
    
    // les Relations : 
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "fournisseur_id_fournisseur")
    @JsonIgnoreProperties({"articles", "hibernateLazyInitializer", "handler"})
    private Fournisseur fournisseur;
    
    @OneToMany(mappedBy = "article", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<MouvemmentStock> mouvements;
    
    @OneToMany(mappedBy = "article", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<LigneCommande> lignesCommande;  // Lignes de commande où cet article apparaît

    
 // Getters & Setters
 // ✅ Constructeur par défaut obligatoire
    public Article() { }
    public Long getIdArticle() {
        return idArticle;
    }

    public void setIdArticle(Long idArticle) {
        this.idArticle = idArticle;
    }
	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}


	public String getReference() {
		return reference;
	}
	public void setReference(String reference) {
		this.reference = reference;
	}


	public int getPrix() {
		return prix;
	}

	public void setPrix(int prix) {
		this.prix = prix;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<MouvemmentStock> getMouvements() {
		return mouvements;
	}

	public void setMouvements(List<MouvemmentStock> mouvements) {
		this.mouvements = mouvements;
	}

	public List<LigneCommande> getLignesCommande() {
		return lignesCommande;
	}

	public void setLignesCommande(List<LigneCommande> lignesCommande) {
		this.lignesCommande = lignesCommande;
	}
	public void setActif(boolean actif) {
		this.actif = actif;
	}
	public boolean isActif() {
		return actif;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public Fournisseur getFournisseur() {
		return fournisseur;
	}

	public void setFournisseur(Fournisseur fournisseur) {
		this.fournisseur = fournisseur;
	}
	public Article(String reference, String nom, String description, int prix, String imageUrl, Fournisseur fournisseur) {
	    this.reference = reference;
	    this.nom = nom;
	    this.description = description;
	    this.prix = prix;
	    this.imageUrl = imageUrl;
	    this.fournisseur = fournisseur;
	}
	
}