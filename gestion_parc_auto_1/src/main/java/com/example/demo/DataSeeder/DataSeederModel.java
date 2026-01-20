package com.example.demo.DataSeeder;

import com.example.demo.Repository.ModeleRepository;
import com.example.demo.model.Modele;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(4) 

public class DataSeederModel implements CommandLineRunner {

    @Autowired
    private ModeleRepository modeleRepository;

    @Override
    public void run(String... args) {
        // Supprimer les anciens modèles
        //modeleRepository.deleteAll();

        // Liste de noms réalistes de modèles de véhicules
        String[] modelesVehicules = {
            "Toyota Corolla",
            "Renault Clio",
            "Peugeot 208",
            "Ford Focus",
            "Volkswagen Golf",
            "Hyundai i30",
            "Citroën C3",
            "Nissan Qashqai",
            "Dacia Sandero",
            "BMW Série 1"
        };

        for (String nomModele : modelesVehicules) {
            Modele modele = new Modele();
            modele.setNom(nomModele);
            modeleRepository.save(modele);
        }

        System.out.println("✅ Modèles de véhicules ajoutés avec succès !");
    }
}
