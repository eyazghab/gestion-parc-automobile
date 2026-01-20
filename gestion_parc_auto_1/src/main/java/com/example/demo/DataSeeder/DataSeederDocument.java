package com.example.demo.DataSeeder;

import com.example.demo.Repository.DocumentRepository;
import com.example.demo.Repository.VehiculeRepository;
import com.example.demo.model.Document;
import com.example.demo.model.Vehicule;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;
@Order(10)
@Component
public class DataSeederDocument implements CommandLineRunner {

    private final DocumentRepository documentRepository;
    private final VehiculeRepository vehiculeRepository;

    public DataSeederDocument(DocumentRepository documentRepository, VehiculeRepository vehiculeRepository) {
        this.documentRepository = documentRepository;
        this.vehiculeRepository = vehiculeRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (documentRepository.count() == 0) {
            List<Vehicule> vehicules = vehiculeRepository.findAll();

            if (vehicules.isEmpty()) {
                System.out.println("Aucun véhicule trouvé. Impossible d'ajouter des documents liés.");
                return;
            }

            for (int i = 0; i < vehicules.size(); i++) {
            	Vehicule v = vehicules.get(i);

                Document doc = new Document();
                doc.setType("Carte Grise");
                doc.setVehicule(v); // associer le véhicule existant

                documentRepository.save(doc);
            }

            System.out.println("Documents créés pour chaque véhicule existant.");
        }
    }
}
