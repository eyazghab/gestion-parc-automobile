package com.example.demo.model;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.validation.constraints.Pattern;

import jakarta.persistence.*;

@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})

public class Utilisateur {
    
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idUtilisateur;
	@Pattern(regexp = "^[a-zA-Z0-9]{10}$", message = "Le code CNAM doit contenir exactement 10 lettres et/ou chiffres (alphanumérique).")
	@Column(unique = true, nullable = false)
	private String codeCnam;
	private String nom;  // Nom de l'utilisateur
    private String prenom;  // Prénom de l'utilisateur
    private String email;  // Email de l'utilisateur
    private String motDePasse;  // Mot de passe de l'utilisateur
    private String role;  // Rôle de l'utilisateur (par exemple, "ADMIN", "UTILISATEUR")
    private Boolean verifie = false;
    private boolean actif = true;
    @Column(name = "code_verification") // ← assure le bon mapping avec la base
    private String codeVerification;
    private LocalDateTime expirationCode;
    private String telephone;
    @Column(unique = true, nullable = false)
    private String cin;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate dateEmbauche;
    
    
 // Relation One-to-Many avec Achats
   
    @OneToMany(mappedBy = "utilisateur")
    private Set<Vehicule> vehicules;  // Liste des véhicules attribués à cet utilisateur
    
    @ManyToOne
    @JoinColumn(name="departement_id", nullable=false)
    private Departement departement;
    
    @OneToMany(mappedBy = "utilisateur")
    private List<OrdreMission> ordresMission;
    
    public Long getIdUtilisateur() {
        return idUtilisateur;
    }

    public void setIdUtilisateur(Long idUtilisateur) {
        this.idUtilisateur = idUtilisateur;
    }

    
	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getMotDePasse() {
		return motDePasse;
	}

	public void setMotDePasse(String motDePasse) {
		this.motDePasse = motDePasse;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPrenom() {
		return prenom;
	}

	public void setPrenom(String prenom) {
		this.prenom = prenom;
	}

	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}
	public void setDepartement(Departement departement) {
		this.departement = departement;
	}

	

    public Utilisateur(String nom, String prenom, String email, String motDePasse, String role,String codeCnam,Departement dep) {
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.motDePasse = motDePasse;
        this.role = role;
        this.codeCnam=codeCnam;
        this.departement=dep;
    }
    public Utilisateur() {}

	public boolean isVerifie() {
		return verifie;
	}

	public void setVerifie(boolean verifie) {
		this.verifie = verifie;
	}

	public String getCodeVerification() {
		return codeVerification;
	}

	public void setCodeVerification(String codeVerification) {
		this.codeVerification = codeVerification;
	}

	public LocalDateTime getExpirationCode() {
		return expirationCode;
	}

	public void setExpirationCode(LocalDateTime expirationCode) {
		this.expirationCode = expirationCode;
	}
	public Departement getDepartement() {
		return departement;
	}

	public boolean isActif() {
		return actif;
	}

	public void setActif(boolean actif) {
		this.actif = actif;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public String getCin() {
		return cin;
	}

	public void setCin(String cin) {
		this.cin = cin;
	}

	public LocalDate getDateEmbauche() {
		return dateEmbauche;
	}

	public void setDateEmbauche(LocalDate dateEmbauche) {
		this.dateEmbauche = dateEmbauche;
	}
	public String getCodeCnam() {
		return codeCnam;
	}
	public void setCodeCnam(String codeCnam) {
		this.codeCnam = codeCnam;
	}

	public Utilisateur(Long idUtilisateur) {
        this.idUtilisateur = idUtilisateur;
    }
	
	
}