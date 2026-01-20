package com.example.demo.Controller;

import com.example.demo.Dto.PredictionDTO;
import com.example.demo.Repository.MaintenanceRepository;
import com.example.demo.Repository.ModeleRepository;
import com.example.demo.Repository.OrdreMissionRepository;
import com.example.demo.Repository.ParkingRepository;
import com.example.demo.Repository.TypeCarRepository;
import com.example.demo.Repository.UtilisateurRepository;
import com.example.demo.Repository.VehiculeRepository;
import com.example.demo.model.EtatMission;
import com.example.demo.model.EtatVehicule;
import com.example.demo.model.Maintenance;
import com.example.demo.model.Modele;
import com.example.demo.model.OrdreMission;
import com.example.demo.model.Parking;
import com.example.demo.model.TypeCarrosserie;
import com.example.demo.model.Utilisateur;
import com.example.demo.model.Vehicule;
import com.example.demo.service.MaintenanceIAService;
import com.example.demo.service.VehiculeService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/vehicules")
@CrossOrigin(origins = "http://localhost:4200")
public class VehiculeController {

    private final String UPLOAD_DIR = System.getProperty("user.dir") + "/uploads/";

    @Autowired
    private VehiculeRepository vehiculeRepository;
    @Autowired
    private ParkingRepository parkingRepository;
    @Autowired
    private UtilisateurRepository utilisateurRepository;
    @Autowired
    private VehiculeService vehiculeService;
    @Autowired
    private OrdreMissionRepository ordreMissionRepository;
    @Autowired
    private ModeleRepository modeleRepository;
    @Autowired
    private TypeCarRepository typeCarrosserieRepository;
    @Autowired
    private MaintenanceIAService iaService;
    @Autowired
    private MaintenanceRepository maintenanceRepository;
    /*@GetMapping("/{id}")
    public ResponseEntity<vehicule> getVehiculeById(@PathVariable Long id) {
        vehicule vehicule = vehiculeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Véhicule non trouvé avec id " + id));
        return ResponseEntity.ok(vehicule);
    }*/
    @GetMapping("{id}")
    public ResponseEntity<Vehicule> getVehiculeDetails(@PathVariable Long id) {
    	Vehicule vehicule = vehiculeService.findByIdWithAllRelations(id);
        return ResponseEntity.ok(vehicule);
    }

    // Méthode modifiée : ajouter un véhicule sans image, juste JSON dans le corps
@PostMapping("/add")
public ResponseEntity<Vehicule> addVehicule(@RequestBody Vehicule vehicule) {
    // Parking optionnel
    if (vehicule.getParking() != null && vehicule.getParking().getId() != null) {
        Parking parking = parkingRepository.findById(vehicule.getParking().getId())
            .orElseThrow(() -> new RuntimeException("Parking introuvable"));
        vehicule.setParking(parking);
    } else {
        vehicule.setParking(null);
    }

    // Utilisateur obligatoire
    if (vehicule.getUtilisateur() == null || vehicule.getUtilisateur().getIdUtilisateur() == null) {
        throw new RuntimeException("Utilisateur obligatoire");
    }
    Utilisateur utilisateur = utilisateurRepository.findById(vehicule.getUtilisateur().getIdUtilisateur())
            .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
    vehicule.setUtilisateur(utilisateur);

    // Modele obligatoire
    if (vehicule.getModele() == null || vehicule.getModele().getId() == null) {
        throw new RuntimeException("Modèle obligatoire");
    }
    Modele modele = modeleRepository.findById(vehicule.getModele().getId())
            .orElseThrow(() -> new RuntimeException("Modèle introuvable"));
    vehicule.setModele(modele);

    // TypeCarrosserie obligatoire
    if (vehicule.getTypeCarrosserie() == null || vehicule.getTypeCarrosserie().getId() == null) {
        throw new RuntimeException("Type de carrosserie obligatoire");
    }
    TypeCarrosserie typeCarrosserie = typeCarrosserieRepository.findById(vehicule.getTypeCarrosserie().getId())
            .orElseThrow(() -> new RuntimeException("Type de carrosserie introuvable"));
    vehicule.setTypeCarrosserie(typeCarrosserie);

    Vehicule savedVehicule = vehiculeRepository.save(vehicule);
    return ResponseEntity.ok(savedVehicule);
}


