package com.example.demo.service.imp;


import com.example.demo.Repository.PlaceRepository;
import com.example.demo.model.Place;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PlaceServiceImp  {

/*
    @Autowired
    private PlaceRepository placeRepository;

    // ✅ Créer une nouvelle place
    public Place create(Place place) {
        return placeRepository.save(place);
    }

    // ✅ Récupérer toutes les places
    public List<Place> findAll() {
        return placeRepository.findAll();
    }

    // ✅ Mettre à jour une place existante
    public Place update(Long id, Place place) {
        Optional<Place> existingPlaceOpt = placeRepository.findById(id);

        if (existingPlaceOpt.isPresent()) {
            Place existingPlace = existingPlaceOpt.get();

            // Mets à jour les champs nécessaires
            existingPlace.setNom(place.getNom());
            existingPlace.setEstOccupee(place.getEstOccupee());

            return placeRepository.save(existingPlace);
        } else {
            throw new RuntimeException("Place avec id " + id + " non trouvée.");
        }
    }

    // ✅ Supprimer une place
    public void delete(Long id) {
        if (!placeRepository.existsById(id)) {
            throw new RuntimeException("Place avec id " + id + " non trouvée.");
        }
        placeRepository.deleteById(id);
    }*/
}