package com.example.demo.Controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.Dto.MissionCarburantDTO;
import com.example.demo.Dto.OrdreMissionDTO;
import com.example.demo.Repository.BonCarburantRepository;
import com.example.demo.Repository.OrdreMissionRepository;
import com.example.demo.model.BonCarburant;
import com.example.demo.model.EtatMission;
import com.example.demo.model.OrdreMission;
import com.example.demo.model.Vehicule;
import com.example.demo.service.OrdreMissionService;
import com.example.demo.service.VehiculeService;

@RestController
@RequestMapping("/api/ordres-mission")
@CrossOrigin(origins = "*")
public class OrdreMissionController {

    @Autowired
    private OrdreMissionService ordreMissionService;

    @Autowired
    private VehiculeService vehiculeService;
    
    @Autowired
    private OrdreMissionRepository ordreMissionRepository;
    
    @Autowired
    private BonCarburantRepository bonCarburantRepository;

    // --- Créer une mission
    @PostMapping("/create")
    public ResponseEntity<?> createOrdreMission(@RequestBody OrdreMission mission) {
        try {
            OrdreMission saved = ordreMissionService.createOrdreMission(mission);
            return ResponseEntity.ok(saved);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // --- Récupérer toutes les missions
    @GetMapping
    public ResponseEntity<List<OrdreMissionDTO>> getAllMissions() {
        List<OrdreMission> missions = ordreMissionRepository.findAll();

        List<OrdreMissionDTO> dtos = missions.stream().map(mission -> {
        	OrdreMissionDTO dto = new OrdreMissionDTO();
            dto.setId(mission.getId());
            dto.setDestination(mission.getDestination());
            dto.setMotif(mission.getMotif());
            dto.setEtat(mission.getEtat().name());
            dto.setUtilisateurNom(mission.getUtilisateur() != null ? mission.getUtilisateur().getNom() : "");
            dto.setVehiculeImmatriculation(
                mission.getVehicule() != null ? mission.getVehicule().getImmatriculation() : "" // si pas encore choisi
            );
            dto.setDateDepart(mission.getDateDepart().toString());
            dto.setDateRetour(mission.getDateRetour().toString());
            return dto;
        }).toList();

        return ResponseEntity.ok(dtos);
    }


    // --- Annuler une mission
    @PutMapping("/annuler/{id}")
    public ResponseEntity<OrdreMission> annulerOrdreMission(@PathVariable Long id) {
        OrdreMission ordreMisAJour = ordreMissionService.annulerOrdreMission(id);
        return ResponseEntity.ok(ordreMisAJour);
    }

    // --- Récupérer une mission par ID
    @GetMapping("/{id}")
    public ResponseEntity<OrdreMission> getById(@PathVariable Long id) {
        OrdreMission mission = ordreMissionService.getOrdreMissionById(id);
        return mission != null ? ResponseEntity.ok(mission) : ResponseEntity.notFound().build();
    }

    // --- Supprimer une mission
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        ordreMissionService.deleteOrdreMission(id);
        return ResponseEntity.noContent().build();
    }

    // --- Récupérer par états
    @GetMapping("/by-etats")
    public List<OrdreMission> getMissionsByEtats(@RequestParam List<String> etats) {
        return ordreMissionService.findByEtatIn(etats);
    }

    // --- Changer état
    @PutMapping("/{id}/etat")
    public ResponseEntity<?> changerEtatMission(
            @PathVariable Long id,
            @RequestParam EtatMission nouvelEtat) {
        try {
            ordreMissionService.changerEtatEtNotifier(id, nouvelEtat);
            return ResponseEntity.ok("État modifié et email envoyé.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur : " + e.getMessage());
        }
    }
    @GetMapping("/utilisateur/{id}")
    public List<OrdreMissionDTO> getOrdresMissionByUtilisateurId(@PathVariable("id") Long utilisateurId) {
        List<OrdreMission> ordres = ordreMissionRepository.findByUtilisateurIdUtilisateur(utilisateurId);
        return ordres.stream().map(mission -> {
            OrdreMissionDTO dto = new OrdreMissionDTO();
            dto.setId(mission.getId());
            dto.setDestination(mission.getDestination());
            dto.setMotif(mission.getMotif());
            dto.setDateDepart(mission.getDateDepart().toString());
            dto.setDateRetour(mission.getDateRetour().toString());
            dto.setEtat(mission.getEtat().name());
            dto.setUtilisateurNom(mission.getUtilisateur() != null ? mission.getUtilisateur().getNom() : "");
            dto.setVehiculeId(mission.getVehicule() != null ? mission.getVehicule().getIdVehicule() : null);
            dto.setVehiculeImmatriculation(
                mission.getVehicule() != null ? mission.getVehicule().getImmatriculation() : "Non défini"
            );
            return dto;
        }).toList();
    }




    // --- Mise à jour automatique des états
   /* @PostMapping("/mettre-a-jour-etat")
    public String mettreAJourEtatDesMissions() {
        ordreMissionService.mettreAJourEtatDesMissions();
        return "Mise à jour des états des missions effectuée";
    }*/

    // --- Terminer mission et libérer le véhicule
// Démarrer mission (dateDépartReelle automatique)
@PutMapping("/{id}/demarrer")
public ResponseEntity<?> demarrerMission(@PathVariable Long id) {
    try {
        OrdreMission mission = ordreMissionService.demarrerMission(id);
        return ResponseEntity.ok(mission);
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erreur : " + e.getMessage());
    }
}

// Terminer mission (dateRetourReelle automatique)
@PutMapping("/{id}/terminer")
public ResponseEntity<?> terminerMission(@PathVariable Long id) {
    try {
        OrdreMission mission = ordreMissionService.terminerMission(id);
        return ResponseEntity.ok(mission);
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erreur : " + e.getMessage());
    }
}

// --- Récupérer toutes les missions en attente
@GetMapping("/en-attente")
public ResponseEntity<List<OrdreMission>> getMissionsEnAttente() {
    List<OrdreMission> missionsEnAttente = ordreMissionService.findByEtat(EtatMission.EN_ATTENTE);
    return ResponseEntity.ok(missionsEnAttente);

}
@PutMapping("/missions/{missionId}/assign-vehicule/{vehiculeId}")
public ResponseEntity<?> assignVehiculeEtNotifier(
        @PathVariable Long missionId,
        @PathVariable Long vehiculeId) {

    try {
        OrdreMissionDTO dto = ordreMissionService.assignVehicule(missionId, vehiculeId);
        return ResponseEntity.ok(dto);

    } catch (IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("message", e.getMessage()));
    } catch (IllegalStateException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("message", e.getMessage()));
    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "Erreur lors de l'assignation du véhicule"));
    }
}