    @GetMapping
    public List<Vehicule> getAllVehicules() {
        return vehiculeRepository.findAll();
    }

    @GetMapping("/uploads/{filename:.+}")
    public ResponseEntity<Resource> getFile(@PathVariable String filename) throws IOException {
        Path file = Paths.get(UPLOAD_DIR + filename);
        Resource resource = new UrlResource(file.toUri());

        if (resource.exists() && resource.isReadable()) {
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(resource);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Vehicule> updateVehicule(@PathVariable Long id, @RequestBody Vehicule updatedVehicule) {
        Optional<Vehicule> optionalVehicule = vehiculeRepository.findById(id);
        if (optionalVehicule.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Vehicule vehicule = optionalVehicule.get();
        vehicule.setImmatriculation(updatedVehicule.getImmatriculation());
        vehicule.setNumeroChassis(updatedVehicule.getNumeroChassis());
        vehicule.setDateCircu(updatedVehicule.getDateCircu());
        vehicule.setDate_acquisition(updatedVehicule.getDate_acquisition());
        vehicule.setKilometrageActuel(updatedVehicule.getKilometrageActuel());
        vehicule.setAnneeModel(updatedVehicule.getAnneeModel());
        vehicule.setTypeCarburant(updatedVehicule.getTypeCarburant());
        vehicule.setEtat(updatedVehicule.getEtat());
        vehicule.setPhotoUrl(updatedVehicule.getPhotoUrl());

        Vehicule savedVehicule = vehiculeRepository.save(vehicule);
        return ResponseEntity.ok(savedVehicule);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVehicule(@PathVariable Long id) {
        if (!vehiculeRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        vehiculeRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/vehicules-libres")
    public ResponseEntity<List<Vehicule>> getVehiculesLibres() {
        List<Vehicule> libres = vehiculeService.getVehiculesLibres();
        return ResponseEntity.ok(libres);
    }
	@GetMapping("/maintenance")
	public List<Vehicule> getVehiculesEnMaintenance() {
    return vehiculeRepository.findByEtat(EtatVehicule.MAINTENANCE);
	}



    @GetMapping("/vehicules-disponibles")
    public ResponseEntity<List<Vehicule>> getVehiculesDisponibles(
        @RequestParam("dateDebut") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateDebut,
        @RequestParam("dateFin") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateFin) {

        List<Vehicule> disponibles = vehiculeService.getVehiculesDisponibles(dateDebut, dateFin);
        return ResponseEntity.ok(disponibles);
    }
    @PatchMapping("/{id}/etat")
    public ResponseEntity<Vehicule> changerEtatVehicule(@PathVariable Long id, @RequestParam EtatVehicule etat) {
        Vehicule vehicule = vehiculeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Véhicule non trouvé"));

        vehicule.setEtat(etat);
        return ResponseEntity.ok(vehiculeRepository.save(vehicule));
    }
    @GetMapping("/disponibles-et-reserves")
    public ResponseEntity<List<Vehicule>> getVehiculesDisponiblesEtReserves() {
        List<Vehicule> vehicules = vehiculeService.getVehiculesDisponiblesEtReserves();
        return ResponseEntity.ok(vehicules);
    }
    @GetMapping("/vehicules/{id}/consommation")
    public ResponseEntity<Double> calculerConsommation(@PathVariable Long id) {
        Double conso = vehiculeService.calculerEtEnregistrerConsommationMoyenne(id);
        return ResponseEntity.ok(conso);
    }

}
