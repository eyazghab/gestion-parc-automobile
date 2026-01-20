package com.example.demo.Dto;

import java.time.LocalDate;

public class InscriptionDTO {
	    // Utilisateur
	private String nom;
    private String prenom;
    private String email;
    private String motDePasse;
    private String tel;
    private String cin;
    private LocalDate dateEmbauche;

	    // OrdreMission
	    // Ordre de mission
	    private String objetMission;
	    private String lieuDepart;
	    private String lieuArrivee;
	    private LocalDate dateDepart;
	    private LocalDate dateRetour;
	    
	    
	    // getters/setters

		public String getNom() {
			return nom;
		}
		public void setNom(String nom) {
			this.nom = nom;
		}
		public String getEmail() {
			return email;
		}
		public void setEmail(String email) {
			this.email = email;
		}
		public String getMotDePasse() {
			return motDePasse;
		}
		public void setMotDePasse(String motDePasse) {
			this.motDePasse = motDePasse;
		}
		
		public LocalDate getDateDepart() {
			return dateDepart;
		}
		public void setDateDepart(LocalDate dateDepart) {
			this.dateDepart = dateDepart;
		}
		public String getObjetMission() {
			return objetMission;
		}
		public void setObjetMission(String objetMission) {
			this.objetMission = objetMission;
		}
		public String getLieuDepart() {
			return lieuDepart;
		}
		public void setLieuDepart(String lieuDepart) {
			this.lieuDepart = lieuDepart;
		}
		public String getLieuArrivee() {
			return lieuArrivee;
		}
		public void setLieuArrivee(String lieuArrivee) {
			this.lieuArrivee = lieuArrivee;
		}
		public LocalDate getDateRetour() {
			return dateRetour;
		}
		public void setDateRetour(LocalDate dateRetour) {
			this.dateRetour = dateRetour;
		}
		public String getPrenom() {
			return prenom;
		}
		public void setPrenom(String prenom) {
			this.prenom = prenom;
		}
		public String getTel() {
			return tel;
		}
		public void setTel(String tel) {
			this.tel = tel;
		}
		public String getCin() {
			return cin;
		}
		public void setCin(String cin) {
			this.cin = cin;
		}
		public LocalDate getDateEmbauche() {
			return dateEmbauche;
		}
		public void setDateEmbauche(LocalDate dateEmbauche) {
			this.dateEmbauche = dateEmbauche;
		}

	}


