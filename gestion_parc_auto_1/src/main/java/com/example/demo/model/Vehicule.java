package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "idVehicule")
public class Vehicule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idVehicule;

    private String immatriculation;
    private String numeroChassis;

    @Temporal(TemporalType.DATE)
    private Date dateCircu;

    @Temporal(TemporalType.DATE)
    private Date date_acquisition;

    private Integer kilometrageActuel;
    private Integer anneeModel;
    private String typeCarburant;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EtatVehicule etat = EtatVehicule.DISPONIBLE;

    private String photoUrl;
    private Double consommationMoyenne; 

    // Relations

    @OneToMany(mappedBy = "vehicule", fetch = FetchType.LAZY)
    private Set<Assurance> assurances = new HashSet<>();


    @OneToMany(mappedBy = "vehicule", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Document> documents;

    @OneToMany(mappedBy = "vehicule", fetch = FetchType.LAZY)
    @JsonIgnore // ⚡ Ignorer sinistres pour éviter boucle
    private Set<Sinistre> sinistres;

    @OneToMany(mappedBy = "vehicule", fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Maintenance> maintenances = new ArrayList<>();

    @OneToMany(mappedBy = "vehicule", cascade = CascadeType.ALL)
    @JsonManagedReference("vehicule-suivi")
    private Set<Suivi> suivis = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "idUtilisateur")
    private Utilisateur utilisateur;

    @OneToMany(mappedBy = "vehicule", fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<BonCarburant> bonsCarburant;

    @OneToMany(mappedBy = "vehicule", fetch = FetchType.LAZY)
    private Set<OrdreMission> missions;

    @ManyToOne
    @JoinColumn(name = "idParking")
    private Parking parking;
    
    @ManyToOne
    @JoinColumn(name = "type_carrosserie_id")
    private TypeCarrosserie typeCarrosserie;
    

    @ManyToOne
    @JoinColumn(name = "modele_id")  // clé étrangère dans la table Vehicule
    private Modele modele;
    // Getters & Setters

    public TypeCarrosserie getTypeCarrosserie() {
		return typeCarrosserie;
	} 
    public void setTypeCarrosserie(TypeCarrosserie typeCarrosserie) {
		this.typeCarrosserie = typeCarrosserie;
	}
    

	public Long getIdVehicule() {
		return idVehicule;
	}

	public void setIdVehicule(Long idVehicule) {
		this.idVehicule = idVehicule;
	}

	public String getImmatriculation() {
		return immatriculation;
	}

	public void setImmatriculation(String immatriculation) {
		this.immatriculation = immatriculation;
	}

	public String getNumeroChassis() {
		return numeroChassis;
	}

	public void setNumeroChassis(String numeroChassis) {
		this.numeroChassis = numeroChassis;
	}

	public Date getDateCircu() {
		return dateCircu;
	}

	public void setDateCircu(Date dateCircu) {
		this.dateCircu = dateCircu;
	}

	public Date getDate_acquisition() {
		return date_acquisition;
	}

	public void setDate_acquisition(Date date_acquisition) {
		this.date_acquisition = date_acquisition;
	}

	public Integer getKilometrageActuel() { return kilometrageActuel; }
	public void setKilometrageActuel(Integer kilometrageActuel) { this.kilometrageActuel = kilometrageActuel; }

	public Integer getAnneeModel() {
		return anneeModel;
	}

	public void setAnneeModel(Integer anneeModel) {
		this.anneeModel = anneeModel;
	}

	public String getTypeCarburant() {
		return typeCarburant;
	}

	public void setTypeCarburant(String typeCarburant) {
		this.typeCarburant = typeCarburant;
	}

	public EtatVehicule getEtat() {
		return etat;
	}

	public void setEtat(EtatVehicule etat) {
		this.etat = etat;
	}

	public String getPhotoUrl() {
		return photoUrl;
	}

	public void setPhotoUrl(String photoUrl) {
		this.photoUrl = photoUrl;
	}

	public Set<Assurance> getAssurances() {
		return assurances;
	}

	public void setAssurances(Set<Assurance> assurances) {
		this.assurances = assurances;
	}

	public List<Maintenance> getMaintenances() {
		return maintenances;
	}
	public void setMaintenances(List<Maintenance> maintenances) {
		this.maintenances = maintenances;
	}

	public Set<Document> getDocuments() {
		return documents;
	}

	public void setDocuments(Set<Document> documents) {
		this.documents = documents;
	}

	public Set<Sinistre> getSinistres() {
		return sinistres;
	}

	public void setSinistres(Set<Sinistre> sinistres) {
		this.sinistres = sinistres;
	}

	public Set<Suivi> getSuivis() {
		return suivis;
	}

	public void setSuivis(Set<Suivi> suivis) {
		this.suivis = suivis;
	}

	public Utilisateur getUtilisateur() {
		return utilisateur;
	}

	public void setUtilisateur(Utilisateur utilisateur) {
		this.utilisateur = utilisateur;
	}

	

	public Set<BonCarburant> getBonsCarburant() {
		return bonsCarburant;
	}
	public void setBonsCarburant(Set<BonCarburant> bonsCarburant) {
		this.bonsCarburant = bonsCarburant;
	}
	public Set<OrdreMission> getMissions() {
		return missions;
	}

	public void setMissions(Set<OrdreMission> missions) {
		this.missions = missions;
	}

	public Parking getParking() {
		return parking;
	}

	public void setParking(Parking parking) {
		this.parking = parking;
	}
public Modele getModele() {
	return modele;
}
 
 public void setModele(Modele modele) {
	this.modele = modele;
}   
 public Vehicule() {}
 public Vehicule(Long idVehicule) {
     this.idVehicule = idVehicule;
 }
public Double getConsommationMoyenne() {
	return consommationMoyenne;
}
public void setConsommationMoyenne(Double consommationMoyenne) {
	this.consommationMoyenne = consommationMoyenne;
}
 
}
