package com.example.demo.service.imp;

import com.example.demo.Repository.TechnicienRepository;
import com.example.demo.model.Technicien;
import com.example.demo.service.TechnicienService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TechnicienServiceImp implements TechnicienService {

    @Autowired
    private TechnicienRepository technicienRepository;

    @Override
    public Technicien saveTechnicien(Technicien technicien) {
        return technicienRepository.save(technicien);
    }

    @Override
    public Technicien updateTechnicien(Long id, Technicien technicien) {
        Optional<Technicien> optionalTech = technicienRepository.findById(id);
        if (optionalTech.isPresent()) {
            Technicien existing = optionalTech.get();
            existing.setNom(technicien.getNom());
            existing.setPrenom(technicien.getPrenom());
            existing.setSpecialite(technicien.getSpecialite());
            existing.setEmailTech(technicien.getEmailTech());
            existing.setTelephoneTech(technicien.getTelephoneTech());
            return technicienRepository.save(existing);
        } else {
            throw new RuntimeException("Technicien non trouvé avec id: " + id);
        }
    }

    @Override
    public void deleteTechnicien(Long id) {
        technicienRepository.deleteById(id);
    }

    @Override
    public Technicien getTechnicienById(Long id) {
        return technicienRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Technicien introuvable"));
    }

    @Override
    public List<Technicien> getAllTechniciens() {
        return technicienRepository.findAll();
    }
}
