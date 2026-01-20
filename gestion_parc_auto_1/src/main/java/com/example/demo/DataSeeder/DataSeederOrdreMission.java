package com.example.demo.DataSeeder;

import com.example.demo.model.OrdreMission;
import com.example.demo.model.Utilisateur;
import com.example.demo.model.Vehicule;
import com.example.demo.model.EtatMission;
import com.example.demo.Repository.OrdreMissionRepository;
import com.example.demo.Repository.UtilisateurRepository;
import com.example.demo.Repository.VehiculeRepository;
import com.github.javafaker.Faker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;

@Component
public class DataSeederOrdreMission implements CommandLineRunner {

    @Autowired
    private OrdreMissionRepository ordreMissionRepository;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private VehiculeRepository vehiculeRepository;

    private final Faker faker = new Faker(new Locale("fr"));
    private final Random random = new Random();

    private final List<String> objets = Arrays.asList(
        "Réunion client", "Audit interne", "Formation", "Visite de site",
        "Rendez-vous fournisseur", "Conférence", "Inspection technique",
        "Livraison urgente", "Réunion projet", "Support technique"
    );

    private final List<String> destinations = Arrays.asList(
        "Paris", "Lyon", "Marseille", "Toulouse", "Nice", "Nantes",
        "Strasbourg", "Bordeaux", "Montpellier", "Lille"
    );

    @Override
    public void run(String... args) throws Exception {

        ordreMissionRepository.deleteAll();

        List<Utilisateur> utilisateurs = utilisateurRepository.findAll();
        List<Vehicule> vehicules = vehiculeRepository.findAll();

        if (utilisateurs.isEmpty() || vehicules.isEmpty()) {
            System.out.println("❌ Impossible de générer les missions (manque utilisateurs ou véhicules).");
            return;
        }

        List<OrdreMission> missions = new ArrayList<>();

        // Génération de 20 missions réalistes
        for (int i = 0; i < 20; i++) {
            OrdreMission mission = new OrdreMission();

            mission.setObjetMission(objets.get(random.nextInt(objets.size())));
            mission.setDestination(destinations.get(random.nextInt(destinations.size())));
            mission.setMotif(faker.lorem().sentence(6));

            // Dates
            LocalDateTime dateDepart = LocalDateTime.now().plusDays(random.nextInt(30)).withHour(8 + random.nextInt(9));
            LocalDateTime dateRetour = dateDepart.plusHours(4 + random.nextInt(12));
            LocalDateTime dateOrdre = LocalDateTime.now().minusDays(random.nextInt(5));

            mission.setDateDepart(dateDepart);
            mission.setDateRetour(dateRetour);
            mission.setDateOrdre(dateOrdre);
            mission.setValidee(random.nextBoolean());

            EtatMission[] etats = EtatMission.values();
            mission.setEtat(etats[random.nextInt(etats.length)]);

            mission.setUtilisateur(utilisateurs.get(random.nextInt(utilisateurs.size())));
            mission.setVehicule(vehicules.get(random.nextInt(vehicules.size())));

            missions.add(mission);
        }

        ordreMissionRepository.saveAll(missions);
        System.out.println("✅ Missions générées : " + missions.size());
    }
}
