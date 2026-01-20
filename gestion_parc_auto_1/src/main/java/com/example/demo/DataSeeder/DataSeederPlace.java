package com.example.demo.DataSeeder;

import com.example.demo.Repository.ParkingRepository;
import com.example.demo.Repository.VehiculeRepository;
import com.example.demo.Repository.PlaceRepository;
import com.example.demo.model.Parking;
import com.example.demo.model.Place;
import com.example.demo.model.Vehicule;
import com.github.javafaker.Faker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Order(8) 
public class DataSeederPlace implements CommandLineRunner {

    @Autowired
    private PlaceRepository placeRepository;

    @Autowired
    private ParkingRepository parkingRepository;

    @Autowired
    private VehiculeRepository vehiculeRepository;

    private final Faker faker = new Faker();

    @Override
    public void run(String... args) {
        // Vider les tables avant d'ajouter de nouvelles données
        placeRepository.deleteAll();

        // Vérifier si des parkings existent dans la base de données
        List<Parking> parkings = parkingRepository.findAll();
        if (parkings.isEmpty()) {
            System.out.println("⚠️ Aucun parking trouvé. Assurez-vous d'avoir des parkings dans la base de données avant d'ajouter des places.");
            return;  // Ne pas continuer si aucun parking n'est trouvé
        }

        // Vérifier si des véhicules existent dans la base de données
        List<Vehicule> vehicules = vehiculeRepository.findAll();

        // Générer des places de parking pour chaque parking
        for (Parking parking : parkings) {
            // Nombre de places aléatoire pour chaque parking
            int nombreDePlaces = faker.number().numberBetween(5, 20); // Nombre de places entre 5 et 20 par parking

            for (int i = 0; i < nombreDePlaces; i++) {
                Place place = new Place();
                place.setNom("Place " + (i + 1));
                place.setNumeroPlace(i + 1);  // Numéro de place de parking
                place.setEstOccupee(faker.bool().bool());  // Statut aléatoire (occupée ou libre)

                // Si la place est occupée, affecter un véhicule aléatoire
                if (place.getEstOccupee() && !vehicules.isEmpty()) {
                	Vehicule vehicule = vehicules.get(faker.number().numberBetween(0, vehicules.size() - 1));
                    place.setVehicule(vehicule);
                }

                // Assigner le parking à la place
                place.setParking(parking);

                // Sauver la place dans la base de données
                placeRepository.save(place);
            }
        }

        System.out.println("✅ Données de places générées avec succès !");
    }
}
