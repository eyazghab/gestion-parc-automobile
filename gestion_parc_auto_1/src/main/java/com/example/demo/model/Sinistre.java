package com.example.demo.model;

import java.sql.Time;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
@Entity
public class Sinistre {
	 @Id@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	 @Temporal(TemporalType.DATE)
    private Date dateSinistre;
    private String description;
    @DateTimeFormat(pattern = "HH:mm:ss")
    private LocalTime heureSinistre; 
    private String lieuSinistre;
    private String degats; 
    private String numDeclaration;
    @Temporal(TemporalType.DATE)
    private Date dateDeclaration;
    @Enumerated(EnumType.STRING) // ⚡ Stocke l'enum sous forme de texte
    private TypeIncident typeIncident;
 // Liste des noms ou chemins de fichiers images
    @ElementCollection
    private List<String> photos = new ArrayList<>();
    
    @Enumerated(EnumType.STRING)
    private EtatSinistre etat = EtatSinistre.DECLARE;
    
    @Enumerated(EnumType.STRING)
    private Circulation circulation;
    
    // Les Relations
    @ManyToOne
    @JoinColumn(name = "vehicule_id", nullable = false)
    private Vehicule vehicule;

    @ManyToOne
    @JoinColumn(name = "utilisateur_id", nullable = true)
    private Utilisateur utilisateur;
    
    // Getters & Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	

	public LocalTime getHeureSinistre() {
		return heureSinistre;
	}

	public void setHeureSinistre(LocalTime heureSinistre) {
		this.heureSinistre = heureSinistre;
	}

	public String getLieuSinistre() {
		return lieuSinistre;
	}

	public void setLieuSinistre(String lieuSinistre) {
		this.lieuSinistre = lieuSinistre;
	}
	public Date getDateDeclaration() {
		return dateDeclaration;
	}
	public String getNumDeclaration() {
		return numDeclaration;
	}
	public void setNumDeclaration(String numDeclaration) {
		this.numDeclaration = numDeclaration;
	}

	public String getDegats() {
		return degats;
	}

	public void setDegats(String degats) {
		this.degats = degats;
	}
	public void setDateDeclaration(Date dateDeclaration) {
		this.dateDeclaration = dateDeclaration;
	}
	public void setVehicule(Vehicule vehicule) {
		this.vehicule = vehicule;
	}
	public void setDateSinistre(Date dateSinistre) {
		this.dateSinistre = dateSinistre;
	}
	public Vehicule getVehicule() {
		return vehicule;
	}
	public Date getDateSinistre() {
		return dateSinistre;
	}

	public List<String> getPhotos() {
		return photos;
	}

	public void setPhotos(List<String> photos) {
		this.photos = photos;
	}

	public Utilisateur getUtilisateur() {
		return utilisateur;
	}

	public void setUtilisateur(Utilisateur utilisateur) {
		this.utilisateur = utilisateur;
	}

	public EtatSinistre getEtat() {
		return etat;
	}

	public void setEtat(EtatSinistre etat) {
		this.etat = etat;
	}

	public Circulation getCirculation() {
		return circulation;
	}

	public void setCirculation(Circulation circulation) {
		this.circulation = circulation;
	}

	public TypeIncident getTypeIncident() {
		return typeIncident;
	}

	public void setTypeIncident(TypeIncident typeIncident) {
		this.typeIncident = typeIncident;
	}
	
}