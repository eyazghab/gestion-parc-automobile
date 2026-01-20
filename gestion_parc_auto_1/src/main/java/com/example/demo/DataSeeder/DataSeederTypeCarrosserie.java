package com.example.demo.DataSeeder;


import com.example.demo.model.TypeCarrosserie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.example.demo.Repository.TypeCarRepository;

@Component
@Order(5) // S'exécute avant la création de véhicule 

public class DataSeederTypeCarrosserie implements CommandLineRunner {

    @Autowired
    private TypeCarRepository typeCarrosserieRepository;

    @Override
    public void run(String... args) throws Exception {
        // Vider la table au démarrage (optionnel)

        // Liste des types de carrosserie
        String[] types = {
            "Berline",
            "SUV",
            "Coupé",
            "Break",
            "Pick-up",
            "Fourgonnette",
            "Cabriolet",
            "Monospace",
            "Utilitaire",
            "Hatchback"
        };

        for (String nomType : types) {
            TypeCarrosserie tc = new TypeCarrosserie();
            tc.setType(nomType);
            typeCarrosserieRepository.save(tc);
        }

        System.out.println("✅ Types de carrosserie ajoutés avec succès !");
    }
}
