package com.example.demo.model;


import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;

@Entity
public class Technicien {
	public enum TypeTechnicien {
        INTERNE,
        EXTERNE
    }
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idTechnicien;
	    private String nom;  // Nom du technicien
	    private String prenom;  // Prénom du technicien
	    private String specialite;  // Spécialité du technicien (ex : moteur, électricité, etc.)
	    private String emailTech;
	    private String  telephoneTech;
	    private boolean actif = true;
	    @Enumerated(EnumType.STRING)
	    private TypeTechnicien type; // interne ou externe

	    
	    //Les Relations : 
	    @OneToMany(mappedBy = "technicien", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	    @JsonIgnore// ⚡ Empêche Jackson de sérialiser toutes les maintenances et boucler
	    private List<Maintenance> maintenances;
		public String getSpecialite() {
			return specialite;
		}

		public void setSpecialite(String specialite) {
			this.specialite = specialite;
		}

		public String getPrenom() {
			return prenom;
		}

		public void setPrenom(String prenom) {
			this.prenom = prenom;
		}

		public String getNom() {
			return nom;
		}

		public void setNom(String nom) {
			this.nom = nom;
		}

		public String getEmailTech() {
			return emailTech;
		}

		public void setEmailTech(String emailTech) {
			this.emailTech = emailTech;
		}

		public String getTelephoneTech() {
			return telephoneTech;
		}

		public void setTelephoneTech(String telephoneTech) {
			this.telephoneTech = telephoneTech;
		}
		public Long getIdTechnicien() {
			return idTechnicien;
		}
		public void setIdTechnicien(Long idTechnicien) {
			this.idTechnicien = idTechnicien;
		}

		public boolean isActif() {
			return actif;
		}

		public void setActif(boolean actif) {
			this.actif = actif;
		}

		public TypeTechnicien getType() {
			return type;
		}

		public void setType(TypeTechnicien type) {
			this.type = type;
		}

		public List<Maintenance> getMaintenances() {
			return maintenances;
		}

		public void setMaintenances(List<Maintenance> maintenances) {
			this.maintenances = maintenances;
		}
		
}