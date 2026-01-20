package com.example.demo.Controller;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.Dto.BonCarburantAvecMissionsDTO;
import com.example.demo.Dto.BonCarburantCreateDTO;
import com.example.demo.Dto.BonCarburantDTO;
import com.example.demo.Dto.MissionCarburantDTO;
import com.example.demo.Dto.OrdreMissionDTO;
import com.example.demo.Repository.BonCarburantRepository;
import com.example.demo.Repository.OrdreMissionRepository;
import com.example.demo.model.BonCarburant;
import com.example.demo.model.BonCarburant.EtatBon;
import com.example.demo.model.OrdreMission;
import com.example.demo.service.BonCarburantService;

@RestController
@RequestMapping("/api/boncarburant")
@CrossOrigin(origins = "*")
public class BonCarburantController {

    @Autowired
    private BonCarburantService bonService;
    
    @Autowired 
    private OrdreMissionRepository ordreMissionRepository;
    
    @Autowired
    private BonCarburantRepository bonCarburantRepository;

    // ➕ Ajouter un bon carburant
    @PostMapping("/ajouter")
    public ResponseEntity<BonCarburant> ajouter(@RequestBody BonCarburantCreateDTO dto) {
        BonCarburant bonCree = bonService.addBon(dto);
        return ResponseEntity.ok(bonCree);
    }

    // 🔎 Liste des bons par véhicule
    @GetMapping("/vehicule/{idVehicule}")
    public ResponseEntity<List<BonCarburant>> getBonsByVehicule(@PathVariable Long idVehicule) {
        return ResponseEntity.ok(bonService.getBonsByVehicule(idVehicule));
    }

    // 🔎 Liste de tous les bons (DTO simplifié)
    @GetMapping("/all")
    public List<BonCarburantDTO> getTousLesBons() {
        return bonService.getTousLesBons();
    }

    // ❌ Supprimer un bon
    @DeleteMapping("/supprimer/{id}")
    public ResponseEntity<Void> supprimer(@PathVariable Long id) {
        bonService.supprimerBon(id);
        return ResponseEntity.noContent().build();
    }

    // 🔎 Consommation totale pour un véhicule
    @GetMapping("/vehicule/{idVehicule}/total")
    public double getConsommationTotale(@PathVariable Long idVehicule) {
        return bonService.getConsommationTotale(idVehicule);
    }

    // 🔎 Liste des bons d’un utilisateur
    @GetMapping("/user/{idUtilisateur}")
    public List<BonCarburantDTO> getBonsByUtilisateur(@PathVariable Long idUtilisateur) {
        return bonService.getBonsByUtilisateur(idUtilisateur);
    }

    // ✅ Validation ou refus d’un bon par l’admin
    @PutMapping("/{id}/valider")
    public ResponseEntity<BonCarburant> validerBon(
            @PathVariable Long id,
            @RequestParam String action) { // ACCEPTE ou REFUSE
        BonCarburant bon = bonService.validerBon(id, action);
        return ResponseEntity.ok(bon);
    }
    @PutMapping("/{id}/modifier-quantite")
    public ResponseEntity<BonCarburant> modifierQuantite(
            @PathVariable Long id,
            @RequestParam double quantite) {
        BonCarburant bon = bonService.modifierQuantite(id, quantite);
        return ResponseEntity.ok(bon);
    }
    @GetMapping("/en-attente")
    public ResponseEntity<List<BonCarburant>> getBonsEnAttente() {
        List<BonCarburant> bons = bonService.findByEtat(EtatBon.EN_ATTENTE);
        return ResponseEntity.ok(bons);
    }
    @GetMapping("/vehicule/{idVehicule}/verifier/{bonId}")
    public ResponseEntity<Double> verifierBon(
            @PathVariable Long idVehicule,
            @PathVariable Long bonId) {
        double restant = bonService.verifierBon(bonId);
        return ResponseEntity.ok(restant);
    }
@GetMapping("/bons-avec-missions")
public List<BonCarburantAvecMissionsDTO> getBonsAvecMissions() {
    List<BonCarburant> bons = bonCarburantRepository.findAll(Sort.by("dateAchat"));
    List<BonCarburantAvecMissionsDTO> result = new ArrayList<>();

    for (int i = 0; i < bons.size(); i++) {
        BonCarburant bon = bons.get(i);
        if (bon.getDateAchat() == null) continue;

        // Conversion java.util.Date -> LocalDateTime
        LocalDateTime dateDebut = bon.getDateAchat()
                                     .toInstant()
                                     .atZone(ZoneId.systemDefault())
                                     .toLocalDateTime();

        // Chercher le prochain bon pour le même véhicule
        BonCarburant prochainBon = bonCarburantRepository
            .findFirstByVehicule_IdVehiculeAndDateAchatAfterOrderByDateAchatAsc(
                bon.getVehicule().getIdVehicule(),
                bon.getDateAchat() // java.util.Date, compatible maintenant
            )
            .orElse(null);

        LocalDateTime dateFin = (prochainBon != null)
                                ? prochainBon.getDateAchat()
                                             .toInstant()
                                             .atZone(ZoneId.systemDefault())
                                             .toLocalDateTime()
                                : LocalDateTime.MAX;

        List<MissionCarburantDTO> missionsDTO = ordreMissionRepository
            .findByVehicule_IdVehiculeAndDateDepartBetweenOrderByDateDepartAsc(
                bon.getVehicule().getIdVehicule(),
                dateDebut,
                dateFin
            )
            .stream()
            .map(m -> new MissionCarburantDTO(
                m.getId(),
                m.getDestination(),
                m.getUtilisateur().getNom(),
                m.getDateDepart().toString(),
                m.getDateRetour().toString(),
                m.getDistanceEstimee(),
                m.getCarburantNecessaire()
            ))
            .toList();

        result.add(new BonCarburantAvecMissionsDTO(
            bon.getId(),
            bon.getDateAchat(),
            bon.getMontant(),
            bon.getQuantite(),
            bon.getResponsable(),
            bon.getVehicule().getImmatriculation(),
            missionsDTO
        ));
    }

    return result;
}



}