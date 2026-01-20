package com.example.demo.model;



import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})

public class Document {
	 @Id@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String type;

    // Les Relations
    @ManyToOne
    @JoinColumn(name = "vehicule_id")
    private Vehicule vehicule;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Vehicule getVehicule() {
	    return vehicule;
	}

	public void setVehicule(Vehicule vehicule) {
	    this.vehicule = vehicule;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
}
