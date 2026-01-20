package com.example.demo.Controller;

import com.example.demo.Repository.SinistreRepository;
import com.example.demo.Repository.UtilisateurRepository;
import com.example.demo.Repository.VehiculeRepository;
import com.example.demo.model.Circulation;
import com.example.demo.model.EtatSinistre;
import com.example.demo.model.Sinistre;
import com.example.demo.model.TypeIncident;
import com.example.demo.model.Utilisateur;
import com.example.demo.model.Vehicule;
import com.example.demo.service.SinistreService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/sinistres")
@CrossOrigin(origins = "http://localhost:4200")
public class SinistreController {

    @Autowired
    private SinistreService sinistreService;
    
    @Autowired
    private VehiculeRepository vehiculeRepository;
    
    @Autowired
    private UtilisateurRepository utilisateurRepository;
    
    @Autowired
    private SinistreRepository sinistreRepository;

@PostMapping("/add")
public ResponseEntity<Sinistre> create(
        @RequestParam("vehiculeId") Long vehiculeId,
        @RequestParam("utilisateurId") Long utilisateurId,
        @RequestParam("dateSinistre") String dateSinistreStr,
        @RequestParam("heureSinistre") String heureSinistreStr,
        @RequestParam("lieuSinistre") String lieuSinistre,
        @RequestParam("typeIncident") TypeIncident typeIncident, // 🔹 ajouter le type
        @RequestParam("degats") String degats,
        @RequestParam(value = "files", required = false) MultipartFile[] files) {

    Vehicule vehicule = vehiculeRepository.findById(vehiculeId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Véhicule introuvable"));

    Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Utilisateur introuvable"));

    Sinistre sinistre = new Sinistre();
    sinistre.setVehicule(vehicule);
    sinistre.setUtilisateur(utilisateur);
    sinistre.setLieuSinistre(lieuSinistre);
    sinistre.setTypeIncident(typeIncident); // 🔹 affecter le typeIncident
    sinistre.setDegats(degats);
    sinistre.setEtat(EtatSinistre.DECLARE);
    sinistre.setDateDeclaration(new Date());

    try {
        // ✅ Définir les formats attendus (à adapter si besoin)
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        // ✅ Parser les chaînes reçues depuis le front
        LocalDate date = LocalDate.parse(dateSinistreStr.trim(), dateFormatter);
        LocalTime time = LocalTime.parse(heureSinistreStr.trim(), timeFormatter);

        // ✅ Affecter l’heure
        sinistre.setHeureSinistre(time);

        // ✅ Fusionner date + heure et convertir en java.util.Date
        LocalDateTime dateTime = LocalDateTime.of(date, time);
        sinistre.setDateSinistre(Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant()));
    }
    catch (DateTimeParseException e) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Format de date ou d’heure invalide. Format attendu : yyyy-MM-dd et HH:mm");
    }
    catch (Exception e) {
        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur interne lors du traitement de la date du sinistre");
    }

    Sinistre savedSinistre = sinistreService.saveSinistreEtNotifier(sinistre, files);
    return ResponseEntity.ok(savedSinistre);
}


 @PutMapping("/{id}/etat")
 public ResponseEntity<Sinistre> changerEtat(
         @PathVariable Long id,
         @RequestParam EtatSinistre nouvelEtat) {
     return ResponseEntity.ok(sinistreService.changerEtat(id, nouvelEtat));
 }
 @PutMapping("/{id}/prendre-en-charge")
 public ResponseEntity<Sinistre> prendreEnCharge(
         @PathVariable Long id,
         @RequestParam Long technicienId,
         @RequestParam Circulation circulation) {

     Sinistre updatedSinistre = sinistreService.prendreEnCharge(id, technicienId, circulation);
     return ResponseEntity.ok(updatedSinistre);
 }


    @GetMapping
    public List<Sinistre> getAll() {
        return sinistreService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Sinistre> getById(@PathVariable Long id) {
        Sinistre sinistre = sinistreService.findById(id);
        if (sinistre == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(sinistre);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Sinistre> update(@PathVariable Long id, @RequestBody Sinistre sinistre) {
        Sinistre updated = sinistreService.update(id, sinistre);
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        sinistreService.delete(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/alertes")
    public ResponseEntity<List<Sinistre>> getAlertes() {
        List<Sinistre> alertes = sinistreService.findByEtat(EtatSinistre.DECLARE);
        return ResponseEntity.ok(alertes);
    }
}
