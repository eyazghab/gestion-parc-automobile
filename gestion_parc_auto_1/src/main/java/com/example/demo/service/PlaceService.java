package com.example.demo.service;


import com.example.demo.Repository.PlaceRepository;
import com.example.demo.model.Place;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PlaceService {
    @Autowired
    private PlaceRepository placeRepository;

    public Place findById(Long id) {
        return placeRepository.findById(id).orElse(null);
    }

    public Place save(Place place) {
        return placeRepository.save(place);
    }
    
 // Récupère toutes les places
    public List<Place> findAll() {
        return placeRepository.findAll();
    }

    // Met à jour une place
    public Place update(Long id, Place place) {
        Optional<Place> existingPlaceOpt = placeRepository.findById(id);

        if (existingPlaceOpt.isPresent()) {
            Place existingPlace = existingPlaceOpt.get();
            // Mets à jour les champs (ajuste selon ton modèle)
            existingPlace.setNom(place.getNom());
            existingPlace.setEstOccupee(place.getEstOccupee());
            return placeRepository.save(existingPlace);
        } else {
            throw new RuntimeException("Place avec id " + id + " non trouvée");
        }
    }

    // Supprime une place
    public void delete(Long id) {
        if (!placeRepository.existsById(id)) {
            throw new RuntimeException("Place avec id " + id + " non trouvée");
        }
        placeRepository.deleteById(id);
    }
}
