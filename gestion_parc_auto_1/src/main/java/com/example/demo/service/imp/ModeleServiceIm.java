package com.example.demo.service.imp;

import com.example.demo.Repository.ModeleRepository;
import com.example.demo.model.Modele;
import com.example.demo.service.ModeleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ModeleServiceIm implements ModeleService {

    @Autowired
    private ModeleRepository modeleRepository;

    @Override
    public Modele saveModele(Modele modele) {
        return modeleRepository.save(modele);
    }

    @Override
    public List<Modele> getAllModeles() {
        return modeleRepository.findAll();
    }

    @Override
    public Optional<Modele> getModeleById(Long id) {
        return modeleRepository.findById(id);
    }

    @Override
    public Modele updateModele(Long id, Modele updatedModele) {
        Optional<Modele> optional = modeleRepository.findById(id);
        if (optional.isPresent()) {
        	Modele existing = optional.get();
            existing.setNom(updatedModele.getNom());
            return modeleRepository.save(existing);
        } else {
            throw new RuntimeException("Modele not found with id: " + id);
        }
    }

    @Override
    public void deleteModele(Long id) {
        modeleRepository.deleteById(id);
    }
}
