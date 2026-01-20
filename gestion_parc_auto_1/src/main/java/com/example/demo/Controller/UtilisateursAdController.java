package com.example.demo.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.Repository.UtilisateurRepository;
import com.example.demo.model.Utilisateur;

	@RestController
	@RequestMapping("/api/utilisateurs")
	@CrossOrigin(origins = "http://localhost:4200")
	public class UtilisateursAdController {
	    @Autowired
	    private UtilisateurRepository utilisateurRepository;
	    
	    @Autowired
	    private PasswordEncoder passwordEncoder;

	    @GetMapping
	    public ResponseEntity<List<Utilisateur>> getAllUtilisateurs() {
	        List<Utilisateur> utilisateurs = utilisateurRepository.findAll();
	        return ResponseEntity.ok(utilisateurs);
	    }
	    @PutMapping("/{id}")
	    public ResponseEntity<Utilisateur> updateUtilisateur(@PathVariable Long id, @RequestBody Utilisateur utilisateurDetails) {
	        Utilisateur utilisateur = utilisateurRepository.findById(id)
	            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

	        utilisateur.setNom(utilisateurDetails.getNom());
	        utilisateur.setPrenom(utilisateurDetails.getPrenom());
	        utilisateur.setEmail(utilisateurDetails.getEmail());
	        utilisateur.setRole(utilisateurDetails.getRole());
	        utilisateur.setTelephone(utilisateurDetails.getTelephone());
	        utilisateur.setCin(utilisateurDetails.getCin());
	        utilisateur.setCodeCnam(utilisateurDetails.getCodeCnam());
	        utilisateur.setDateEmbauche(utilisateurDetails.getDateEmbauche());
	        utilisateur.setMotDePasse(utilisateurDetails.getMotDePasse());

	        if (!utilisateurDetails.getMotDePasse().equals(utilisateur.getMotDePasse())) {
	            utilisateur.setMotDePasse(passwordEncoder.encode(utilisateurDetails.getMotDePasse()));
	        }




	        utilisateur.setActif(utilisateurDetails.isActif()); // ✅ Mise à jour du champ "actif"

	        Utilisateur updated = utilisateurRepository.save(utilisateur);
	        return ResponseEntity.ok(updated);
	    }
	    @GetMapping("/{id}")
	    public ResponseEntity<Utilisateur> getUtilisateurById(@PathVariable Long id) {
	        Utilisateur utilisateur = utilisateurRepository.findById(id)
	            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'id : " + id));
	        
	        return ResponseEntity.ok(utilisateur);
	    }
	}


