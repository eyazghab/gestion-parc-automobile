package com.example.demo.DataSeeder;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.example.demo.Repository.BonCarburantRepository;
import com.example.demo.Repository.CarburantRepository;
import com.example.demo.Repository.UtilisateurRepository;
import com.example.demo.Repository.VehiculeRepository;
import com.example.demo.model.BonCarburant;
import com.example.demo.model.Carburant;
import com.example.demo.model.Utilisateur;
import com.example.demo.model.Vehicule;

@Component
public class DataSeederCarburant implements CommandLineRunner {

    @Autowired
    private VehiculeRepository vehiculeRepository;

    @Autowired
    private CarburantRepository carburantRepository;

    @Autowired
    private BonCarburantRepository bonCarburantRepository;

    @Autowired
    private UtilisateurRepository utilisateurRepository; // ⚠️ Ajouter le repository Utilisateur

    @Override
    public void run(String... args) throws Exception {

        // ----- VEHICULES -----
        String[] immatriculations = {"AB-101-CD", "AB-102-CD", "AB-103-CD", "AB-104-CD", "AB-105-CD"};

        for (String immat : immatriculations) {
            vehiculeRepository.findByImmatriculation(immat)
                .orElseGet(() -> {
                    Vehicule v = new Vehicule();
                    v.setImmatriculation(immat);
                    return vehiculeRepository.save(v);
                });
        }

        // ----- CARBURANTS -----
        Carburant essence = carburantRepository.findByCode("ESS")
                .orElseGet(() -> {
                    Carburant c = new Carburant();
                    c.setTypeCarburant("Essence");
                    c.setPrix(2.1);
                    c.setCode("ESS");
                    return carburantRepository.save(c);
                });

        Carburant diesel = carburantRepository.findByCode("DSL")
                .orElseGet(() -> {
                    Carburant c = new Carburant();
                    c.setTypeCarburant("Diesel");
                    c.setPrix(1.8);
                    c.setCode("DSL");
                    return carburantRepository.save(c);
                });

        // ----- UTILISATEUR ADMIN -----
     // Vérifier si l'admin existe, sinon le créer
        Utilisateur admin = utilisateurRepository.findByEmail("zghabeya15@gmail.com")
                .orElseGet(() -> {
                    Utilisateur u = new Utilisateur();
                    u.setNom("Elit");
                    u.setPrenom("Info");
                    u.setEmail("zghabeya15@gmail.com"); // doit correspondre au username de Spring Security
                    u.setMotDePasse("{noop}admin123"); // mot de passe pour test, {noop} = pas de hash
                    u.setRole("ADMIN");
                    u.setVerifie(true);
                    u.setActif(true);
                    u.setCin("12345678");
                    //u.setDateEmbauche(new Date().toString());
                    return utilisateurRepository.save(u);
                });


        // ----- BONS CARBURANT -----
        List<Vehicule> vehicules = vehiculeRepository.findAll();
        for (Vehicule v : vehicules) {
            // Vérifier si un bon existe déjà pour ce véhicule
            if (bonCarburantRepository.findByVehiculeIdVehicule(v.getIdVehicule()).isEmpty()) {
                BonCarburant b = new BonCarburant();
                b.setVehicule(v);
                b.setCarburant(essence); // ou diesel selon le véhicule
                b.setQuantite(50.0);
                b.setMontant(105.0);
                b.setDateAchat(new Date());
                b.setResponsable("Admin");
                b.setUtilisateur(admin); // ⚠️ assigner l'utilisateur obligatoire
                bonCarburantRepository.save(b);
            }
        }

        System.out.println("✅ DataSeeder exécuté sans créer de doublons !");
    }
}
