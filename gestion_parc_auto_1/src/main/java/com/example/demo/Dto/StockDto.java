package com.example.demo.Dto;


import java.util.Date;

public class StockDto {
 private Long id;
 private String iot;
 private Integer quantite_disp;
 private Integer quantite_reservee;
 private Date date_dernier_entree;
 private Date date_dernier_sortie;
 private Date date_dernier_inventaire;
 private Boolean actif;

 // Getters et setters
 public Long getId() { return id; }
 public void setId(Long id) { this.id = id; }

 public String getIot() { return iot; }
 public void setIot(String iot) { this.iot = iot; }

 public Integer getQuantite_disp() { return quantite_disp; }
 public void setQuantite_disp(Integer quantite_disp) { this.quantite_disp = quantite_disp; }

 public Integer getQuantite_reservee() { return quantite_reservee; }
 public void setQuantite_reservee(Integer quantite_reservee) { this.quantite_reservee = quantite_reservee; }

 public Date getDate_dernier_entree() { return date_dernier_entree; }
 public void setDate_dernier_entree(Date date_dernier_entree) { this.date_dernier_entree = date_dernier_entree; }

 public Date getDate_dernier_sortie() { return date_dernier_sortie; }
 public void setDate_dernier_sortie(Date date_dernier_sortie) { this.date_dernier_sortie = date_dernier_sortie; }

 public Date getDate_dernier_inventaire() { return date_dernier_inventaire; }
 public void setDate_dernier_inventaire(Date date_dernier_inventaire) { this.date_dernier_inventaire = date_dernier_inventaire; }

 public Boolean getActif() { return actif; }
 public void setActif(Boolean actif) { this.actif = actif; }
}
