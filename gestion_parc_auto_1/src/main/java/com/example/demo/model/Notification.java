package com.example.demo.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String message;
    private String titre;
    private LocalDateTime dateNotif;
    private boolean lue = false;
    private String type; 

    @ManyToOne
    @JoinColumn(name = "destinataire_id_utilisateur")
    private Utilisateur destinataire;
    
 // getters/setters
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public LocalDateTime getDateNotif() {
		return dateNotif;
	}

	public void setDateNotif(LocalDateTime dateNotif) {
		this.dateNotif = dateNotif;
	}

	public boolean isLue() {
		return lue;
	}

	public void setLue(boolean lue) {
		this.lue = lue;
	}
public Utilisateur getDestinataire() {
	return destinataire;
}
public void setDestinataire(Utilisateur destinataire) {
	this.destinataire = destinataire;
}

public String getTitre() {
	return titre;
}

public void setTitre(String titre) {
	this.titre = titre;
}
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
