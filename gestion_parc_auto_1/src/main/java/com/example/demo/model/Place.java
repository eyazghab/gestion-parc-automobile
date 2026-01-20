package com.example.demo.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.*;

@Entity

public class Place {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPlaceParking;
	private String nom;
    private Integer numeroPlace;  // Numéro de la place de parking
    private Boolean estOccupee;  // Statut de la place (occupée ou libre)

    //Les Relations : 
    @ManyToOne
    @JoinColumn(name = "idParking", nullable = false)  // Clé étrangère vers l'entité Parking
    private Parking parking; public void setParking(Parking parking) {
		this.parking = parking;
	} // Parking auquel cette place appartient

    @ManyToOne
    @JoinColumn(name = "idVehicule") 
    @JsonIgnore // Ignorer côté JSON pour éviter la boucle infinie
    private Vehicule vehicule;

 // Getters et Setters
    public Parking getParking() {
		return parking;
	}
    public Vehicule getVehicule() {
		return vehicule;
	}
    
    public void setVehicule(Vehicule vehicule) {
		this.vehicule = vehicule;
	}
	public Long getIdPlaceParking() {
        return idPlaceParking;
    }

    public void setIdPlaceParking(Long idPlaceParking) {
        this.idPlaceParking = idPlaceParking;
    }
	public Boolean getEstOccupee() {
		return estOccupee;
	}

	public void setEstOccupee(Boolean estOccupee) {
		this.estOccupee = estOccupee;
	}

	public Integer getNumeroPlace() {
		return numeroPlace;
	}

	public void setNumeroPlace(Integer numeroPlace) {
		this.numeroPlace = numeroPlace;
	}

	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

}