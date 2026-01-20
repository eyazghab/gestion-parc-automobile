package com.example.demo.service;

import java.util.List;

import com.example.demo.model.Commande;
import com.example.demo.model.Commande.StatutCommande;
import com.lowagie.text.DocumentException;
import com.lowagie.text.DocumentException;


public interface CommandeService {
    Commande creerCommande(Long fournisseurId, List<Long> articlesIds, String commentaire, int quantiteParArticle);
    Commande changerStatut(Long commandeId, StatutCommande statut);
    Commande creerCommandeAvecQuantites(Long fournisseurId, List<Long> articleIds, List<Integer> quantites, String commentaire);
    Commande updateCommande(Long commandeId, Long fournisseurId, List<Long> articleIds, List<Integer> quantites, String commentaire);
    List<Commande> getCommandes();
    Commande getCommandeById(Long id);
    byte[] generateFacturePDFWithQRCode(Commande commande) throws Exception;// Générer le PDF d'une commande
    void sendFactureEmail(Commande commande);  // Envoyer le PDF par email
    Commande findById(Long id);
    Commande save(Commande commande);
    Commande passerEnCours(Long commandeId);
    void delete(Long commandeId);
    Commande livrerCommande(Long commandeId);

}

