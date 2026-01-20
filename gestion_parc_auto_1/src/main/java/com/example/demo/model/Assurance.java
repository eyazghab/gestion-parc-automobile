package com.example.demo.model;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;

@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})

public class Assurance {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nomAssurance;  // Nom de la compagnie d'assurance
    private String compagnie;  
    private String typeAssurance;  // Type d'assurance, par exemple "Tous risques", "Responsabilité civile", etc.
    private Double montantPrime;  // Montant de la prime d'assurance
    private String telephone;  
    private String email;  
    private String commantaire;  
    @Temporal(TemporalType.DATE)
    private Date dateDebut;public void setDateDebut(Date dateDebut) {
		this.dateDebut = dateDebut;
	}  // Date de début de l'assurance
    @Temporal(TemporalType.DATE)
    private Date dateFin;public void setDateFin(Date dateFin) {
		this.dateFin = dateFin;
	}  // Date de fin de l'assurance
    private String numeroPolice;  // Numéro de police d'assurance

    // Les Relations : 
    @ManyToOne
    @JoinColumn(name = "idVehicule")
    private Vehicule vehicule;  // Relation Many-to-One avec Véhicule
    

    
 // Getters & Setters

    public Long getIdAssurance() {
        return id;
    }

    public void setIdAssurance(Long id) {
        this.id = id;
    }
	public String getNomAssurance() {
		return nomAssurance;
	}

	public void setNomAssurance(String nomAssurance) {
		this.nomAssurance = nomAssurance;
	}

	public String getTypeAssurance() {
		return typeAssurance;
	}

	public void setTypeAssurance(String typeAssurance) {
		this.typeAssurance = typeAssurance;
	}

	public Double getMontantPrime() {
		return montantPrime;
	}

	public void setMontantPrime(Double montantPrime) {
		this.montantPrime = montantPrime;
	}

	public String getNumeroPolice() {
		return numeroPolice;
	}

	public void setNumeroPolice(String numeroPolice) {
		this.numeroPolice = numeroPolice;
	}

	public String getCompagnie() {
		return compagnie;
	}

	public void setCompagnie(String compagnie) {
		this.compagnie = compagnie;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getCommantaire() {
		return commantaire;
	}

	public void setCommantaire(String commantaire) {
		this.commantaire = commantaire;
	}
	public Vehicule getVehicule() {
		return vehicule;
	}
	public void setVehicule(Vehicule vehicule) {
		this.vehicule = vehicule;
	}
	public Date getDateDebut() {
		return dateDebut;
	}
	public Date getDateFin() {
		return dateFin;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
}