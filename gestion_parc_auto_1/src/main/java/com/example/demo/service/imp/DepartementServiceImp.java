package com.example.demo.service.imp;



import com.example.demo.Repository.DepartementRepository;
import com.example.demo.model.Departement;
import com.example.demo.service.DepartementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DepartementServiceImp implements DepartementService {

    @Autowired
    private DepartementRepository departementRepository;
   
   
    @Override
    public Departement createDepartement(Departement departement) {
        return departementRepository.save(departement);
    }

    @Override
    public Departement getDepartementById(Long id) {
        return departementRepository.findById(id).orElse(null);
    }

    @Override
    public List<Departement> getAllDepartements() {
        return departementRepository.findAll();
    }

    @Override
    public Departement updateDepartement(Long id, Departement departement) {
        Optional<Departement> existing = departementRepository.findById(id);
        if (existing.isPresent()) {
            departement.setId(id);
            return departementRepository.save(departement);
        }
        return null;
    }

    @Override
    public void deleteDepartement(Long id) {
        Departement departement = departementRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Département introuvable"));

        if (!departement.getUtilisateurs().isEmpty()) {
            throw new IllegalStateException("Impossible de supprimer ce département : des utilisateurs y sont encore associés.");
        }

        departementRepository.delete(departement);
    }
}
