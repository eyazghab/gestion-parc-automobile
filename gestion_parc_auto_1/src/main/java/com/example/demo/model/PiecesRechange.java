package com.example.demo.model;



import jakarta.persistence.*;

@Entity
public class PiecesRechange {
    
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPieceDeRechange;
    private String nom;  // Nom de la pièce de rechange
    private String reference;  // Référence de la pièce
    private Double prix;  // Prix de la pièce
    private Integer quantiteStock;  // Quantité en stock

    //Les Relations 
    @ManyToOne
    @JoinColumn(name = "idFournisseur", nullable = false)  // Clé étrangère vers Fournisseur
    private Fournisseur fournisseur;  // Fournisseur de la pièce

    @ManyToOne
    @JoinColumn(name = "idVehicule")  // Clé étrangère vers Véhicule (si la pièce est spécifique à un véhicule)
    private Vehicule vehicule;  // Véhicule auquel cette pièce est associée (si applicable)
    
    @ManyToOne
    @JoinColumn(name = "article_id")
    private Article article;

    @ManyToOne
    @JoinColumn(name = "stock_id")
    private Stock stock;
 // Getters et Setters
    public Long getId() {
        return idPieceDeRechange;
    }

    public void setId(Long id) {
        this.idPieceDeRechange = id;
    }
	public Integer getQuantiteStock() {
		return quantiteStock;
	}

	public void setQuantiteStock(Integer quantiteStock) {
		this.quantiteStock = quantiteStock;
	}

	public Double getPrix() {
		return prix;
	}

	public void setPrix(Double prix) {
		this.prix = prix;
	}

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}
}