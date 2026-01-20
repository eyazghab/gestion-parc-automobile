package com.example.demo.service;

import java.util.List;
import java.util.Optional;

import com.example.demo.model.LigneMaintenance;

public interface LigneMaintenanceService {
    LigneMaintenance save(LigneMaintenance ligneMaintenance);
    List<LigneMaintenance> getAll();
    Optional<LigneMaintenance> getById(Long id);
    LigneMaintenance update(Long id, LigneMaintenance ligneMaintenance);
    void delete(Long id);
}
