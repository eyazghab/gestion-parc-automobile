package com.example.demo.service;

import com.example.demo.Repository.DepartementRepository;
import com.example.demo.Repository.UtilisateurRepository;
import com.example.demo.RepositoryCnam.CodeCnamRepository;
import com.example.demo.model.Departement;
import com.example.demo.model.Utilisateur;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UtilisateurService {

    @Autowired
    private UtilisateurRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private DepartementRepository departementRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private  CodeCnamRepository codeCnamRepository; 
    
    
    public String generateVerificationCode() {
        int code = (int)(Math.random() * 900000) + 100000; // entre 100000 et 999999
        return String.valueOf(code);
    }

    public void registerUser(Utilisateur utilisateur) {
        if (utilisateur.getDepartement() == null || utilisateur.getDepartement().getId() == null) {
            throw new IllegalArgumentException("Le département est requis");
        }

        Departement departement = departementRepository.findById(utilisateur.getDepartement().getId())
            .orElseThrow(() -> new IllegalArgumentException("Département introuvable"));

        utilisateur.setDepartement(departement);

        if (utilisateur.getRole() == null) {
            utilisateur.setRole("USER");
        }

        if (utilisateur.getCodeCnam() == null || utilisateur.getCodeCnam().trim().isEmpty()) {
            throw new IllegalArgumentException("Le code CNAM est requis.");
        }

        // Vérifier si le code CNAM existe
        boolean cnamExists = codeCnamRepository.findById(utilisateur.getCodeCnam()).isPresent();
        if (!cnamExists) {
            throw new IllegalArgumentException("Code CNAM invalide.");
        }

        // Encodage du mot de passe
        String hashedPassword = passwordEncoder.encode(utilisateur.getMotDePasse());
        utilisateur.setMotDePasse(hashedPassword);
        // Code de vérification
        String code = generateVerificationCode();
        utilisateur.setCodeVerification(code);
        utilisateur.setExpirationCode(LocalDateTime.now().plusMinutes(10));
        utilisateur.setVerifie(false);
        utilisateur.setActif(true);

        userRepository.save(utilisateur);

        // Envoi de l’email
        String subject = "Vérification de votre compte";
        String body = "Bonjour " + utilisateur.getPrenom() + ",\n\nVoici votre code de vérification : " + code +
                      "\n\n⚠️ Ce code expire dans 10 minutes.\n\nMerci.";
        emailService.sendEmail(utilisateur.getEmail(), subject, body);
    }

    public boolean verifyUser(String email, String code) {
        Optional<Utilisateur> optional = userRepository.findByEmailAndCodeVerification(email, code);
        if (optional.isPresent()) {
            Utilisateur user = optional.get();

            if (user.getExpirationCode() != null &&
                LocalDateTime.now().isAfter(user.getExpirationCode())) {
                return false; // ❌ Code expiré
            }

            user.setVerifie(true);
            user.setCodeVerification(null);
            user.setExpirationCode(null); // nettoyer
            userRepository.save(user);
            return true;
        }
        return false;
    }

    public void resendVerificationCode(String email) {
        Optional<Utilisateur> optional = userRepository.findByEmail(email);
        if (optional.isPresent()) {
            Utilisateur user = optional.get();
            String code = generateVerificationCode();
            user.setCodeVerification(code);
            user.setExpirationCode(LocalDateTime.now().plusMinutes(10));
            userRepository.save(user);

            String subject = "🔁 Nouveau code de vérification";
            String body = "Bonjour " + user.getPrenom() + ",\n\nVotre nouveau code est : " + code +
                          "\nIl expire dans 10 minutes.\n\nMerci.";
            emailService.sendEmail(user.getEmail(), subject, body);
        }
    }
}
