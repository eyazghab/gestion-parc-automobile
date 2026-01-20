package com.example.demo.DataSeeder;

import com.example.demo.Repository.*;
import com.example.demo.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.*;

@Component
@Order(7)
public class DataSeederVehicule implements CommandLineRunner {

    @Autowired
    private VehiculeRepository vehiculeRepository;

    @Autowired
    private ParkingRepository parkingRepository;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private ModeleRepository modeleRepository;

    @Autowired
    private TypeCarRepository typeCarrosserieRepository;

    @Override
    public void run(String... args) throws Exception {
        if (vehiculeRepository.count() > 0) {
            System.out.println("ℹ️ Véhicules déjà présents. Aucune insertion faite.");
            return;
        }

        List<Parking> parkings = parkingRepository.findAll();
        List<Utilisateur> utilisateurs = utilisateurRepository.findAll();
        List<Modele> modeles = modeleRepository.findAll();
        List<TypeCarrosserie> carrosseries = typeCarrosserieRepository.findAll();

        if (parkings.isEmpty() || utilisateurs.isEmpty() || modeles.isEmpty() || carrosseries.isEmpty()) {
            System.out.println("⚠️ Données liées manquantes. Veuillez insérer des parkings, utilisateurs, modèles et types de carrosserie d'abord.");
            return;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Random rand = new Random();

        for (int i = 1; i <= 50; i++) {
            Vehicule v = new Vehicule();
            v.setImmatriculation("AB-" + (100 + i) + "-CD");
            v.setNumeroChassis("CHS" + (100000 + rand.nextInt(900000)));
            v.setDateCircu(sdf.parse("2020-01-" + ((i % 28) + 1)));
            v.setKilometrageActuel(10000 + rand.nextInt(90000));
            v.setAnneeModel(2015 + rand.nextInt(10));
            v.setTypeCarburant(rand.nextBoolean() ? "Essence" : "Diesel");

            v.setParking(parkings.get(rand.nextInt(parkings.size())));
            v.setUtilisateur(utilisateurs.get(rand.nextInt(utilisateurs.size())));
            v.setModele(modeles.get(rand.nextInt(modeles.size())));
            v.setTypeCarrosserie(carrosseries.get(rand.nextInt(carrosseries.size())));

            Date acquisitionDate = sdf.parse("2019-12-" + ((i % 28) + 1));
            v.setDate_acquisition(acquisitionDate);
            EtatVehicule[] etats = EtatVehicule.values();
            v.setEtat(etats[rand.nextInt(etats.length)]);

            vehiculeRepository.save(v);
        }

        System.out.println("✅ 50 véhicules insérés avec succès !");
    }
}
