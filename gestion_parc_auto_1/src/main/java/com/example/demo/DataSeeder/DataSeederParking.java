package com.example.demo.DataSeeder;

import com.example.demo.Repository.ParkingRepository;
import com.example.demo.Repository.UtilisateurRepository;
import com.example.demo.Repository.PlaceRepository;
import com.example.demo.model.Parking;
import com.example.demo.model.Utilisateur;
import com.example.demo.model.Place;
import com.github.javafaker.Faker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Order(6) 
public class DataSeederParking implements CommandLineRunner {

    @Autowired
    private ParkingRepository parkingRepository;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private PlaceRepository placeRepository;

    private final Faker faker = new Faker();

    @Override
    public void run(String... args) throws Exception {
        // Vider les tables avant d'ajouter de nouvelles données
        parkingRepository.deleteAll();

        // Vérifier si des utilisateurs existent dans la base de données
        List<Utilisateur> utilisateurs = utilisateurRepository.findAll();
        if (utilisateurs.isEmpty()) {
            System.out.println("⚠️ Aucun utilisateur trouvé. Assurez-vous d'avoir des utilisateurs dans la base de données avant d'ajouter des parkings.");
            return;  // Ne pas continuer si aucun utilisateur n'est trouvé
        }

        // Générer des données fictives pour les parkings
        for (int i = 0; i < 10; i++) {
            Parking parking = new Parking();

            // Définir les informations du parking
            parking.setNomParking(faker.company().name());
            parking.setAdresse(faker.address().streetAddress());
            parking.setCapacite(faker.number().numberBetween(50, 200));  // Capacité entre 50 et 200
            parking.setEstDisponible(faker.bool().bool());  // Statut aléatoire de disponibilité

            // Assigner un utilisateur au parking (assurez-vous qu'il existe)
            Utilisateur utilisateur = utilisateurs.get(faker.number().numberBetween(0, utilisateurs.size() - 1));  // Choisir un utilisateur aléatoire
            parking.setUtilisateur(utilisateur);

            // Sauver le parking
            parkingRepository.save(parking);

            // Générer des places de parking pour ce parking
            for (int j = 0; j < parking.getCapacite(); j++) {
                Place place = new Place();
                place.setNom("Place " + (j + 1));
                place.setEstOccupee(faker.bool().bool());  // Statut aléatoire de la place de parking
                place.setParking(parking);

                // Sauver la place de parking
                placeRepository.save(place);
            }
        }

        System.out.println("✅ Données de parkings générées avec succès !");
    }
}
