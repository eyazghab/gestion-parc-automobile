package com.example.demo.DataSeeder;

import com.example.demo.Repository.AssuranceReposittory;
import com.example.demo.Repository.VehiculeRepository;
import com.example.demo.model.Assurance;
import com.example.demo.model.Vehicule;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Calendar;

@Component
public class DataSeederAssurance implements CommandLineRunner {

    private final AssuranceReposittory assuranceRepository;
    private final VehiculeRepository vehiculeRepository;

    public DataSeederAssurance(AssuranceReposittory assuranceRepository, VehiculeRepository vehiculeRepository) {
        this.assuranceRepository = assuranceRepository;
        this.vehiculeRepository = vehiculeRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (assuranceRepository.count() == 0) {
            List<Vehicule> vehicules = vehiculeRepository.findAll();

            if (vehicules.isEmpty()) {
                System.out.println("Aucun véhicule trouvé, aucune assurance créée.");
                return;
            }

            Random random = new Random();

            for (Vehicule vehicule : vehicules) {
                Assurance assurance = new Assurance();
                assurance.setVehicule(vehicule);
                assurance.setNomAssurance("Assurance AutoPro");
                assurance.setCompagnie("Compagnie " + (char) (random.nextInt(26) + 'A'));
                assurance.setTypeAssurance("Tous Risques");
                assurance.setMontantPrime(450.00 + random.nextDouble() * 200);
                assurance.setTelephone("0555-000-" + (100 + random.nextInt(900)));
                assurance.setEmail("contact@assurance.com");
                assurance.setCommantaire("Assurance annuelle classique.");
                assurance.setNumeroPolice("POL" + random.nextInt(100000));

                // Début aujourd’hui
                Date today = new Date();
                assurance.setDateDebut(today);

                // Fin dans un an
                Calendar cal = Calendar.getInstance();
                cal.setTime(today);
                cal.add(Calendar.YEAR, 1);
                assurance.setDateFin(cal.getTime());

                assuranceRepository.save(assurance);
            }

            System.out.println("Assurances créées pour tous les véhicules existants.");
        }
    }
}
