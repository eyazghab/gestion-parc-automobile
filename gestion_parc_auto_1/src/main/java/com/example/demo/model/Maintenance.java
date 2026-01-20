package com.example.demo.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.*;

@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Maintenance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idMaintenance;
    private String numeroMaintenance;

    private LocalDate dateMaintenance;//Date réelle du début de maintenance
    private LocalDate dateEffectuee;

    // ✅ Nouveaux champs
    private LocalDate dateFinMaintenance;//Date Fin réelle de la maintenance
    private LocalDate dateProchaineMaintenance;
    private LocalDate dateDepartPlanifiee;
    private LocalDate dateFinPlanifiee;

    @Enumerated(EnumType.STRING)
    private TypeIncident typeIncident;   // Accident / Panne / Préventive

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "maintenance_type_intervention", joinColumns = @JoinColumn(name = "maintenance_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "type_intervention")
    private List<TypeIntervention> typeIntervention = new ArrayList<>();

    private Integer kilometrage;
    private String prestataire;
    private Double coutPiece;
    private Double coutExterne;
    private Double coutTotal = 0.0;
    private String numeroFa;
    private String statut; // Initialiser, Planifie, En cours, Termine
    private String observations;

    @Temporal(TemporalType.DATE)
    private Date dateFacture;

    @Temporal(TemporalType.DATE)
    private Date dateDemande;

    @ElementCollection
    @CollectionTable(name = "maintenance_photos", joinColumns = @JoinColumn(name = "maintenance_id"))
    @Column(name = "photo_url")
    private List<String> photos = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "vehicule_id")
    @JsonBackReference
    private Vehicule vehicule;

    @ManyToOne
    @JoinColumn(name = "idTechnicien", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Technicien technicien;

    @OneToOne
    @JoinColumn(name = "sinistre_id")
    @JsonIgnore
    private Sinistre sinistre;

    @OneToMany(mappedBy = "maintenance", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<LigneMaintenance> lignes = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "typeMaintenance_id")
    private TypeMaintenance typeMaintenance;
    
    @ManyToOne
    @JoinColumn(name = "suivi_id")
    @JsonBackReference("suivi-maintenance")
    private Suivi suivi;

    // === Getters & Setters ===
    // ⚡ Ajout des getters/setters pour les nouveaux champs

    public LocalDate getDateFinMaintenance() {
        return dateFinMaintenance;
    }

    public void setDateFinMaintenance(LocalDate dateFinMaintenance) {
        this.dateFinMaintenance = dateFinMaintenance;
    }

    public LocalDate getDateProchaineMaintenance() {
        return dateProchaineMaintenance;
    }

    public void setDateProchaineMaintenance(LocalDate dateProchaineMaintenance) {
        this.dateProchaineMaintenance = dateProchaineMaintenance;
    }

    public TypeIncident getTypeIncident() {
        return typeIncident;
    }

    public void setTypeIncident(TypeIncident typeIncident) {
        this.typeIncident = typeIncident;
    }

	public Long getIdMaintenance() {
		return idMaintenance;
	}

	public void setIdMaintenance(Long idMaintenance) {
		this.idMaintenance = idMaintenance;
	}

	public String getNumeroMaintenance() {
		return numeroMaintenance;
	}

	public void setNumeroMaintenance(String numeroMaintenance) {
		this.numeroMaintenance = numeroMaintenance;
	}

	public LocalDate getDateMaintenance() {
		return dateMaintenance;
	}

	public void setDateMaintenance(LocalDate dateMaintenance) {
		this.dateMaintenance = dateMaintenance;
	}

	public LocalDate getDateEffectuee() {
		return dateEffectuee;
	}

	public void setDateEffectuee(LocalDate dateEffectuee) {
		this.dateEffectuee = dateEffectuee;
	}

	public List<TypeIntervention> getTypeIntervention() {
	    return typeIntervention;
	}

	public void setTypeIntervention(List<TypeIntervention> typeIntervention) {
	    this.typeIntervention = typeIntervention;
	}

	public Integer getKilometrage() {
		return kilometrage;
	}

	public void setKilometrage(Integer kilometrage) {
		this.kilometrage = kilometrage;
	}

	public String getPrestataire() {
		return prestataire;
	}

	public void setPrestataire(String prestataire) {
		this.prestataire = prestataire;
	}

	public Double getCoutPiece() {
		return coutPiece;
	}

	public void setCoutPiece(Double coutPiece) {
		this.coutPiece = coutPiece;
	}

	public Double getCoutExterne() {
		return coutExterne;
	}

	public void setCoutExterne(Double coutExterne) {
		this.coutExterne = coutExterne;
	}

	public Double getCoutTotal() {
		return coutTotal;
	}

	public void setCoutTotal(Double coutTotal) {
		this.coutTotal = coutTotal;
	}

	public String getNumeroFa() {
		return numeroFa;
	}

	public void setNumeroFa(String numeroFa) {
		this.numeroFa = numeroFa;
	}

	public String getStatut() {
		return statut;
	}

	public void setStatut(String statut) {
		this.statut = statut;
	}

	public String getObservations() {
		return observations;
	}

	public void setObservations(String observations) {
		this.observations = observations;
	}

	public Date getDateFacture() {
		return dateFacture;
	}

	public void setDateFacture(Date dateFacture) {
		this.dateFacture = dateFacture;
	}

	public Date getDateDemande() {
		return dateDemande;
	}

	public void setDateDemande(Date dateDemande) {
		this.dateDemande = dateDemande;
	}

	public List<String> getPhotos() {
		return photos;
	}

	public void setPhotos(List<String> photos) {
		this.photos = photos;
	}

	public Vehicule getVehicule() {
		return vehicule;
	}

	public void setVehicule(Vehicule vehicule) {
		this.vehicule = vehicule;
	}

	public Technicien getTechnicien() {
		return technicien;
	}

	public void setTechnicien(Technicien technicien) {
		this.technicien = technicien;
	}

	public Sinistre getSinistre() {
		return sinistre;
	}

	public void setSinistre(Sinistre sinistre) {
		this.sinistre = sinistre;
	}

	public List<LigneMaintenance> getLignes() {
		return lignes;
	}

	public void setLignes(List<LigneMaintenance> lignes) {
		this.lignes = lignes;
	}

	public TypeMaintenance getTypeMaintenance() {
		return typeMaintenance;
	}

	public void setTypeMaintenance(TypeMaintenance typeMaintenance) {
		this.typeMaintenance = typeMaintenance;
	}

	public Suivi getSuivi() {
		return suivi;
	}

	public void setSuivi(Suivi suivi) {
		this.suivi = suivi;
	}

	public LocalDate getDateDepartPlanifiee() {
		return dateDepartPlanifiee;
	}

	public void setDateDepartPlanifiee(LocalDate dateDepartPlanifiee) {
		this.dateDepartPlanifiee = dateDepartPlanifiee;
	}

	public LocalDate getDateFinPlanifiee() {
		return dateFinPlanifiee;
	}

	public void setDateFinPlanifiee(LocalDate dateFinPlanifiee) {
		this.dateFinPlanifiee = dateFinPlanifiee;
	}

  
}
