package com.example.demo.cnam;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.example.demo.RepositoryCnam.CodeCnamRepository;

import java.util.Random;

@Component
public class CnamDataSeeder implements CommandLineRunner {

    private final CodeCnamRepository codeCnamRepository;

    public CnamDataSeeder(CodeCnamRepository codeCnamRepository) {
        this.codeCnamRepository = codeCnamRepository;
    }

    @Override
    public void run(String... args) {
        if (codeCnamRepository.count() == 0) {
            for (int i = 0; i < 20; i++) {  // créer 20 codes CNAM
                String code = generateRandomCnamCode(10);
                CodeCnam cnam = new CodeCnam();
                cnam.setCode(code);
                codeCnamRepository.save(cnam);
            }
            System.out.println("✅ Codes CNAM insérés !");
        }
    }

    private String generateRandomCnamCode(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random rnd = new Random();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(rnd.nextInt(chars.length())));
        }
        return sb.toString();
    }
}
