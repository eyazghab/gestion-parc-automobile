package com.example.demo.Dto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.example.demo.model.LigneMaintenance;
import com.example.demo.model.Maintenance;
import com.example.demo.model.TypeIncident;
import com.example.demo.model.TypeIntervention;

public class MaintenanceDTO {

    private Long idMaintenance;
    private String numeroMaintenance;
    private String observations;
    private String typeMaintenance;
    private String statut;
    private Double coutPiece;
    private Double coutExterne;
    private Double coutTotal;
    private LocalDate dateMaintenance;
    private LocalDate dateFinMaintenance;
    private LocalDate dateEffectuee;
    // ⚡ Ajout pour upload fichiers
    private MultipartFile[] files;
    private List<String> typeInterventionLibre;
    private Long sinistreId;
    private Long suiviId;
    private String autreDescription;

    //  Nouveaux champs pour planification
    private LocalDate dateDepartPlanifiee;
    private LocalDate dateFinPlanifiee;
    
    // Véhicule
    private Long vehiculeId;
    private String vehiculeImmatriculation;

    // Technicien
    private Long technicienId;
    private String technicienNom;

    // Lignes
    private List<LigneMaintenance> lignesMaintenance;
    
    private Long typeMaintenanceId;
    private String typeMaintenanceLibelle;
    private TypeIncident typeIncident; // <-- Enum
    private List<TypeIntervention> typeIntervention;


    public MaintenanceDTO(Long idMaintenance, String numeroMaintenance, String observations,
                      String typeMaintenance, String statut, Double coutPiece, Double coutExterne,
                      Double coutTotal, LocalDate dateMaintenance, LocalDate dateEffectuee,
                      Long vehiculeId, String vehiculeImmatriculation,
                      Long technicienId, String technicienNom,
                      List<LigneMaintenance> lignesMaintenance,
                      Long typeMaintenanceId, String typeMaintenanceLibelle,Long suiviId,TypeIncident typeIncident,List<TypeIntervention> typeIntervention) {
    this.idMaintenance = idMaintenance;
    this.numeroMaintenance = numeroMaintenance;
    this.observations = observations;
    this.typeMaintenance = typeMaintenance;
    this.statut = statut;
    this.coutPiece = coutPiece;
    this.coutExterne = coutExterne;
    this.coutTotal = coutTotal;
    this.dateMaintenance = dateMaintenance;
    this.dateEffectuee = dateEffectuee;
    this.vehiculeId = vehiculeId;
    this.vehiculeImmatriculation = vehiculeImmatriculation;
    this.technicienId = technicienId;
    this.technicienNom = technicienNom;
    this.lignesMaintenance = lignesMaintenance;
    this.typeMaintenanceId = typeMaintenanceId;
    this.typeMaintenanceLibelle = typeMaintenanceLibelle;
    this.suiviId = suiviId;
    this.typeIncident=typeIncident;
    this.typeIntervention=typeIntervention;
}


    // Getters et setters
    public Long getIdMaintenance() { return idMaintenance; }
    public void setIdMaintenance(Long idMaintenance) { this.idMaintenance = idMaintenance; }

    public String getVehiculeImmatriculation() { return vehiculeImmatriculation; }
    public void setVehiculeImmatriculation(String vehiculeImmatriculation) { this.vehiculeImmatriculation = vehiculeImmatriculation; }

    public String getTypeMaintenance() { return typeMaintenance; }
    public void setTypeMaintenance(String typeMaintenance) { this.typeMaintenance = typeMaintenance; }

    public String getTechnicienNom() { return technicienNom; }
    public void setTechnicienNom(String technicienNom) { this.technicienNom = technicienNom; }


