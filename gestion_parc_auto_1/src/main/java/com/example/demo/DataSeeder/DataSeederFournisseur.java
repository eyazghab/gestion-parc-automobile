/*package com.example.demo.DataSeeder;

import com.example.demo.Repository.FournisseurRepository;
import com.example.demo.Repository.ArticleRepository;
import com.example.demo.model.Article;
import com.example.demo.model.Fournisseur;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import java.util.ArrayList;
import java.util.List;

@Order(12)

@Configuration
public class DataSeederFournisseur {

    @Bean
    CommandLineRunner initFournisseurs(FournisseurRepository fournisseurRepository,
                                       ArticleRepository articleRepository) {
        return args -> {
            if (fournisseurRepository.count() == 0) {
                // Créer quelques fournisseurs
                Fournisseur fournisseur1 = new Fournisseur();
                fournisseur1.setNomFournisseur("Fournisseur A");
                fournisseur1.setAdresse("123 rue des Fournisseurs");
                fournisseur1.setContact("contact@fournisseura.com");
                fournisseur1.setTypeService("Pièces de rechange");
                fournisseur1.setArticles(new ArrayList<>());

                Fournisseur fournisseur2 = new Fournisseur();
                fournisseur2.setNomFournisseur("Fournisseur B");
                fournisseur2.setAdresse("456 avenue des Fournisseurs");
                fournisseur2.setContact("contact@fournisseurb.com");
                fournisseur2.setTypeService("Entretien et réparations");
                fournisseur2.setArticles(new ArrayList<>());

                Fournisseur fournisseur3 = new Fournisseur();
                fournisseur3.setNomFournisseur("Fournisseur C");
                fournisseur3.setAdresse("789 boulevard des Fournisseurs");
                fournisseur3.setContact("contact@fournisseurc.com");
                fournisseur3.setTypeService("Vente de véhicules");
                fournisseur3.setArticles(new ArrayList<>());

                // Sauvegarder les fournisseurs d'abord
                fournisseur1 = fournisseurRepository.save(fournisseur1);
                fournisseur2 = fournisseurRepository.save(fournisseur2);
                fournisseur3 = fournisseurRepository.save(fournisseur3);

                // Ajouter des articles pour chaque fournisseur
                List<Article> articles = new ArrayList<>();

                Article article1 = new Article();
                article1.setReference("REF-A1");
                article1.setNom("Huile moteur 5W30");
                article1.setDescription("Huile synthétique haute performance");
                article1.setPrix(30);
                article1.setImageUrl("http://localhost:8090/images/huile.jpg");
                article1.setFournisseur(fournisseur1);
                fournisseur1.getArticles().add(article1);

                Article article2 = new Article();
                article2.setReference("REF-A2");
                article2.setNom("Filtre à air");
                article2.setDescription("Filtre à air standard");
                article2.setPrix(15);
                article2.setImageUrl("http://localhost:8090/images/filtre.jpg");
                article2.setFournisseur(fournisseur1);
                fournisseur1.getArticles().add(article2);

                Article article3 = new Article();
                article3.setReference("REF-B1");
                article3.setNom("Batterie 12V 60Ah");
                article3.setDescription("Batterie auto longue durée");
                article3.setPrix(120);
                article3.setImageUrl("http://localhost:8090/images/batterie.jpg");
                article3.setFournisseur(fournisseur2);
                fournisseur2.getArticles().add(article3);

                Article article4 = new Article();
                article4.setReference("REF-C1");
                article4.setNom("Pneu 195/65 R15");
                article4.setDescription("Pneu toutes saisons");
                article4.setPrix(80);
                article4.setImageUrl("http://localhost:8090/images/pneu.jpg");
                article4.setFournisseur(fournisseur3);
                fournisseur3.getArticles().add(article4);

                // Sauvegarde des articles
                articles.add(article1);
                articles.add(article2);
                articles.add(article3);
                articles.add(article4);

                articleRepository.saveAll(articles);

                System.out.println("✅ Fournisseurs et articles ajoutés dans la base de données.");
            }
        };
    }
}
*/