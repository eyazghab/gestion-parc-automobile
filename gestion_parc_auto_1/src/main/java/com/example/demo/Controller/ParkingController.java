package com.example.demo.Controller;



import com.example.demo.Repository.ParkingRepository;
import com.example.demo.model.Parking;
import com.example.demo.service.ParkingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/parkings")
@CrossOrigin("*")
public class ParkingController {

    @Autowired
    private ParkingService parkingService;
    
    @Autowired
    private ParkingRepository parkingRepository;
    
    @PostMapping
    public Parking createParking(@RequestBody Parking parking) {
        return parkingService.saveParking(parking);
    }
   

    @GetMapping("/{id}")
    public Parking getParking(@PathVariable Long id) {
        return parkingService.getParkingById(id);
    }

    @GetMapping
    public List<Parking> getAllParkings() {
        return parkingService.getAllParkings();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Parking> modifierParking(@PathVariable Long id, @RequestBody Parking parkingModifie) {
        return parkingRepository.findById(id)
            .map(p -> {
                p.setNomParking(parkingModifie.getNomParking());
                p.setAdresse(parkingModifie.getAdresse());
                p.setCapacite(parkingModifie.getCapacite());
                p.setEstDisponible(parkingModifie.getEstDisponible());
                return new ResponseEntity<>(parkingRepository.save(p), HttpStatus.OK);
            })
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/{id}")
    public void deleteParking(@PathVariable Long id) {
        parkingService.deleteParking(id);
    }
}
