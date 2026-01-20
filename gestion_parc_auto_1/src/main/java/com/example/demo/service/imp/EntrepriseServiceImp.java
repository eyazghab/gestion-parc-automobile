package com.example.demo.service.imp;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.Repository.EntrepriseRepository;
import com.example.demo.model.Entreprise;
import com.example.demo.service.EntrepriseService;

@Service
public class EntrepriseServiceImp implements EntrepriseService {

    @Autowired
    private EntrepriseRepository entrepriseRepository;

    @Override
    public Entreprise createEntreprise(Entreprise entreprise) {
        return entrepriseRepository.save(entreprise);
    }

    @Override
    public Entreprise getEntrepriseById(Long id) {
        return entrepriseRepository.findById(id).orElse(null);
    }

    @Override
    public List<Entreprise> getAllEntreprises() {
        return entrepriseRepository.findAll();
    }

    @Override
    public Entreprise updateEntreprise(Long id, Entreprise entreprise) {
        Optional<Entreprise> existing = entrepriseRepository.findById(id);
        if (existing.isPresent()) {
            entreprise.setId(id);
            return entrepriseRepository.save(entreprise);
        }
        return null;
    }

    @Override
    public void deleteEntreprise(Long id) {
        entrepriseRepository.deleteById(id);
    }
}