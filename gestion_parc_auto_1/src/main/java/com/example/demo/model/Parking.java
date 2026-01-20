package com.example.demo.model;


import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.*;

@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})

public class Parking {
    
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idParking;
    private String nomParking;  // Nom du parking
    private String adresse;  // Adresse du parking
    private Integer capacite;  // Capacité maximale du parking
    private Boolean estDisponible;  // Statut du parking (disponible ou non)

    //Les Relations 
    @OneToMany(mappedBy = "parking", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Vehicule> vehicules;  // Liste des véhicules stationnés dans ce parking

    @ManyToOne
    @JoinColumn(name = "idUtilisateur")  // Clé étrangère vers l'entité Employé
    private Utilisateur utilisateur; public void setUtilisateur(Utilisateur utilisateur) {
		this.utilisateur = utilisateur;
	} // Employé assigné à ce parking
    
    @OneToMany(mappedBy = "parking", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Place> placesParking;  // Liste des places de parking dans ce parking

 // Getters & Setters
    public Long getId() {
        return idParking;
    }

    public void setId(Long id) {
        this.idParking = id;
    }
	public Boolean getEstDisponible() {
		return estDisponible;
	}

	public void setEstDisponible(Boolean estDisponible) {
		this.estDisponible = estDisponible;
	}

	public Integer getCapacite() {
		return capacite;
	}

	public void setCapacite(Integer capacite) {
		this.capacite = capacite;
	}

	public String getAdresse() {
		return adresse;
	}

	public void setAdresse(String adresse) {
		this.adresse = adresse;
	}

	public String getNomParking() {
		return nomParking;
	}

	public void setNomParking(String nomParking) {
		this.nomParking = nomParking;
	}
}