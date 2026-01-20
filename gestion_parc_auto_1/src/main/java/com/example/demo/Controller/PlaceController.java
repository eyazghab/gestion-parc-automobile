package com.example.demo.Controller;



import com.example.demo.Repository.ParkingRepository;
import com.example.demo.Repository.PlaceRepository;
import com.example.demo.Repository.VehiculeRepository;
import com.example.demo.model.Parking;
import com.example.demo.model.Place;
import com.example.demo.model.Vehicule;
import com.example.demo.service.PlaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/places")
@CrossOrigin("*")
public class PlaceController {

    @Autowired
    private PlaceService placeService;
    @Autowired
    private PlaceRepository placeRepository;
    @Autowired
    private ParkingRepository parkingRepository;
    @Autowired
    private VehiculeRepository vehiculeRepository;
    
    @PostMapping
public Place create(@RequestBody Map<String, Object> payload) {
    Place place = new Place();
    place.setNom((String) payload.get("nom"));
    
    // Assurer la conversion de numeroPlace
    Object numeroPlaceObj = payload.get("numeroPlace");
    if (numeroPlaceObj instanceof Integer) {
        place.setNumeroPlace((Integer) numeroPlaceObj);
    } else if (numeroPlaceObj instanceof Number) {
        place.setNumeroPlace(((Number) numeroPlaceObj).intValue());
    }

    place.setEstOccupee((Boolean) payload.get("estOccupee"));

    // Récupérer l'id du parking depuis l'objet "parking"
    Map<String, Object> parkingMap = (Map<String, Object>) payload.get("parking");
    if (parkingMap != null) {
        Long parkingId = Long.valueOf(parkingMap.get("idParking").toString());
        Parking parking = parkingRepository.findById(parkingId)
                           .orElseThrow(() -> new RuntimeException("Parking introuvable"));
        place.setParking(parking);
    }

    // Vehicule si présent
    Map<String, Object> vehiculeMap = (Map<String, Object>) payload.get("vehicule");
    if (vehiculeMap != null) {
        Long vehiculeId = Long.valueOf(vehiculeMap.get("idVehicule").toString());
        Vehicule vehicule = vehiculeRepository.findById(vehiculeId).orElse(null);
        place.setVehicule(vehicule);
    }

    return placeService.save(place);
}



    @GetMapping("/{id}")
    public Place getById(@PathVariable Long id) {
        return placeService.findById(id);
    }

    @GetMapping
    public List<Place> getAll() {
        return placeService.findAll();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Place> updatePlace(@PathVariable Long id, @RequestBody Place updatedPlace) {
        Optional<Place> optionalPlace = placeRepository.findById(id);
        if (optionalPlace.isPresent()) {
            Place existingPlace = optionalPlace.get();
            existingPlace.setEstOccupee(updatedPlace.getEstOccupee());
            // Tu peux mettre à jour d'autres champs si nécessaire
            return ResponseEntity.ok(placeRepository.save(existingPlace));
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        placeService.delete(id);
    }
 // Toggle occupation
    @PutMapping("/{id}/toggleOccupancy")
    public ResponseEntity<Place> toggleOccupancy(@PathVariable Long id) {
        Place place = placeService.findById(id);
        if (place == null) {
            return ResponseEntity.notFound().build();
        }
        place.setEstOccupee(!place.getEstOccupee());
        placeService.save(place);
        return ResponseEntity.ok(place);
    }


}
