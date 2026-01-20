package com.example.demo.DataSeeder;
/*
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.example.demo.model.*;
import com.example.demo.model.Facture.EtatFacture;
import com.example.demo.Repository.ArticleRepository;
import com.example.demo.Repository.CommandeRepository;
import com.example.demo.Repository.FournisseurRepository;

@Order(14)
@Configuration
public class DataSeederCommande implements CommandLineRunner {

    private final CommandeRepository commandeRepository;
    private final FournisseurRepository fournisseurRepository;
    private final ArticleRepository articleRepository;

    public DataSeederCommande(CommandeRepository commandeRepository,
                              FournisseurRepository fournisseurRepository,
                              ArticleRepository articleRepository) {
        this.commandeRepository = commandeRepository;
        this.fournisseurRepository = fournisseurRepository;
        this.articleRepository = articleRepository;
    }

    @Override
    public void run(String... args) throws Exception {

        // ⚠️ Vérifier si la table des commandes est vide
        if (commandeRepository.count() > 0) return;

        // 🔹 Vérification des fournisseurs
        long fournisseurCount = fournisseurRepository.count();
        System.out.println("⚡ Fournisseurs existants : " + fournisseurCount);
        if (fournisseurCount == 0) {
            System.out.println("❌ Aucun fournisseur n'existe encore. Assurez-vous que les fournisseurs ont été créés.");
            return;
        }

        // =========================
        // 1️⃣ Récupérer un fournisseur existant
        // =========================
        Fournisseur fournisseur = fournisseurRepository.findAll().stream().findFirst()
                .orElseThrow(() -> new RuntimeException("Aucun fournisseur existant dans la base !"));

        // =========================
        // 2️⃣ Récupérer des articles existants pour ce fournisseur
        // =========================
        List<Article> articles = articleRepository.findAll().stream()
                .filter(a -> a.getFournisseur() != null &&
                             a.getFournisseur().getIdFournisseur().equals(fournisseur.getIdFournisseur()))
                .toList();

        if (articles.isEmpty()) {
            System.out.println("❌ Aucun article existant pour ce fournisseur !");
            return;
        }

        // =========================
        // 3️⃣ Créer une commande
        // =========================
        Commande commande = new Commande();
        commande.setNumeroCommande(1002);
        commande.setDate(new Date());
        commande.setDate_creation(new Date());
        commande.setDate_modification(new Date());
        commande.setCommentaire("Commande test via DataSeeder");
        commande.setFournisseur(fournisseur);

        // ⚡ Initialiser la liste pour éviter NullPointerException
        commande.setLignes(new ArrayList<>());

        // =========================
        // 4️⃣ Créer des lignes de commande
        // =========================
        int quantite = 1;
        for (Article article : articles) {
            LigneCommande ligne = new LigneCommande();
            ligne.setArticle(article);
            ligne.setQuantite(quantite++);
            ligne.setPrixUnitaire(article.getPrix());
            ligne.setCommande(commande);
            commande.getLignes().add(ligne);
        }

        // =========================
        // 5️⃣ Calcul des montants
        // =========================
        int ht = commande.getLignes().stream().mapToInt(l -> l.getPrixUnitaire() * l.getQuantite()).sum();
        int tva = (int) Math.round(ht * 0.19);
        int ttc = ht + tva;

        commande.setMontant_ht(ht);
        commande.setMontant_tva(tva);
        commande.setMontant_ttc(ttc);

        // =========================
        // 6️⃣ Créer une facture associée
        // =========================
        Facture facture = new Facture();
        facture.setNumeroFacture("FAC-1002");
        facture.setDateFacture(new Date());
        facture.setMontantHT(ht);
        facture.setMontantTVA(tva);
        facture.setMontantTTC(ttc);
        facture.setEtat(EtatFacture.NON_PAYEE);
        facture.setCommande(commande);

        commande.setFacture(facture);

        // =========================
        // 7️⃣ Sauvegarder la commande avec lignes et facture
        // =========================
        commandeRepository.save(commande);

        System.out.println("✅ Nouvelle commande créée avec lignes et facture existantes.");
    }
}
*/