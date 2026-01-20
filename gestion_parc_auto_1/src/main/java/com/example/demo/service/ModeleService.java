package com.example.demo.service;

import java.util.List;
import java.util.Optional;

import com.example.demo.model.Modele;

public interface ModeleService {
    Modele saveModele(Modele modele);
    List<Modele> getAllModeles();
    Optional<Modele> getModeleById(Long id);
    Modele updateModele(Long id, Modele modele);
    void deleteModele(Long id);
}
