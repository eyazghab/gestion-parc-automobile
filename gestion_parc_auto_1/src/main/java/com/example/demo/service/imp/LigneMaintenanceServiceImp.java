package com.example.demo.service.imp;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.demo.Repository.LigneMaintenanceRepository;
import com.example.demo.model.LigneMaintenance;
import com.example.demo.service.LigneMaintenanceService;

@Service
public class LigneMaintenanceServiceImp implements LigneMaintenanceService {

    private final LigneMaintenanceRepository ligneMaintenanceRepository;

    public LigneMaintenanceServiceImp(LigneMaintenanceRepository ligneMaintenanceRepository) {
        this.ligneMaintenanceRepository = ligneMaintenanceRepository;
    }

    @Override
    public LigneMaintenance save(LigneMaintenance ligneMaintenance) {
        return ligneMaintenanceRepository.save(ligneMaintenance);
    }

    @Override
    public List<LigneMaintenance> getAll() {
        return ligneMaintenanceRepository.findAll();
    }

    @Override
    public Optional<LigneMaintenance> getById(Long id) {
        return ligneMaintenanceRepository.findById(id);
    }

    @Override
    public LigneMaintenance update(Long id, LigneMaintenance ligneMaintenance) {
        return ligneMaintenanceRepository.findById(id).map(existing -> {
            existing.setQuantite(ligneMaintenance.getQuantite());
            existing.setMontant(ligneMaintenance.getMontant());
            existing.setCout(ligneMaintenance.getCout());
            existing.setDateCreation(ligneMaintenance.getDateCreation());
            existing.setDescription(ligneMaintenance.getDescription());
            existing.setMaintenance(ligneMaintenance.getMaintenance());
            existing.setTypeMaintenance(ligneMaintenance.getTypeMaintenance());
            return ligneMaintenanceRepository.save(existing);
        }).orElseThrow(() -> new RuntimeException("LigneMaintenance non trouvée avec id: " + id));
    }

    @Override
    public void delete(Long id) {
        ligneMaintenanceRepository.deleteById(id);
    }
}