@GetMapping("/vehicule/{vehiculeId}")
public List<OrdreMissionDTO> getOrdresByVehicule(@PathVariable Long vehiculeId) {
    List<OrdreMission> missions = ordreMissionRepository.findByVehiculeIdVehicule(vehiculeId);
    return missions.stream().map(m -> {
        OrdreMissionDTO dto = new OrdreMissionDTO();
        dto.setId(m.getId());
        dto.setDestination(m.getDestination());
        dto.setMotif(m.getMotif());
        dto.setDateDepart(m.getDateDepart().toString());
        dto.setDateRetour(m.getDateRetour().toString());
        dto.setEtat(m.getEtat().name());
        dto.setUtilisateurNom(m.getUtilisateur() != null ? m.getUtilisateur().getNom() : "");
        dto.setVehiculeId(m.getVehicule() != null ? m.getVehicule().getIdVehicule() : null);
        dto.setVehiculeImmatriculation(
            m.getVehicule() != null ? m.getVehicule().getImmatriculation() : "Non défini"
        );
        return dto;
    }).toList();
}
@GetMapping("/missions/{id}/bons")
public List<BonCarburant> getBonsByMission(@PathVariable Long id) {
    return bonCarburantRepository.findByMission_Id(id);
}
@GetMapping("/missions-par-bon/{bonId}")
public List<MissionCarburantDTO> getMissionsParBon(@PathVariable Long bonId) {
    return ordreMissionService.getMissionsByBonId(bonId);
}

}