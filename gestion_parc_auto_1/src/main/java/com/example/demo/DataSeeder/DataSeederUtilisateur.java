package com.example.demo.DataSeeder;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.example.demo.Repository.UtilisateurRepository;
import com.example.demo.Repository.DepartementRepository;
import com.example.demo.model.Utilisateur;
import com.example.demo.model.Departement;
import com.github.javafaker.Faker;

@Component
@Order(3)
public class DataSeederUtilisateur implements CommandLineRunner {

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private DepartementRepository departementRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final Faker faker = new Faker();
    private final Random random = new Random();
    private final Set<String> generatedCodes = new HashSet<>();

    @Override
    public void run(String... args) {
        utilisateurRepository.deleteAll();

        List<Departement> departements = departementRepository.findAll();
        if (departements.isEmpty()) {
            for (int i = 0; i < 5; i++) {
                Departement d = new Departement();
                d.setNom("Département " + (i + 1));
                departementRepository.save(d);
                departements.add(d);
            }
            System.out.println("Départements générés.");
        }

        // ✅ Création manuelle de l'utilisateur ADMIN
        Utilisateur admin = new Utilisateur();
        admin.setNom("Elit");
        admin.setPrenom("Info");
        admin.setEmail("zghabeya15@gmail.com");
        admin.setMotDePasse(passwordEncoder.encode("admin123"));
        admin.setRole("ADMIN");
        admin.setDepartement(departements.get(0));
        admin.setTelephone("0102030405");
        admin.setCin("12345678");
        admin.setDateEmbauche(LocalDate.now().minusYears(1));
        admin.setCodeVerification(generateVerificationCode());
        admin.setCodeCnam(generateUniqueCodeCnam());

        utilisateurRepository.save(admin);

        // Générer les 99 utilisateurs restants
        for (int i = 0; i < 50; i++) {
            Utilisateur utilisateur = new Utilisateur();
            utilisateur.setNom(faker.name().lastName());
            utilisateur.setPrenom(faker.name().firstName());
            utilisateur.setEmail(faker.internet().emailAddress());
            utilisateur.setMotDePasse(passwordEncoder.encode("password"));
            utilisateur.setRole("UTILISATEUR");
            utilisateur.setDepartement(departements.get(random.nextInt(departements.size())));
            utilisateur.setTelephone(faker.phoneNumber().cellPhone());
            String cin = faker.number().digits(8);
            utilisateur.setCin(cin);
            utilisateur.setDateEmbauche(faker.date()
                    .past(3650, java.util.concurrent.TimeUnit.DAYS)
                    .toInstant()
                    .atZone(java.time.ZoneId.systemDefault())
                    .toLocalDate());
            utilisateur.setCodeVerification(generateVerificationCode());
            utilisateur.setCodeCnam(generateUniqueCodeCnam());

            utilisateurRepository.save(utilisateur);
        }

        System.out.println("1 ADMIN + 50 UTILISATEURS générés.");
    }

    private String generateVerificationCode() {
        int code = (int) (Math.random() * 900000) + 100000;
        return String.valueOf(code);
    }

    // ✅ Code CNAM : exactement 10 caractères alphanumériques (lettres + chiffres)
    private String generateUniqueCodeCnam() {
        String code;
        do {
            code = generateAlphanumericCode(10);
        } while (generatedCodes.contains(code));
        generatedCodes.add(code);
        return code;
    }

    private String generateAlphanumericCode(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(characters.charAt(random.nextInt(characters.length())));
        }
        return sb.toString();
    }
}
