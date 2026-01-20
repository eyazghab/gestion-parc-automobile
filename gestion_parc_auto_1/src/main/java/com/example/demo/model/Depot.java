package com.example.demo.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})

public class Depot {
    @Id @GeneratedValue
    private Long idDepot;
    private String nom;
    private String localisation;

    @OneToMany(mappedBy = "depot")
    @JsonIgnore
    private List<Stock> stocks;

	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	public String getLocalisation() {
		return localisation;
	}

	public void setLocalisation(String localisation) {
		this.localisation = localisation;
	}
	public Long getIdDepot() {
		return idDepot;
	}

	public List<Stock> getStocks() {
		return stocks;
	}

	public void setStocks(List<Stock> stocks) {
		this.stocks = stocks;
	}

	public void setIdDepot(Long idDepot) {
		this.idDepot = idDepot;
	}

	  public Depot(String nom, String localisation) {
	        this.nom = nom;
	        this.localisation = localisation;
	    }
	  // ✅ Constructeur par défaut obligatoire
	    public Depot() { }
	
	
}