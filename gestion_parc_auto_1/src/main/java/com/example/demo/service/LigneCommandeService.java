package com.example.demo.service;



import com.example.demo.model.LigneCommande;

import java.util.List;

public interface LigneCommandeService {
    LigneCommande createLigneCommande(LigneCommande ligneCommande);
    LigneCommande getLigneCommandeById(Long id);
    List<LigneCommande> getAllLigneCommandes();
    LigneCommande updateLigneCommande(Long id, LigneCommande ligneCommande);
    void deleteLigneCommande(Long id);
}
