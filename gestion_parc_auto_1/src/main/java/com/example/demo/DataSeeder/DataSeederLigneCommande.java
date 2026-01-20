/*package com.example.demo.DataSeeder;

import com.example.demo.Repository.ArticleRepository;

import com.example.demo.Repository.CommandeRepository;
import com.example.demo.Repository.LigneComRepository;
import com.example.demo.model.Article;
import com.example.demo.model.Commande;
import com.example.demo.model.LigneCommande;


import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Date;
/*import java.util.List;
import java.util.Random;
@Order(16)  // Aprés  Commande 
@Component
public class DataSeederLigneCommande implements CommandLineRunner {

    private final LigneComRepository ligneCommandeRepository;
    private final ArticleRepository articleRepository;
    private final CommandeRepository commandeRepository;

    public DataSeederLigneCommande(LigneComRepository ligneCommandeRepository,
                                ArticleRepository articleRepository,
                                CommandeRepository commandeRepository) {
        this.ligneCommandeRepository = ligneCommandeRepository;
        this.articleRepository = articleRepository;
        this.commandeRepository = commandeRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (ligneCommandeRepository.count() == 0) {
            List<Article> articles = articleRepository.findAll();
            List<Commande> commandes = commandeRepository.findAll();
            Random random = new Random();

            if (articles.isEmpty() || commandes.isEmpty()) {
                System.out.println("Pas d'articles ou de commandes trouvés dans la base. Seeder annulé.");
                return;
            }

            for (int i = 0; i < 10; i++) { // Insère 10 lignes de commandes
                LigneCommande ligneCommande = new LigneCommande();
                ligneCommande.setArticle(articles.get(random.nextInt(articles.size())));
                ligneCommande.setCommande(commandes.get(random.nextInt(commandes.size())));
                ligneCommande.setQuantite(random.nextInt(50) + 1); // quantité entre 1 et 50
                ligneCommande.setQuantiteRecu(random.nextInt(30)); // quantité reçue entre 0 et 30
                ligneCommande.setDescription("Description ligne " + (i+1));
                ligneCommande.setTaux_tva(19.6f); // TVA fixe exemple
                ligneCommande.setRemise(5.0f); // Remise fixe exemple
                ligneCommande.setMontant_ht(random.nextInt(1000) + 100); // montant ht entre 100 et 1100
                ligneCommande.setMontant_ttt((int)(ligneCommande.getMontant_ht() * 1.196)); // montant TTC calculé
                ligneCommande.setDate_livraison_prevue(new Date()); // Date actuelle

                ligneCommandeRepository.save(ligneCommande);
            }

            System.out.println("LigneCommande seeding terminé !");
        }
    }
}
*/