package com.example.demo.service.imp;


import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.Repository.TypeMainRepository;
import com.example.demo.model.TypeMaintenance;
import com.example.demo.service.TypeMaintenanceService;

@Service
@Transactional
public class TypeMaintenanceServiceImpl implements TypeMaintenanceService {

    private final TypeMainRepository typeMainRepository;

    public TypeMaintenanceServiceImpl(TypeMainRepository typeMainRepository) {
        this.typeMainRepository = typeMainRepository;
    }

    @Override
    public List<TypeMaintenance> getAllActif() {
        return typeMainRepository.findAllByActifTrue();
    }

    @Override
    public Optional<TypeMaintenance> findByLibelle(String libelle) {
        return typeMainRepository.findByLibelleIgnoreCaseAndActifTrue(libelle);
    }

    @Override
    public TypeMaintenance save(TypeMaintenance typeMaintenance) {
        return typeMainRepository.save(typeMaintenance);
    }
}
