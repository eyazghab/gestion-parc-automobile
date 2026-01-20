/*package com.example.demo.DataSeeder;

import java.util.Date;
import java.util.List;
import java.util.Random;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.example.demo.Repository.LigneMaintenanceRepository;
import com.example.demo.Repository.MaintenanceRepository;
import com.example.demo.Repository.TypeMainRepository;
import com.example.demo.model.LigneMaintenance;
import com.example.demo.model.Maintenance;
import com.example.demo.model.TypeMaintenance;


@Component
public class DataSeederLigneMaintenance implements CommandLineRunner {

    private final LigneMaintenanceRepository ligneMaintenanceRepository;
    private final MaintenanceRepository maintenanceRepository;
    private final TypeMainRepository typeMaintenanceRepository;

    public DataSeederLigneMaintenance(
    		LigneMaintenanceRepository ligneMaintenanceRepository,
            MaintenanceRepository maintenanceRepository,
            TypeMainRepository typeMaintenanceRepository) {
        this.ligneMaintenanceRepository = ligneMaintenanceRepository;
        this.maintenanceRepository = maintenanceRepository;
        this.typeMaintenanceRepository = typeMaintenanceRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (ligneMaintenanceRepository.count() == 0) {
            List<Maintenance> maintenances = maintenanceRepository.findAll();
            List<TypeMaintenance> types = typeMaintenanceRepository.findAll();

            if (maintenances.isEmpty() || types.isEmpty()) {
                System.out.println("Pas de Maintenance ou de TypeMaintenance existant. Seeder annulé.");
                return;
            }

            Random random = new Random();

            for (Maintenance maintenance : maintenances) {
                // On assigne 2 ou 3 lignes par maintenance
                int nombreLignes = random.nextInt(2) + 1; // génère 1 ou 2 lignes

                for (int i = 0; i < nombreLignes; i++) {
                    LigneMaintenance ligne = new LigneMaintenance();
                    ligne.setQuantite(random.nextInt(5) + 1); // entre 1 et 5
                    ligne.setMontant(100 + random.nextDouble() * 900); // entre 100 et 1000
                    ligne.setCout(ligne.getQuantite() * ligne.getMontant());
                    ligne.setDateCreation(new Date());
                    ligne.setDescription("doc_" + maintenance.getIdMaintenance() + "_" + (i+1));
                    ligne.setMaintenance(maintenance);

                    // Associer un type de maintenance au hasard
                    TypeMaintenance randomType = types.get(random.nextInt(types.size()));
                    ligne.setTypeMaintenance(randomType);

                    ligneMaintenanceRepository.save(ligne);
                }
            }

            System.out.println("LigneMaintenance seeding terminé !");
        }
    }
}
*/