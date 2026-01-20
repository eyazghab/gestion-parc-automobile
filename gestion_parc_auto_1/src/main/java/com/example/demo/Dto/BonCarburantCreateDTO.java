package com.example.demo.Dto;

import java.util.Date;
import com.example.demo.model.BonCarburant.EtatBon;

public class BonCarburantCreateDTO {

    private Double montant;
    private Double quantite;
    private Date dateAchat;
    private Long vehiculeId;
    private Long carburantId;
    private Long utilisateurId;
    private EtatBon etat;
    private Long missionId;
    public BonCarburantCreateDTO() {}

    public BonCarburantCreateDTO(Double montant, Double quantite, Date dateAchat,
                                 Long vehiculeId, Long carburantId, Long utilisateurId,
                                 EtatBon etat,Long missionId) {
        this.montant = montant;
        this.quantite = quantite;
        this.dateAchat = dateAchat;
        this.vehiculeId = vehiculeId;
        this.carburantId = carburantId;
        this.utilisateurId = utilisateurId;
        this.etat = etat;
        this.missionId=missionId;
    }

    // Getters & Setters
    public Double getMontant() { return montant; }
    public void setMontant(Double montant) { this.montant = montant; }

    public Double getQuantite() { return quantite; }
    public void setQuantite(Double quantite) { this.quantite = quantite; }

    public Date getDateAchat() { return dateAchat; }
    public void setDateAchat(Date dateAchat) { this.dateAchat = dateAchat; }

    public Long getVehiculeId() { return vehiculeId; }
    public void setVehiculeId(Long vehiculeId) { this.vehiculeId = vehiculeId; }

    public Long getCarburantId() { return carburantId; }
    public void setCarburantId(Long carburantId) { this.carburantId = carburantId; }

    public Long getUtilisateurId() { return utilisateurId; }
    public void setUtilisateurId(Long utilisateurId) { this.utilisateurId = utilisateurId; }

    public EtatBon getEtat() { return etat; }
    public void setEtat(EtatBon etat) { this.etat = etat; }

	public Long getMissionId() {return missionId;}
	public void setMissionId(Long missionId) {this.missionId = missionId;}
    
}
