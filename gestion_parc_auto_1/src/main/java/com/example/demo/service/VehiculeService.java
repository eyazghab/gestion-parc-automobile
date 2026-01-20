package com.example.demo.service;


import java.time.LocalDateTime;
import java.util.List;

import com.example.demo.model.Vehicule;

public interface VehiculeService {
	Vehicule createVehicule(Vehicule vehicule);
	Vehicule getVehiculeById(Long id);
    List<Vehicule> getAllVehicules();
    Vehicule updateVehicule(Long id, Vehicule vehicule);
    void deleteVehicule(Long id);
    public Vehicule findByIdWithAllRelations(Long id);
    List<Vehicule> getVehiculesDisponibles(LocalDateTime dateDebut, LocalDateTime dateFin);
    List<Vehicule> getVehiculesLibres();
    List<Vehicule> getVehiculesDisponiblesEtReserves();
    Double calculerEtEnregistrerConsommationMoyenne(Long idVehicule);

    
    

}
