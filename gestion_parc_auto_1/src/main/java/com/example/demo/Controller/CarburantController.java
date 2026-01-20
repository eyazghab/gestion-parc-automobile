package com.example.demo.Controller;


import com.example.demo.Dto.MissionCarburantDTO;
import com.example.demo.Dto.UtilisateurAvecBonsDTO;
import com.example.demo.Repository.VehiculeRepository;
import com.example.demo.model.Carburant;
import com.example.demo.model.Vehicule;
import com.example.demo.service.CarburantService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/carburants")
@CrossOrigin(origins = "*")
public class CarburantController {

    private final CarburantService carburantService;
    private final VehiculeRepository vehiculeRepository;

    public CarburantController(CarburantService carburantService,VehiculeRepository vehiculeRepository) {
        this.carburantService = carburantService;
        this.vehiculeRepository=vehiculeRepository;
    }

    @GetMapping
    public List<Carburant> getAllCarburants() {
        return carburantService.getAllCarburants();
    }

    @GetMapping("/{id}")
    public Carburant getCarburantById(@PathVariable Long id) {
        return carburantService.getCarburantById(id);
    }

    @PostMapping
    public Carburant addCarburant(@RequestBody Carburant carburant) {
        return carburantService.addCarburant(carburant);
    }

    @DeleteMapping("/{id}")
    public void deleteCarburant(@PathVariable Long id) {
        carburantService.deleteCarburant(id);
    }
    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/missions-carburant/{vehiculeId}")
    public List<MissionCarburantDTO> getMissionsCarburant(@PathVariable Long vehiculeId) {
        Vehicule v = vehiculeRepository.findById(vehiculeId)
                      .orElseThrow(() -> new RuntimeException("Véhicule introuvable"));
        return carburantService.getMissionsAvecCarburant(v);
    }
    @GetMapping("/utilisateurs-bons")
    public List<UtilisateurAvecBonsDTO> getUtilisateursAvecBons() {
        return carburantService.getBonsParUtilisateur();
    }
}

