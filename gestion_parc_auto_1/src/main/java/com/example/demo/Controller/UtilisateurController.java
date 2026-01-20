package com.example.demo.Controller;

import com.example.demo.Repository.UtilisateurRepository;
import com.example.demo.model.Utilisateur;
import com.example.demo.service.RecaptchaService;
import com.example.demo.service.UtilisateurService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/auth")
public class UtilisateurController {

    @Autowired
    private UtilisateurService userService;

    @Autowired
    private UtilisateurRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private RecaptchaService recaptchaService;

 @PostMapping("/register")
public ResponseEntity<?> register(@Valid @RequestBody Utilisateur utilisateur) {
    // Vérification de l'email
    if (userRepository.findByEmail(utilisateur.getEmail()).isPresent()) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Collections.singletonMap("message", "Email déjà utilisé"));
    }

    // Vérification du CIN
    if (userRepository.findByCin(utilisateur.getCin()).isPresent()) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Collections.singletonMap("message", "CIN déjà utilisé"));
    }

    // Vérification du code CNAM (unicité)
    if (utilisateur.getCodeCnam() != null && userRepository.existsByCodeCnam(utilisateur.getCodeCnam())) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Collections.singletonMap("message", "Code CNAM déjà utilisé"));
    }

    // Vérification de la longueur du code CNAM
    if (utilisateur.getCodeCnam() == null || !utilisateur.getCodeCnam().matches("^[a-zA-Z0-9]{10}$")) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Collections.singletonMap("message", "Le code CNAM doit contenir exactement 10 lettres et/ou chiffres (alphanumérique)."));
    }

    try {
        userService.registerUser(utilisateur);
        return ResponseEntity.ok(Collections.singletonMap("message", "Un code a été envoyé à votre email."));
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Collections.singletonMap("message", "Erreur lors de l'inscription : " + e.getMessage()));
    }
}



    // Vérification du code
    @PostMapping("/verify")
    public ResponseEntity<?> verify(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String code = body.get("code");

        boolean verified = userService.verifyUser(email, code);

        if (verified) {
            return ResponseEntity.ok(Collections.singletonMap("message", "Compte activé !"));
        } else {
            return ResponseEntity.badRequest()
                    .body(Collections.singletonMap("message", "Code incorrect ou expiré."));
        }
    }

    // Connexion sécurisée
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginData) {
        String email = loginData.get("email");
        String password = loginData.get("password");

        if (email == null || password == null) {
            return ResponseEntity.badRequest()
                    .body(Collections.singletonMap("message", "Email et mot de passe requis"));
        }

        Optional<Utilisateur> optionalUtilisateur = userRepository.findByEmail(email);

        if (optionalUtilisateur.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Collections.singletonMap("message", "Email invalide"));
        }

        Utilisateur utilisateur = optionalUtilisateur.get();

        if (utilisateur.getMotDePasse() == null || !passwordEncoder.matches(password, utilisateur.getMotDePasse())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Collections.singletonMap("message", "Mot de passe incorrect"));
        }

        if (!utilisateur.isVerifie()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Collections.singletonMap("message", "Votre compte n'est pas encore vérifié"));
        }
        if (utilisateur.getCodeCnam() == null || utilisateur.getCodeCnam().trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("message", "Le code CNAM est requis"));
        }

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Connexion réussie");
        response.put("idUtilisateur", utilisateur.getIdUtilisateur());
        response.put("email", utilisateur.getEmail());
        response.put("nom", utilisateur.getNom());
        response.put("prenom", utilisateur.getPrenom());
        response.put("role", utilisateur.getRole());

        return ResponseEntity.ok(response);
    }
    @PostMapping("/verify-captcha")
    public String verifyCaptcha(@RequestParam("token") String token) {
        boolean isValid = recaptchaService.verify(token);

        if (isValid) {
            return "✅ reCAPTCHA validé !";
        } else {
            return "❌ reCAPTCHA invalide !";
        }
    }
    
}
