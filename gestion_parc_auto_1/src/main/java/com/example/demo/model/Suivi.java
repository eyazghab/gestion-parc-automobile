package com.example.demo.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Transient;

@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Suivi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String description;
    private LocalDate dateDerniersuivi;
    private int kilometreActuel;
    private LocalDate dateSuivi;
    private int frequence;
    private LocalDate dateTemp;
    private LocalDate dateKm;
    private int dureeEstimé;

    @Transient
    private List<ProchaineEcheance> echeances;

    private boolean alerteMaintenance;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> alertes = new ArrayList<>();

    // ===================== RELATIONS EXISTANTES =====================
    @ManyToOne
    @JoinColumn(name = "ordre_mission_id")
    @JsonBackReference("ordreMission-suivi")
    private OrdreMission ordreMission;

    @ManyToOne
    @JoinColumn(name = "vehicule_id")
    @JsonBackReference("vehicule-suivi")
    private Vehicule vehicule;

    // ===================== RELATION AVEC MAINTENANCE =====================
    @OneToMany(mappedBy = "suivi", fetch = FetchType.LAZY)
    @JsonManagedReference("suivi-maintenance")
    private List<Maintenance> maintenances = new ArrayList<>();

    // ===================== CHAMPS MÉTRIQUES =====================
    private int nombreAccidents = 0;
    private int nombrePannes = 0;
    private int kilometresDepuisFreins = 0;
    private int kilometresDepuisVidange = 0;
    private int dureeVieBatterie = 0; // en mois ou années

    // === Getters & Setters ===
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDate getDateDerniersuivi() { return dateDerniersuivi; }
    public void setDateDerniersuivi(LocalDate dateDerniersuivi) { this.dateDerniersuivi = dateDerniersuivi; }

    public int getKilometreActuel() { return kilometreActuel; }
    public void setKilometreActuel(int kilometreActuel) { this.kilometreActuel = kilometreActuel; }

    public LocalDate getDateSuivi() { return dateSuivi; }
    public void setDateSuivi(LocalDate dateSuivi) { this.dateSuivi = dateSuivi; }

    public int getFrequence() { return frequence; }
    public void setFrequence(int frequence) { this.frequence = frequence; }

    public LocalDate getDateTemp() { return dateTemp; }
    public void setDateTemp(LocalDate dateTemp) { this.dateTemp = dateTemp; }

    public LocalDate getDateKm() { return dateKm; }
    public void setDateKm(LocalDate dateKm) { this.dateKm = dateKm; }

    public int getDureeEstimé() { return dureeEstimé; }
    public void setDureeEstimé(int dureeEstimé) { this.dureeEstimé = dureeEstimé; }

    public List<ProchaineEcheance> getEcheances() { return echeances; }
    public void setEcheances(List<ProchaineEcheance> echeances) { this.echeances = echeances; }

    public boolean isAlerteMaintenance() { return alerteMaintenance; }
    public void setAlerteMaintenance(boolean alerteMaintenance) { this.alerteMaintenance = alerteMaintenance; }

    public List<String> getAlertes() { return alertes; }
    public void setAlertes(List<String> alertes) { this.alertes = alertes; }

    public Vehicule getVehicule() { return vehicule; }
    public void setVehicule(Vehicule vehicule) { this.vehicule = vehicule; }

    public int getNombreAccidents() { return nombreAccidents; }
    public void setNombreAccidents(int nombreAccidents) { this.nombreAccidents = nombreAccidents; }

    public int getNombrePannes() { return nombrePannes; }
    public void setNombrePannes(int nombrePannes) { this.nombrePannes = nombrePannes; }

    public int getKilometresDepuisFreins() { return kilometresDepuisFreins; }
    public void setKilometresDepuisFreins(int kilometresDepuisFreins) { this.kilometresDepuisFreins = kilometresDepuisFreins; }

    public int getKilometresDepuisVidange() { return kilometresDepuisVidange; }
    public void setKilometresDepuisVidange(int kilometresDepuisVidange) { this.kilometresDepuisVidange = kilometresDepuisVidange; }

    public int getDureeVieBatterie() { return dureeVieBatterie; }
    public void setDureeVieBatterie(int dureeVieBatterie) { this.dureeVieBatterie = dureeVieBatterie; }

    public OrdreMission getOrdreMission() {return ordreMission;}
    public void setOrdreMission(OrdreMission ordreMission) {this.ordreMission = ordreMission;}

    public List<Maintenance> getMaintenances() { return maintenances; }
    public void setMaintenances(List<Maintenance> maintenances) { this.maintenances = maintenances; }

    public Suivi() {}
}
