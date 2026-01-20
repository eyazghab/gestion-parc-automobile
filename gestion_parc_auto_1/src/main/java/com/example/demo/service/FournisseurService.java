package com.example.demo.service;


import com.example.demo.model.Fournisseur;
import java.util.List;

public interface FournisseurService {
    Fournisseur createFournisseur(Fournisseur fournisseur);
    Fournisseur getFournisseurById(Long id);
    List<Fournisseur> getAllFournisseurs();
    Fournisseur updateFournisseur(Long id, Fournisseur fournisseur);
    void deleteFournisseur(Long id);
}
