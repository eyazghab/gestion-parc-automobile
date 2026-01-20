package com.example.demo.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

@Entity
public class LigneMaintenance {
	 @Id@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	private int quantite;
	private Double montant;
	 @Temporal(TemporalType.DATE)
	private Date dateCreation;
	private String description;

	
	//Les Relations 
	@ManyToOne
	@JoinColumn(name = "maintenance_id")
	@JsonBackReference
	private Maintenance maintenance;

    @ManyToOne
    @JoinColumn(name = "idTypeMaintenance") 
    @JsonIgnore
    private TypeMaintenance typeMaintenance;

    private double cout;

	public double getCout() {
		return cout;
	}

	public void setCout(double cout) {
		this.cout = cout;
	}

	public int getQuantite() {
		return quantite;
	}

	public void setQuantite(int quantite) {
		this.quantite = quantite;
	}

	public Double getMontant() {
		return montant;
	}

	public void setMontant(double montant) {
		this.montant = montant;
	}

	public Date getDateCreation() {
		return dateCreation;
	}

	public void setDateCreation(Date dateCreation) {
		this.dateCreation = dateCreation;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	public void setMaintenance(Maintenance maintenance) {
		this.maintenance = maintenance;
	}
	public void setTypeMaintenance(TypeMaintenance typeMaintenance) {
		this.typeMaintenance = typeMaintenance;
	}
	public Long getId() {
		return id;
	}
	public Maintenance getMaintenance() {
		return maintenance;
	}
	public TypeMaintenance getTypeMaintenance() {
		return typeMaintenance;
	}
}