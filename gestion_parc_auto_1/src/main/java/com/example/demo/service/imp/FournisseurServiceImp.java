package com.example.demo.service.imp;

import com.example.demo.Repository.FournisseurRepository;
import com.example.demo.model.Fournisseur;
import com.example.demo.service.FournisseurService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FournisseurServiceImp implements FournisseurService {

    private final FournisseurRepository fournisseurRepository;

    public FournisseurServiceImp(FournisseurRepository fournisseurRepository) {
        this.fournisseurRepository = fournisseurRepository;
    }

    @Override
    public Fournisseur createFournisseur(Fournisseur fournisseur) {
        return fournisseurRepository.save(fournisseur);
    }

    @Override
    public Fournisseur getFournisseurById(Long id) {
        return fournisseurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Fournisseur introuvable"));
    }

    @Override
    public List<Fournisseur> getAllFournisseurs() {
        return fournisseurRepository.findAll();
    }

    @Override
    public Fournisseur updateFournisseur(Long id, Fournisseur fournisseur) {
        Fournisseur existing = getFournisseurById(id);
        existing.setNomFournisseur(fournisseur.getNomFournisseur());
        existing.setAdresse(fournisseur.getAdresse());
        existing.setContact(fournisseur.getContact());
        existing.setTypeService(fournisseur.getTypeService());
        return fournisseurRepository.save(existing);
    }

    @Override
    public void deleteFournisseur(Long id) {
        fournisseurRepository.deleteById(id);
    }
}
