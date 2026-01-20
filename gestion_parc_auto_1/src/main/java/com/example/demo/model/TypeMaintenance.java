package com.example.demo.model;

import java.util.Date;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

@Entity
public class TypeMaintenance {
	 @Id@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String libelle;   // "Vidange", "Freins", "Pneus", ...
    private String description;
    private Integer periodiciteKm;       // ex: 10000
    private Integer periodiciteMois;     // ex: 12
	 @Temporal(TemporalType.DATE)
    private Date date_creation;
	 private boolean actif = true;


    @OneToMany(mappedBy = "typeMaintenance")
    private List<LigneMaintenance> lignes;

	public String getLibelle() {
		return libelle;
	}

	public void setLibelle(String libelle) {
		this.libelle = libelle;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getDate_creation() {
		return date_creation;
	}

	public void setDate_creation(Date date_creation) {
		this.date_creation = date_creation;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getPeriodiciteKm() {
		return periodiciteKm;
	}

	public void setPeriodiciteKm(Integer periodiciteKm) {
		this.periodiciteKm = periodiciteKm;
	}

	public Integer getPeriodiciteMois() {
		return periodiciteMois;
	}

	public void setPeriodiciteMois(Integer periodiciteMois) {
		this.periodiciteMois = periodiciteMois;
	}

	public List<LigneMaintenance> getLignes() {
		return lignes;
	}

	public void setLignes(List<LigneMaintenance> lignes) {
		this.lignes = lignes;
	}
	 public boolean isActif() { return actif; }
	    public void setActif(boolean actif) { this.actif = actif; }

public TypeMaintenance(Long id, String libelle) {
    this.id = id;
    this.libelle = libelle;
}

//  Constructeur par défaut obligatoire pour JPA
public TypeMaintenance() {}
//Constructeur pratique utilisé dans le DataSeeder
public TypeMaintenance(String libelle, String description, int periodiciteKm, int periodiciteMois) {
    this.libelle = libelle;
    this.description = description;
    this.periodiciteKm = periodiciteKm;
    this.periodiciteMois = periodiciteMois;
    this.actif = true;
    this.date_creation = new Date();
}
}