    public double getCoutTotal() { return coutTotal; }
    public void setCoutTotal(double coutTotal) { this.coutTotal = coutTotal; }
    public String getDescription() {
		return observations;
	}
    public String getNumeroMaintenance() {
		return numeroMaintenance;
	}
    public void setDescription(String description) {
		this.observations = description;
	}
    public void setNumeroMaintenance(String numeroMaintenance) {
		this.numeroMaintenance = numeroMaintenance;
	}
    public String getStatut() { return statut; }
public void setStatut(String statut) {
	this.statut = statut;
}

public String getObservations() {
	return observations;
}

public void setObservations(String observations) {
	this.observations = observations;
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

public List<LigneMaintenance> getLignesMaintenance() {
	return lignesMaintenance;
}

public void setLignesMaintenance(List<LigneMaintenance> lignesMaintenance) {
	this.lignesMaintenance = lignesMaintenance;
}

public void setCoutTotal(Double coutTotal) {
	this.coutTotal = coutTotal;
}



public Long getVehiculeId() {
	return vehiculeId;
}



public void setVehiculeId(Long vehiculeId) {
	this.vehiculeId = vehiculeId;
}



public Long getTechnicienId() {
	return technicienId;
}



public void setTechnicienId(Long technicienId) {
	this.technicienId = technicienId;
}


public Long getTypeMaintenanceId() {
	return typeMaintenanceId;
}


public void setTypeMaintenanceId(Long typeMaintenanceId) {
	this.typeMaintenanceId = typeMaintenanceId;
}


public String getTypeMaintenanceLibelle() {
	return typeMaintenanceLibelle;
}


public void setTypeMaintenanceLibelle(String typeMaintenanceLibelle) {
	this.typeMaintenanceLibelle = typeMaintenanceLibelle;
}
public MultipartFile[] getFiles() { return files; }
public void setFiles(MultipartFile[] files) { this.files = files; }


public Long getSinistreId() {
	return sinistreId;
}


public void setSinistreId(Long sinistreId) {
	this.sinistreId = sinistreId;
}


public Long getSuiviId() {
	return suiviId;
}


public void setSuiviId(Long suiviId) {
	this.suiviId = suiviId;
}


public TypeIncident getTypeIncident() {
	return typeIncident;
}


public void setTypeIncident(TypeIncident typeIncident) {
	this.typeIncident = typeIncident;
}


public List<TypeIntervention> getTypeIntervention() {
	return typeIntervention;
}


public void setTypeIntervention(List<TypeIntervention> typeIntervention) {
	this.typeIntervention = typeIntervention;
}





public MaintenanceDTO() {
	super();
}



// Dans MaintenanceDTO.java
public MaintenanceDTO(Maintenance maintenance) {
    this.idMaintenance = maintenance.getIdMaintenance();
    this.numeroMaintenance = maintenance.getNumeroMaintenance();
    this.observations = maintenance.getObservations();
    this.typeMaintenance = maintenance.getTypeMaintenance() != null ? maintenance.getTypeMaintenance().getLibelle() : null;
    this.statut = maintenance.getStatut();
    this.coutPiece = maintenance.getCoutPiece();
    this.coutExterne = maintenance.getCoutExterne();
    this.coutTotal = maintenance.getCoutTotal();
    this.dateMaintenance = maintenance.getDateMaintenance();
    this.dateEffectuee = maintenance.getDateEffectuee();
    this.dateFinMaintenance = maintenance.getDateFinMaintenance();
    this.dateDepartPlanifiee = maintenance.getDateDepartPlanifiee();
    this.dateFinPlanifiee = maintenance.getDateFinPlanifiee();
    this.dateEffectuee = maintenance.getDateEffectuee();
    this.typeIncident = maintenance.getTypeIncident();
 // Si getTypeIntervention() retourne déjà une liste
    this.typeIntervention = maintenance.getTypeIntervention() != null
        ? new ArrayList<>(maintenance.getTypeIntervention()) // copie de la liste pour sécurité
        : new ArrayList<>(); // plutôt que null pour éviter NullPointerException


    // 🔗 Véhicule
    if (maintenance.getVehicule() != null) {
        this.vehiculeId = maintenance.getVehicule().getIdVehicule();
        this.vehiculeImmatriculation = maintenance.getVehicule().getImmatriculation();
    }

    // 🔗 Technicien
    if (maintenance.getTechnicien() != null) {
        this.technicienId = maintenance.getTechnicien().getIdTechnicien();
        this.technicienNom = maintenance.getTechnicien().getNom();
    }

    // 🔗 Type maintenance
    if (maintenance.getTypeMaintenance() != null) {
        this.typeMaintenanceId = maintenance.getTypeMaintenance().getId();
        this.typeMaintenanceLibelle = maintenance.getTypeMaintenance().getLibelle();
    }

    // 🔗 Sinistre & Suivi
    this.sinistreId = maintenance.getSinistre() != null ? maintenance.getSinistre().getId() : null;
    this.suiviId = maintenance.getSuivi() != null ? maintenance.getSuivi().getId() : null;

    // Lignes
    this.lignesMaintenance = maintenance.getLignes();
}
public LocalDate getDateDepartPlanifiee() { return dateDepartPlanifiee; }
public void setDateDepartPlanifiee(LocalDate dateDepartPlanifiee) { this.dateDepartPlanifiee = dateDepartPlanifiee; }

public LocalDate getDateFinPlanifiee() { return dateFinPlanifiee; }
public void setDateFinPlanifiee(LocalDate dateFinPlanifiee) { this.dateFinPlanifiee = dateFinPlanifiee; }


public LocalDate getDateFinMaintenance() {
	return dateFinMaintenance;
}


public void setDateFinMaintenance(LocalDate dateFinMaintenance) {
	this.dateFinMaintenance = dateFinMaintenance;
}


public List<String> getTypeInterventionLibre() {
	return typeInterventionLibre;
}


public void setTypeInterventionLibre(List<String> typeInterventionLibre) {
	this.typeInterventionLibre = typeInterventionLibre;
}


public String getAutreDescription() {
	return autreDescription;
}


public void setAutreDescription(String autreDescription) {
	this.autreDescription = autreDescription;
}

}
