package com.example.demo.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})

@Entity
public class Departement {
	 @Id@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nom;
    private String responsable;

    //Les Relations 
    @ManyToOne
    private Entreprise entreprise;
	

    @OneToMany(mappedBy = "departement")
    @JsonIgnore
    private List<Utilisateur> utilisateurs;

 // Getters & Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	public String getResponsable() {
		return responsable;
	}

	public void setResponsable(String responsable) {
		this.responsable = responsable;
	}
 public void setEntreprise(Entreprise entreprise) {
	this.entreprise = entreprise;
}
 public List<Utilisateur> getUtilisateurs() {
	return utilisateurs;
}

public Departement(Long id, String nom) {
	super();
	this.id = id;
	this.nom = nom;
	
}
public Departement() {
	// TODO Auto-generated constructor stub
}

public Entreprise getEntreprise() {
	return entreprise;
}

}
