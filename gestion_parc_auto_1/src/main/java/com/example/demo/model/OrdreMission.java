package com.example.demo.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class OrdreMission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String objetMission;
    private String destination;
    private String motif;
    private Double distanceEstimee= 0.0;
    private Double carburantNecessaire= 0.0;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime dateDepart;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime dateRetour;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime dateDepartReelle;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime dateRetourReelle;

    private LocalDateTime dateOrdre;
    private boolean validee;

    @Enumerated(EnumType.STRING)
    private EtatMission etat = EtatMission.EN_ATTENTE;

    // ✅ Ajout du champ typesIncident
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "ordre_mission_incidents", joinColumns = @JoinColumn(name = "ordre_mission_id"))
    @Column(name = "type_incident")
    private Set<String> typesIncident;
    
    @ManyToOne
    @JoinColumn(name = "utilisateur_id")
    private Utilisateur utilisateur;

    @ManyToOne
    @JoinColumn(name = "vehicule_id")
    private Vehicule vehicule;

    @OneToMany(mappedBy = "ordreMission")
    @JsonManagedReference("ordreMission-suivi")
    private Set<Suivi> suivis;

  
	public String getObjetMission() {
		return objetMission;
	}

	public void setObjetMission(String objetMission) {
		this.objetMission = objetMission;
	}

	

	public LocalDateTime getDateDepart() {
		return dateDepart;
	}
	public LocalDateTime getDateRetour() {
		return dateRetour;
	}
	public void setDateDepart(LocalDateTime dateDepart) {
		this.dateDepart = dateDepart;
	}
	public void setDateRetour(LocalDateTime dateRetour) {
		this.dateRetour = dateRetour;
	}

	public boolean isValidee() {
		return validee;
	}

	public void setValidee(boolean validee) {
		this.validee = validee;
	}

	public LocalDateTime getDateOrdre() {
		return dateOrdre;
	}

	public void setDateOrdre(LocalDateTime dateOrdre) {
		this.dateOrdre = dateOrdre;
	}

   public void setUtilisateur(Utilisateur utilisateur) {
	this.utilisateur = utilisateur;
}
   public void setVehicule(Vehicule vehicule) {
	this.vehicule = vehicule;
}
   public Utilisateur getUtilisateur() {
	return utilisateur;
}
   public Vehicule getVehicule() {
	return vehicule;
}
   public Long getId() {
	return id;
}
   public String getDestination() {
	return destination;
}public void setDestination(String destination) {
	this.destination = destination;
}
public EtatMission getEtat() {
	return etat;
}
public String getMotif() {
	return motif;
}
public void setMotif(String motif) {
	this.motif = motif;
} 
public void setEtat(EtatMission etat) {
	this.etat = etat;
}

public LocalDateTime getDateDepartReelle() {
	return dateDepartReelle;
}

public void setDateDepartReelle(LocalDateTime dateDepartReelle) {
	this.dateDepartReelle = dateDepartReelle;
}

public LocalDateTime getDateRetourReelle() {
	return dateRetourReelle;
}

public void setDateRetourReelle(LocalDateTime dateRetourReelle) {
	this.dateRetourReelle = dateRetourReelle;
}

public void setId(Long id) {
	this.id = id;
}

public Double getDistanceEstimee() {
	return distanceEstimee;
}

public void setDistanceEstimee(Double distanceEstimee) {
	this.distanceEstimee = distanceEstimee;
}

public Double getCarburantNecessaire() {
	return carburantNecessaire;
}

public void setCarburantNecessaire(Double carburantNecessaire) {
	this.carburantNecessaire = carburantNecessaire;
}

public Set<String> getTypesIncident() {
	return typesIncident;
}

public void setTypesIncident(Set<String> typesIncident) {
	this.typesIncident = typesIncident;
}

public Set<Suivi> getSuivis() {
	return suivis;
}

public void setSuivis(Set<Suivi> suivis) {
	this.suivis = suivis;
}
   
}
