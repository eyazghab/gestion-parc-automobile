package com.example.demo.DataSeeder;

import com.example.demo.Repository.VehiculeRepository;
import com.example.demo.model.EtatSinistre;
import com.example.demo.model.Sinistre;
import com.example.demo.model.TypeIncident;
import com.example.demo.model.Vehicule;
import com.example.demo.service.SinistreService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;

@Order(13)
@Component
public class DataSeederSinistre implements CommandLineRunner {

    private final SinistreService sinistreService;
    private final VehiculeRepository vehiculeRepository;

    public DataSeederSinistre(SinistreService sinistreService, VehiculeRepository vehiculeRepository) {
        this.sinistreService = sinistreService;
        this.vehiculeRepository = vehiculeRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (!sinistreService.findAll().isEmpty()) {
            return; // ne pas ajouter si déjà existant
        }

        List<Vehicule> vehicules = vehiculeRepository.findAll();
        if (vehicules.isEmpty()) {
            System.out.println("Aucun véhicule trouvé. Aucun sinistre ne sera ajouté.");
            return;
        }

        // 🔹 Types de sinistres possibles (Enum TypeIncident)
        List<TypeIncident> typesIncident = Arrays.asList(TypeIncident.ACCIDENT, TypeIncident.PANNE);

        // 🔹 Lieux possibles
        List<String> lieux = Arrays.asList(
                "Tunis", "Sfax", "Sousse", "Bizerte", "Gabès",
                "Nabeul", "Monastir", "Gafsa", "Mahdia", "Kairouan"
        );

        Random random = new Random();
        Date today = new Date();
        int sinistreCount = Math.min(5, vehicules.size());

        for (int i = 0; i < sinistreCount; i++) {
            Vehicule vehicule = vehicules.get(i);
            Sinistre sinistre = new Sinistre();

            sinistre.setVehicule(vehicule);
            sinistre.setDateSinistre(today);
            sinistre.setHeureSinistre(LocalTime.of(10 + i, 30));
            sinistre.setLieuSinistre(lieux.get(random.nextInt(lieux.size()))); // ✅ lieu aléatoire
            sinistre.setTypeIncident(typesIncident.get(random.nextInt(typesIncident.size()))); // ✅ type aléatoire
            sinistre.setDescription("Sinistre de type " + sinistre.getTypeIncident()
                    + " pour le véhicule " + vehicule.getImmatriculation());
            sinistre.setDegats("Dégâts constatés sur le véhicule " + vehicule.getImmatriculation());
            sinistre.setDateDeclaration(today);
            sinistre.setEtat(EtatSinistre.DECLARE);

            // ✅ Ajout de photos fictives
            sinistre.setPhotos(Arrays.asList(
                    "https://via.placeholder.com/150?text=" + sinistre.getTypeIncident() + "+Photo1",
                    "https://via.placeholder.com/150?text=" + sinistre.getTypeIncident() + "+Photo2"
            ));

            // ✅ Sauvegarde via le service
            sinistreService.saveSinistreEtNotifier(sinistre, null);

            System.out.println("✅ Sinistre ajouté : " + sinistre.getTypeIncident()
                    + " à " + sinistre.getLieuSinistre()
                    + " pour le véhicule " + vehicule.getImmatriculation());
        }
    }
}
