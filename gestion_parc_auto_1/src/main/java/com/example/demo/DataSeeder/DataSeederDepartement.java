package com.example.demo.DataSeeder;

import com.example.demo.Repository.DepartementRepository;
import com.example.demo.Repository.EntrepriseRepository;
import com.example.demo.model.Departement;
import com.example.demo.model.Entreprise;
import com.github.javafaker.Faker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@Component
@Order(2) 

public class DataSeederDepartement implements CommandLineRunner {

    @Autowired
    private DepartementRepository departementRepository;

    @Autowired
    private EntrepriseRepository entrepriseRepository;

    private final Faker faker = new Faker(new Locale("fr"));

    private final List<String> nomsDepartements = Arrays.asList(
            "Commercial", "Technique", "Administration", "Logistique", "Production",
            "Marketing", "Informatique", "RH", "Qualité", "Sécurité"
    );

    @Override
    public void run(String... args) {
        if (departementRepository.count() == 0) {

            List<Entreprise> entreprises = entrepriseRepository.findAll();

            if (entreprises.isEmpty()) {
                System.out.println("⚠️ Aucune entreprise trouvée. Assure-toi que les entreprises sont déjà insérées.");
                return;
            }

            for (Entreprise entreprise : entreprises) {
                for (String nomDept : nomsDepartements) {
                    Departement departement = new Departement();
                    departement.setNom(nomDept);
                    departement.setResponsable(faker.name().fullName());
                    departement.setEntreprise(entreprise); // 🔗 Clé étrangère

                    departementRepository.save(departement);
                }
                System.out.println("✅ 10 départements créés pour l'entreprise : " + entreprise.getNomEntreprise());
            }
        }
    }
}
