package com.example.demo.service;


import com.example.demo.model.Circulation;
import com.example.demo.model.EtatSinistre;
import com.example.demo.model.Sinistre;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public interface SinistreService {
    Sinistre saveSinistreEtNotifier(Sinistre sinistre, MultipartFile[] files);
    Sinistre findById(Long id);
    List<Sinistre> findAll();
    Sinistre update(Long id, Sinistre sinistre);
    void delete(Long id);
     List<Sinistre> getAllSinistresWithVehicule();
     Sinistre changerEtat(Long id, EtatSinistre nouvelEtat);
     Sinistre prendreEnCharge(Long idSinistre, Long idTechnicien, Circulation circulation);
     List<Sinistre> findByEtat(EtatSinistre etat);

}
