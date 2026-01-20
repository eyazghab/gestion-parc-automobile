package com.example.demo.service.imp;


import com.example.demo.Repository.LigneComRepository;
import com.example.demo.model.LigneCommande;
import com.example.demo.service.LigneCommandeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LigneCommandeServiceImp implements LigneCommandeService {

    @Autowired
    private LigneComRepository ligneCommandeRepository;

    @Override
    public LigneCommande createLigneCommande(LigneCommande ligneCommande) {
        return ligneCommandeRepository.save(ligneCommande);
    }

    @Override
    public LigneCommande getLigneCommandeById(Long id) {
        return ligneCommandeRepository.findById(id).orElse(null);
    }

    @Override
    public List<LigneCommande> getAllLigneCommandes() {
        return ligneCommandeRepository.findAll();
    }

    @Override
    public LigneCommande updateLigneCommande(Long id, LigneCommande ligneCommande) {
        Optional<LigneCommande> existing = ligneCommandeRepository.findById(id);
        if (existing.isPresent()) {
            ligneCommande.setId(id);
            return ligneCommandeRepository.save(ligneCommande);
        }
        return null;
    }

    @Override
    public void deleteLigneCommande(Long id) {
        ligneCommandeRepository.deleteById(id);
    }
}
