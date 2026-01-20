package com.example.demo.DataSeeder;

import com.example.demo.model.*;
import com.example.demo.service.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import java.util.ArrayList;
import java.util.List;

@Configuration
@Order(12)
public class FullDataSeeder {

    private final FournisseurService fournisseurService;
    private final ArticleService articleService;
    private final DepotService depotService;
    private final StockService stockService;
    private final CommandeService commandeService;
    private final EmailService emailService;

    public FullDataSeeder(FournisseurService fournisseurService,
                          ArticleService articleService,
                          DepotService depotService,
                          StockService stockService,
                          CommandeService commandeService,
                          EmailService emailService) {
        this.fournisseurService = fournisseurService;
        this.articleService = articleService;
        this.depotService = depotService;
        this.stockService = stockService;
        this.commandeService = commandeService;
        this.emailService = emailService;
    }

    @Bean
    public CommandLineRunner initAll() {
        return args -> {

            if (!fournisseurService.getAllFournisseurs().isEmpty()) return;

            // ----- 1️⃣ Création des fournisseurs -----
            Fournisseur fournisseurA = fournisseurService.createFournisseur(
                    new Fournisseur("Fournisseur A", "123 rue des Fournisseurs", "contact@fournisseura.com", "Pièces de rechange", new ArrayList<>())
            );
            Fournisseur fournisseurB = fournisseurService.createFournisseur(
                    new Fournisseur("Fournisseur B", "456 avenue des Fournisseurs", "contact@fournisseurb.com", "Entretien et réparations", new ArrayList<>())
            );
            Fournisseur fournisseurC = fournisseurService.createFournisseur(
                    new Fournisseur("Fournisseur C", "789 boulevard des Fournisseurs", "zghaya30@gmail.com", "Vente de véhicules", new ArrayList<>())
            );

            // ----- 2️⃣ Création des articles -----
            Article huileMoteur = articleService.saveArticle(new Article("REF-A1", "Huile Moteur 5W30", "Huile synthétique haute performance", 30, "http://localhost:8090/images/huile.jpg", fournisseurA));
            Article filtreAir = articleService.saveArticle(new Article("REF-A2", "Filtre à air", "Filtre à air standard", 15, "http://localhost:8090/images/Filtreairstandard.jpg", fournisseurA));
            Article huileBoite = articleService.saveArticle(new Article("REF-A3", "Huile Boîte de vitesse", "Huile spéciale boîte auto", 25, "http://localhost:8090/images/HuileMoteur5W30.jpg", fournisseurA));

            Article batterie = articleService.saveArticle(new Article("REF-B1", "Batterie 12V 60Ah", "Batterie auto longue durée", 120, "http://localhost:8090/images/batterie.jpg", fournisseurB));
            Article ampoule = articleService.saveArticle(new Article("REF-B2", "Ampoule H7 55W", "Ampoule halogène pour phare avant", 10, "http://localhost:8090/images/ampouleh7.jpg", fournisseurB));
            Article liquideFrein = articleService.saveArticle(new Article("REF-B3", "Liquide de frein DOT4", "Liquide haute performance", 12, "http://localhost:8090/images/HUI001.jpg", fournisseurB));

            Article pneu = articleService.saveArticle(new Article("REF-C1", "Pneu 195/65 R15", "Pneu toutes saisons", 80, "http://localhost:8090/images/Pneu195.jpg", fournisseurC));
            Article retroviseur = articleService.saveArticle(new Article("REF-C2", "Rétroviseur gauche", "Rétroviseur pour voiture compacte", 50, "http://localhost:8090/images/téléchargement (3).jpg", fournisseurC));
            Article kitEssuieGlace = articleService.saveArticle(new Article("REF-C3", "Kit essuie-glace", "Essuie-glace avant et arrière", 35, "http://localhost:8090/images/Kit essuie-glace.jpg", fournisseurC));
            Article antenne = articleService.saveArticle(new Article("REF-C4", "Antenne voiture", "Antenne radio universelle", 15, "http://localhost:8090/images/Antenne.jpg", fournisseurC));

            // ----- 3️⃣ Création des dépôts -----
            Depot depotCentral = depotService.saveDepot(new Depot("Dépôt Central", "Tunis"));
            Depot depotSud = depotService.saveDepot(new Depot("Dépôt Sud", "Sfax"));

            // ----- 4️⃣ Entrée initiale de stock -----
            stockService.entreeStock(depotCentral.getIdDepot(), huileMoteur.getIdArticle(), 100, "Stock initial");
            stockService.entreeStock(depotCentral.getIdDepot(), filtreAir.getIdArticle(), 200, "Stock initial");
            stockService.entreeStock(depotCentral.getIdDepot(), huileBoite.getIdArticle(), 50, "Stock initial");

            stockService.entreeStock(depotSud.getIdDepot(), batterie.getIdArticle(), 50, "Stock initial");
            stockService.entreeStock(depotSud.getIdDepot(), ampoule.getIdArticle(), 100, "Stock initial");
            stockService.entreeStock(depotSud.getIdDepot(), liquideFrein.getIdArticle(), 30, "Stock initial");

            stockService.entreeStock(depotCentral.getIdDepot(), pneu.getIdArticle(), 40, "Stock initial");
            stockService.entreeStock(depotSud.getIdDepot(), retroviseur.getIdArticle(), 20, "Stock initial");

            // ----- 5️⃣ Création des commandes -----
            List<Long> articlesFournisseurA = List.of(huileMoteur.getIdArticle(), filtreAir.getIdArticle());
            List<Integer> qtesA = List.of(10, 20);
            Commande commandeA = commandeService.creerCommandeAvecQuantites(fournisseurA.getIdFournisseur(), articlesFournisseurA, qtesA, "Commande automatique fournisseur A");

            List<Long> articlesFournisseurB = List.of(batterie.getIdArticle());
            List<Integer> qtesB = List.of(5);
            Commande commandeB = commandeService.creerCommandeAvecQuantites(fournisseurB.getIdFournisseur(), articlesFournisseurB, qtesB, "Commande automatique fournisseur B");

            // ----- 6️⃣ Envoi d'emails -----
            emailService.sendCommandeEmail(commandeA);
            emailService.sendCommandeEmail(commandeB);

            System.out.println("✅ Base initialisée : fournisseurs, articles, stock et commandes/factures créés.");
        };
    }
}
