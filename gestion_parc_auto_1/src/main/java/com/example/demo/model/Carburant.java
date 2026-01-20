package com.example.demo.model;


import java.util.Set;

import jakarta.persistence.*;

@Entity
public class Carburant {
    
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCarburant;

    private String typeCarburant; // Essence, Diesel, etc.
    private Double prix;          // prix par litre
    private String code;

    @OneToMany(mappedBy = "carburant", fetch = FetchType.LAZY)
    private Set<BonCarburant> bonsCarburant;
    
    // Getters et setters
	public String getTypeCarburant() {
		return typeCarburant;
	}

	public void setTypeCarburant(String typeCarburant) {
		this.typeCarburant = typeCarburant;
	}

	public Double getPrix() {
		return prix;
	}

	public void setPrix(Double prix) {
		this.prix = prix;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Long getIdCarburant() {
		return idCarburant;
	}

	public void setIdCarburant(Long idCarburant) {
		this.idCarburant = idCarburant;
	}
	
}