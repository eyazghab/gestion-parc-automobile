package com.example.demo.service;

import java.util.List;

import com.example.demo.model.Entreprise;

public interface EntrepriseService {

    Entreprise createEntreprise(Entreprise entreprise);
    Entreprise getEntrepriseById(Long id);
    List<Entreprise> getAllEntreprises();
    Entreprise updateEntreprise(Long id, Entreprise entreprise);
    void deleteEntreprise(Long id);
}
