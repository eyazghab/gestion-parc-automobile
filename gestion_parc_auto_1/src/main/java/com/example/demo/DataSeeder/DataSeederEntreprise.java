package com.example.demo.DataSeeder;

import com.example.demo.Repository.EntrepriseRepository;
import com.example.demo.model.Entreprise;
import com.github.javafaker.Faker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(1) // S'exécute en premier

public class DataSeederEntreprise implements CommandLineRunner {

    @Autowired
    private EntrepriseRepository entrepriseRepository;

    private final Faker faker = new Faker();

    @Override
    public void run(String... args) {
        // Facultatif : vider les anciennes données
        entrepriseRepository.deleteAll();

        for (int i = 0; i < 5; i++) {
            Entreprise entreprise = new Entreprise();
            entreprise.setNomEntreprise(faker.company().name());
            entreprise.setSecteurActivite(faker.company().industry());
            entreprise.setAdresse(faker.address().fullAddress());
            entreprise.setEmail(faker.internet().emailAddress());
            entreprise.setTelephone(faker.phoneNumber().phoneNumber());
            entreprise.setNbEmployes(faker.number().numberBetween(20, 500));

            entrepriseRepository.save(entreprise);
        }

        System.out.println("✅ Entreprises faker insérées avec succès !");
    }
}
