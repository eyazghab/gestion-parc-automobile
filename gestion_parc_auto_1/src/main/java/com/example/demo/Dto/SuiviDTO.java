package com.example.demo.Dto;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import com.example.demo.model.EtatVehicule;
import com.example.demo.model.ProchaineEcheance;
import com.fasterxml.jackson.annotation.JsonFormat;

public class SuiviDTO {

    private Long idVehicule;
    private String immatriculation;
    private EtatVehicule etatVehicule;
    private Integer kmActuel;
    private List<ProchaineEcheance> echeances;
    private Date dateDerniersuivi;
    private Date dateKm;
    private Date dateTemp;
    private List<String> alertes;
    private boolean alerteMaintenance;
    private Date dateCircu;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate prochainSuivi;
    private Double consommationMoyenne;


    // Nouveaux champs
    private String description;
    private Integer frequence;
    private Integer dureeEstimé;
    private Integer nombreAccidents;
    private Integer nombrePannes;
    private Integer kilometresDepuisFreins;
    private Integer kilometresDepuisVidange;
    private Integer dureeVieBatterie;

    public SuiviDTO(Long idVehicule, String immatriculation, Integer kmActuel,
                    List<ProchaineEcheance> echeances, Date dateDerniersuivi, Date dateKm, Date dateTemp,
                    List<String> alertes, boolean alerteMaintenance,
                    String description, Integer frequence, Integer dureeEstimé,
                    Integer nombreAccidents, Integer nombrePannes,
                    Integer kilometresDepuisFreins, Integer kilometresDepuisVidange,
                    Integer dureeVieBatterie,Double consommationMoyenne) {
        this.idVehicule = idVehicule;
        this.immatriculation = immatriculation;
        this.kmActuel = kmActuel;
        this.echeances = echeances;
        this.dateDerniersuivi = dateDerniersuivi;
        this.dateKm = dateKm;
        this.dateTemp = dateTemp;
        this.alertes = alertes;
        this.alerteMaintenance = alerteMaintenance;
        this.description = description;
        this.frequence = frequence;
        this.dureeEstimé = dureeEstimé;
        this.nombreAccidents = nombreAccidents;
        this.nombrePannes = nombrePannes;
        this.kilometresDepuisFreins = kilometresDepuisFreins;
        this.kilometresDepuisVidange = kilometresDepuisVidange;
        this.dureeVieBatterie = dureeVieBatterie;
        this.consommationMoyenne=consommationMoyenne;
    }

    // Getters & Setters pour tous les champs (existants et nouveaux)
    // ... exemple :
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getFrequence() { return frequence; }
    public void setFrequence(Integer frequence) { this.frequence = frequence; }

    public Integer getDureeEstimé() { return dureeEstimé; }
    public void setDureeEstimé(Integer dureeEstimé) { this.dureeEstimé = dureeEstimé; }

    public Integer getNombreAccidents() { return nombreAccidents; }
    public void setNombreAccidents(Integer nombreAccidents) { this.nombreAccidents = nombreAccidents; }

    public Integer getNombrePannes() { return nombrePannes; }
    public void setNombrePannes(Integer nombrePannes) { this.nombrePannes = nombrePannes; }

    public Integer getKilometresDepuisFreins() { return kilometresDepuisFreins; }
    public void setKilometresDepuisFreins(Integer kilometresDepuisFreins) { this.kilometresDepuisFreins = kilometresDepuisFreins; }

    public Integer getKilometresDepuisVidange() { return kilometresDepuisVidange; }
    public void setKilometresDepuisVidange(Integer kilometresDepuisVidange) { this.kilometresDepuisVidange = kilometresDepuisVidange; }

    public Integer getDureeVieBatterie() { return dureeVieBatterie; }
    public void setDureeVieBatterie(Integer dureeVieBatterie) { this.dureeVieBatterie = dureeVieBatterie; }

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

	public Integer getKmActuel() {
		return kmActuel;
	}

	public void setKmActuel(Integer kmActuel) {
		this.kmActuel = kmActuel;
	}

	public List<ProchaineEcheance> getEcheances() {
		return echeances;
	}

	public void setEcheances(List<ProchaineEcheance> echeances) {
		this.echeances = echeances;
	}

	public Date getDateDerniersuivi() {
		return dateDerniersuivi;
	}

	public void setDateDerniersuivi(Date dateDerniersuivi) {
		this.dateDerniersuivi = dateDerniersuivi;
	}

	public Date getDateKm() {
		return dateKm;
	}

	public void setDateKm(Date dateKm) {
		this.dateKm = dateKm;
	}

	public Date getDateTemp() {
		return dateTemp;
	}

	public void setDateTemp(Date dateTemp) {
		this.dateTemp = dateTemp;
	}

	public List<String> getAlertes() {
		return alertes;
	}

	public void setAlertes(List<String> alertes) {
		this.alertes = alertes;
	}

	public boolean isAlerteMaintenance() {
		return alerteMaintenance;
	}

	public void setAlerteMaintenance(boolean alerteMaintenance) {
		this.alerteMaintenance = alerteMaintenance;
	}

	public EtatVehicule getEtatVehicule() {
		return etatVehicule;
	}

	public void setEtatVehicule(EtatVehicule etatVehicule) {
		this.etatVehicule = etatVehicule;
	}

	public Date getDateCircu() {
		return dateCircu;
	}

	public void setDateCircu(Date dateCircu) {
		this.dateCircu = dateCircu;
	}

	public LocalDate getProchainSuivi() {
		return prochainSuivi;
	}

	public void setProchainSuivi(LocalDate prochainSuivi) {
		this.prochainSuivi = prochainSuivi;
	}

	public Double getConsommationMoyenne() {
		return consommationMoyenne;
	}

	public void setConsommationMoyenne(Double consommationMoyenne) {
		this.consommationMoyenne = consommationMoyenne;
	}

	

    
}
