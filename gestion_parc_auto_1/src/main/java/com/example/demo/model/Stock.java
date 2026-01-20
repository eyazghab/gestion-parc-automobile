package com.example.demo.model;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"depot_id","article_id"}))
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")

public class Stock {
	 @Id@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int quantite_disp;
    private int quantite_reservee;
    private int stock_min;
    private int stock_alerte;
	 @Temporal(TemporalType.DATE)
    private Date date_dernier_entree;
	 @Temporal(TemporalType.DATE)
	    private Date date_dernier_sortie;
	 @Temporal(TemporalType.DATE)
    private Date date_dernier_inventaire;
    private String Iot;
    private String motif;
    private boolean actif = true; // Pour activer/désactiver

    @ManyToOne
    private Depot depot;

    @ManyToOne
    private Article article;

    @OneToMany(mappedBy = "stock", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<MouvemmentStock> mouvements;
    
    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
	public int getQuantite_disp() {
		return quantite_disp;
	}

	public void setQuantite_disp(int quantite_disp) {
		this.quantite_disp = quantite_disp;
	}

	public int getQuantite_reservee() {
		return quantite_reservee;
	}

	public void setQuantite_reservee(int quantite_reservee) {
		this.quantite_reservee = quantite_reservee;
	}

	public Date getDate_dernier_entree() {
		return date_dernier_entree;
	}

	public void setDate_dernier_entree(Date date_dernier_entree) {
		this.date_dernier_entree = date_dernier_entree;
	}

	public Date getDate_dernier_inventaire() {
		return date_dernier_inventaire;
	}

	public void setDate_dernier_inventaire(Date date_dernier_inventaire) {
		this.date_dernier_inventaire = date_dernier_inventaire;
	}

	public String getIot() {
		return Iot;
	}

	public void setIot(String iot) {
		Iot = iot;
	}
	public void setDate_dernier_sortie(Date date_dernier_sortie) {
		this.date_dernier_sortie = date_dernier_sortie;
	}

	public boolean isActif() {
		return actif;
	}

	public void setActif(boolean actif) {
		this.actif = actif;
	}
	public Date getDate_dernier_sortie() {
		return date_dernier_sortie;
	}

	public int getStock_min() {
		return stock_min;
	}

	public void setStock_min(int stock_min) {
		this.stock_min = stock_min;
	}

	public int getStock_alerte() {
		return stock_alerte;
	}

	public void setStock_alerte(int stock_alerte) {
		this.stock_alerte = stock_alerte;
	}

	public Depot getDepot() {
		return depot;
	}

	public void setDepot(Depot depot) {
		this.depot = depot;
	}

	public Article getArticle() {
		return article;
	}

	public void setArticle(Article article) {
		this.article = article;
	}

	public List<MouvemmentStock> getMouvements() {
		return mouvements;
	}

	public void setMouvements(List<MouvemmentStock> mouvements) {
		this.mouvements = mouvements;
	}

	public String getMotif() {
		return motif;
	}

	public void setMotif(String motif) {
		this.motif = motif;
	}
	
	}