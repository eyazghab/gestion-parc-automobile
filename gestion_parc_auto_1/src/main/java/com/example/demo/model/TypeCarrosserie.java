package com.example.demo.model;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
@Entity
public class TypeCarrosserie {
	 @Id@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String type;

    @OneToMany(mappedBy = "typeCarrosserie")
    private List<Vehicule> vehicules;
public Long getId() {
	return id;
}
public void setId(Long id) {
	this.id = id;
}
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
