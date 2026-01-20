package com.example.demo.service;


import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.demo.model.TypeMaintenance;
@Service
public interface TypeMaintenanceService {
    List<TypeMaintenance> getAllActif();
    Optional<TypeMaintenance> findByLibelle(String libelle);
    TypeMaintenance save(TypeMaintenance typeMaintenance);
}
