package com.example.demo.DataSeeder;

import java.time.LocalDate;
import java.util.*;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.example.demo.Repository.MaintenanceRepository;
import com.example.demo.Repository.TechnicienRepository;
import com.example.demo.Repository.VehiculeRepository;
import com.example.demo.Repository.TypeMainRepository;
import com.example.demo.Repository.SuiviRepository;
import com.example.demo.model.LigneMaintenance;
import com.example.demo.model.Maintenance;
import com.example.demo.model.Suivi;
import com.example.demo.model.Technicien;
import com.example.demo.model.TypeMaintenance;
import com.example.demo.model.Vehicule;
import com.example.demo.model.TypeIncident;
import com.example.demo.model.TypeIntervention;

@Component
public class DataSeederMaintenance implements CommandLineRunner {

    private final VehiculeRepository vehiculeRepository;
    private final TechnicienRepository technicienRepository;
    private final MaintenanceRepository maintenanceRepository;
    private final TypeMainRepository typeMaintenanceRepository;
    private final SuiviRepository suiviRepository;

    public DataSeederMaintenance(VehiculeRepository vehiculeRepository,
                                 TechnicienRepository technicienRepository,
                                 MaintenanceRepository maintenanceRepository,
                                 TypeMainRepository typeMaintenanceRepository,
                                 SuiviRepository suiviRepository) {
        this.vehiculeRepository = vehiculeRepository;
        this.technicienRepository = technicienRepository;
        this.maintenanceRepository = maintenanceRepository;
        this.typeMaintenanceRepository = typeMaintenanceRepository;
        this.suiviRepository = suiviRepository;
    }

    @Override
    public void run(String... args) throws Exception {

        // ===== Ajouter des TypeMaintenance si vide =====
        if (typeMaintenanceRepository.count() == 0) {
            TypeMaintenance vidange = new TypeMaintenance("Vidange", "Vidange moteur", 10000, 12);
            TypeMaintenance freins = new TypeMaintenance("Freins", "Contrôle et remplacement des freins", 20000, 24);
            TypeMaintenance pneus = new TypeMaintenance("Pneus", "Remplacement des pneus", 40000, 36);
            typeMaintenanceRepository.saveAll(List.of(vidange, freins, pneus));
            System.out.println("TypeMaintenance ajoutés !");
        }

        List<TypeMaintenance> types = typeMaintenanceRepository.findAll();

        // ===== Créer des Maintenances et Suivis pour 25 véhicules =====
        if (maintenanceRepository.count() == 0) {
            List<Vehicule> vehicules = vehiculeRepository.findAll();
            List<Technicien> techniciens = technicienRepository.findAll();

            if (vehicules.isEmpty() || techniciens.isEmpty()) {
                System.out.println("Aucun véhicule ou technicien existant. Seeder annulé.");
                return;
            }

            // 🔹 Mélanger la liste pour choix aléatoire
            Collections.shuffle(vehicules);
            vehicules = vehicules.subList(0, Math.min(25, vehicules.size()));

            Random random = new Random();
            String[] statuts = {"Planifiée", "En cours", "Terminée"};
            String[] prestataires = {"Garage central", "Garage externe", "Atelier interne"};

            for (Vehicule vehicule : vehicules) {
                // ⚡ Suivi de base
                Suivi suivi = new Suivi();
                suivi.setVehicule(vehicule);
                suivi.setDateSuivi(LocalDate.now());
                suivi.setDescription("Suivi initial du véhicule " + vehicule.getImmatriculation());
                suivi.setKilometreActuel(5000 + random.nextInt(80000));
                suivi.setNombreAccidents(random.nextInt(3));
                suivi.setNombrePannes(random.nextInt(5));
                suivi.setKilometresDepuisVidange(random.nextInt(15000));
                suivi.setKilometresDepuisFreins(random.nextInt(30000));
                suivi.setDureeVieBatterie(24 + random.nextInt(24)); // en mois

                suivi = suiviRepository.save(suivi);

                int countMaintenance = 1 + random.nextInt(3);

                for (int i = 0; i < countMaintenance; i++) {
                    Maintenance maintenance = new Maintenance();
                    maintenance.setVehicule(vehicule);
                    maintenance.setSuivi(suivi);
                    maintenance.setTechnicien(techniciens.get(random.nextInt(techniciens.size())));
                    maintenance.setNumeroMaintenance("MTN-" + random.nextInt(10000));
                    maintenance.setDateEffectuee(LocalDate.now().minusDays(random.nextInt(10)));
                    maintenance.setKilometrage(suivi.getKilometreActuel());
                    maintenance.setPrestataire(prestataires[random.nextInt(prestataires.length)]);
                    maintenance.setCoutPiece(50.0 + random.nextInt(200));
                    maintenance.setCoutExterne(random.nextDouble() * 200);
                    maintenance.setStatut(statuts[random.nextInt(statuts.length)]);
                    maintenance.setObservations("Maintenance générée automatiquement et liée au suivi.");
                    maintenance.setTypeMaintenance(types.get(random.nextInt(types.size())));

                    // Type d’incident aléatoire
                    TypeIncident[] incidents = TypeIncident.values();
                    maintenance.setTypeIncident(incidents[random.nextInt(incidents.length)]);

                    // Si maintenance terminée → ajouter interventions
                    if ("Terminée".equals(maintenance.getStatut())) {
                        TypeIntervention[] interventions = TypeIntervention.values();
                        int nb = 1 + random.nextInt(2);
                        for (int j = 0; j < nb; j++) {
                            maintenance.getTypeIntervention().add(interventions[random.nextInt(interventions.length)]);
                        }
                    }

                    // Lignes maintenance
                    double totalLignes = 0.0;
                    int nombreLignes = 1 + random.nextInt(2);
                    for (int j = 0; j < nombreLignes; j++) {
                        LigneMaintenance ligne = new LigneMaintenance();
                        ligne.setMaintenance(maintenance);
                        ligne.setTypeMaintenance(types.get(random.nextInt(types.size())));
                        ligne.setQuantite(random.nextInt(5) + 1);
                        double montant = 100 + random.nextDouble() * 900;
                        ligne.setMontant(montant);
                        ligne.setCout(ligne.getQuantite() * montant);
                        ligne.setDescription("Ligne_" + (j + 1));
                        ligne.setDateCreation(new Date());

                        maintenance.getLignes().add(ligne);
                        totalLignes += ligne.getCout();
                    }

                    maintenance.setCoutTotal(totalLignes + maintenance.getCoutPiece() + maintenance.getCoutExterne());
                    maintenanceRepository.save(maintenance);
                }
            }

            System.out.println("Suivis + Maintenances (25 véhicules seulement) créés !");
        }
    }
}
