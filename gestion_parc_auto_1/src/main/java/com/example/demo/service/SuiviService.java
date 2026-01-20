package com.example.demo.service;



import com.example.demo.Dto.SuiviDTO;
import com.example.demo.model.Suivi;

import java.util.List;

public interface SuiviService {
    Suivi save(Suivi suivi);
    Suivi findById(Long id);
    List<Suivi> findAll();
    Suivi update(Long id, Suivi suivi);
    void delete(Long id);
    Suivi getSuiviVehicule(Long idVehicule);
    List<String> getAlertesVehicule(Long idVehicule);
    void mettreAJourAlertes(Long vehiculeId);
    Suivi getDernierSuiviVehicule(Long idVehicule);
    String verifierEtatVehicule(Long idVehicule);
    // Méthode pour l'historique d'un véhicule
    List<Suivi> getSuivisParVehicule(Long idVehicule);


}
