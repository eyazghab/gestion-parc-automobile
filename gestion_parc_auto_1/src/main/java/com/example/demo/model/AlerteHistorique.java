package com.example.demo.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
@Entity
@Table(name = "alerte_historique") 
public class AlerteHistorique {
	  @Id@GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;
	    private String message;
	    private LocalDateTime dateEnvoi;
	    private boolean resolue = false; // <- nouveau champ

	    @ManyToOne
	    private Vehicule vehicule;

	    @ManyToOne
	    private Utilisateur destinataire;

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}

		public LocalDateTime getDateEnvoi() {
			return dateEnvoi;
		}

		public void setDateEnvoi(LocalDateTime dateEnvoi) {
			this.dateEnvoi = dateEnvoi;
		}

		public Vehicule getVehicule() {
			return vehicule;
		}

		public void setVehicule(Vehicule vehicule) {
			this.vehicule = vehicule;
		}

		public Utilisateur getDestinataire() {
			return destinataire;
		}

		public void setDestinataire(Utilisateur destinataire) {
			this.destinataire = destinataire;
		}

		public boolean isResolue() {
			return resolue;
		}

		public void setResolue(boolean resolue) {
			this.resolue = resolue;
		}

}
