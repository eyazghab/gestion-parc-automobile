package com.example.demo.model;


import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})

@Entity
public class Entreprise {
    
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idEntreprise;
    private String nomEntreprise;  // Nom de l'entreprise
    private String secteurActivite;  // Secteur d'activité de l'entreprise, par exemple "Transport", "Informatique", etc.
    private String adresse;  // Adresse de l'entreprise
    private String email;  // Email de l'entreprise
    private String telephone;  // Numéro de téléphone de l'entreprise
    private Integer nbEmployes;  // Nombre des employes  dans  l'entreprise

    //Les Relations 
    @OneToMany(mappedBy = "entreprise", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Departement> departements;
    
    // Getters et setters


    public Long getId() {
        return idEntreprise;
    }

    public void setId(Long id) {
        this.idEntreprise = id;
    }

	public String getNomEntreprise() {
		return nomEntreprise;
	}

	public void setNomEntreprise(String nomEntreprise) {
		this.nomEntreprise = nomEntreprise;
	}

	public String getSecteurActivite() {
		return secteurActivite;
	}

	public void setSecteurActivite(String secteurActivite) {
		this.secteurActivite = secteurActivite;
	}

	public String getAdresse() {
		return adresse;
	}

	public void setAdresse(String adresse) {
		this.adresse = adresse;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public Integer getNbEmployes() {
		return nbEmployes;
	}

	public void setNbEmployes(Integer nbEmployes) {
		this.nbEmployes = nbEmployes;
	}

}