package com.example.demo.DataSeeder;

import com.example.demo.Repository.TechnicienRepository;
import com.example.demo.model.Technicien;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Order(11)
@Component
public class DataSeederTechnicien implements CommandLineRunner {

    private final TechnicienRepository technicienRepository;

    public DataSeederTechnicien(TechnicienRepository technicienRepository) {
        this.technicienRepository = technicienRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (technicienRepository.count() > 0) {
            System.out.println("✔️ Techniciens déjà insérés.");
            return;
        }

        List<Technicien> techniciens = Arrays.asList(
                createTechnicien("Durand", "Marc", "Mécanique générale", "marc.durand@garage.fr", "0612345678", Technicien.TypeTechnicien.INTERNE),
                createTechnicien("Lemoine", "Claire", "Électricité auto", "claire.lemoine@garage.fr", "0623456789", Technicien.TypeTechnicien.EXTERNE),
                createTechnicien("Nguyen", "Théo", "Carrosserie", "theo.nguyen@garage.fr", "0634567890", Technicien.TypeTechnicien.INTERNE),
                createTechnicien("Benoît", "Sarah", "Peinture", "sarah.benoit@garage.fr", "0645678901", Technicien.TypeTechnicien.EXTERNE),
                createTechnicien("Eya", "Technicien", "Diagnostic électronique", "zghaya30@gmail.com", "0656789012", Technicien.TypeTechnicien.INTERNE)
        );

        technicienRepository.saveAll(techniciens);
        System.out.println("✅ " + techniciens.size() + " techniciens insérés avec succès !");
    }

    private Technicien createTechnicien(String nom, String prenom, String specialite, String email, String telephone, Technicien.TypeTechnicien type) {
        Technicien t = new Technicien();
        t.setNom(nom);
        t.setPrenom(prenom);
        t.setSpecialite(specialite);
        t.setEmailTech(email);
        t.setTelephoneTech(telephone);
        t.setType(type);
        return t;
    }
}
