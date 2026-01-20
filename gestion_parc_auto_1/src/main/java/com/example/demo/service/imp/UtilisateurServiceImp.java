package com.example.demo.service.imp;
/*


import com.example.demo.Repository.UtilisateurRepository;
import com.example.demo.model.Utilisateur;
import com.example.demo.service.UtilisateurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UtilisateurServiceImp implements UtilisateurService {

	@Autowired
    private UtilisateurRepository utilisateurRepository;

    @Override
    public Utilisateur createUtilisateur(Utilisateur utilisateur) {
        return utilisateurRepository.save(utilisateur);
    }

    @Override
    public Utilisateur getUtilisateurById(Long id) {
        return utilisateurRepository.findById(id).orElse(null);
    }

    @Override
    public List<Utilisateur> getAllUtilisateurs() {
        return utilisateurRepository.findAll();
    }

    @Override
    public Utilisateur updateUtilisateur(Long id, Utilisateur utilisateur) {
        Optional<Utilisateur> existing = utilisateurRepository.findById(id);
        if (existing.isPresent()) {
            utilisateur.setIdUtilisateur(id);
            return utilisateurRepository.save(utilisateur);
        }
        return null;
    }

    @Override
    public void deleteUtilisateur(Long id) {
        utilisateurRepository.deleteById(id);
    }
    
}*/