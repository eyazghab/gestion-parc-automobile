package com.example.demo.Controller;

import com.example.demo.Dto.AlerteVehiculeDTO;
import com.example.demo.Dto.SuiviDTO;
import com.example.demo.Repository.AlerteHistoriqueRepository;
import com.example.demo.Repository.VehiculeRepository;
import com.example.demo.model.AlerteHistorique;
import com.example.demo.model.EtatVehicule;
import com.example.demo.model.ProchaineEcheance;
import com.example.demo.model.Suivi;
import com.example.demo.model.Vehicule;
import com.example.demo.service.SuiviService;
import com.example.demo.service.VehiculeService;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

@RestController
@RequestMapping("/api/suivis")
@CrossOrigin(origins = "*")
public class SuiviController {

    private final SuiviService suiviService;
    private final VehiculeRepository vehiculeRepository;
    private final AlerteHistoriqueRepository alerteRepo;
    private final VehiculeService vehiculeService;
    
    public SuiviController(SuiviService suiviService, VehiculeRepository vehiculeRepository,AlerteHistoriqueRepository alerteRepo,VehiculeService vehiculeService) {
        this.suiviService = suiviService;
        this.vehiculeRepository = vehiculeRepository;
        this.alerteRepo=alerteRepo;
        this.vehiculeService=vehiculeService;
    }

    // ✅ Ajouter un suivi
    @PostMapping
    public ResponseEntity<Suivi> createSuivi(@RequestBody Suivi suivi) {
        return ResponseEntity.ok(suiviService.save(suivi));
    }

    // ✅ Récupérer un suivi par ID
    @GetMapping("/{id}")
    public ResponseEntity<Suivi> getSuiviById(@PathVariable Long id) {
        Suivi suivi = suiviService.findById(id);
        return (suivi != null) ? ResponseEntity.ok(suivi) : ResponseEntity.notFound().build();
    }

    // ✅ Récupérer tous les suivis
    
@GetMapping
public ResponseEntity<List<SuiviDTO>> getAllSuivisDTO() {
    List<Suivi> suivis = suiviService.findAll();

    List<SuiviDTO> dtos = suivis.stream().map(suivi -> {
        Long idVehicule = suivi.getVehicule().getIdVehicule();
        String immatriculation = suivi.getVehicule().getImmatriculation();
        Integer kmActuel = suivi.getKilometreActuel();
        Date dateDerniersuivi = convertToDate(suivi.getDateDerniersuivi());

        // Calcul du prochain suivi
        LocalDate prochainSuivi = null;
        LocalDate dateDernierSuiviLD = suivi.getDateDerniersuivi();
        int dureeEstime = suivi.getDureeEstimé();
        if (dateDernierSuiviLD != null && dureeEstime > 0) {
            prochainSuivi = dateDernierSuiviLD.plusMonths(dureeEstime);
        }

        // 🔹 Récupérer la consommation moyenne depuis le service
        Double consommationMoyenne = vehiculeService.calculerEtEnregistrerConsommationMoyenne(idVehicule);

        SuiviDTO dto = new SuiviDTO(
        	    idVehicule,
        	    immatriculation,
        	    kmActuel,
        	    suivi.getEcheances(),
        	    dateDerniersuivi,
        	    convertToDate(suivi.getDateKm()),
        	    convertToDate(suivi.getDateTemp()),
        	    suivi.getAlertes(),
        	    suivi.isAlerteMaintenance(),
        	    suivi.getDescription(),
        	    suivi.getFrequence(),
        	    dureeEstime,
        	    suivi.getNombreAccidents(),
        	    suivi.getNombrePannes(),
        	    suivi.getKilometresDepuisFreins(),
        	    suivi.getKilometresDepuisVidange(),
        	    suivi.getDureeVieBatterie(),
        	    vehiculeService.calculerEtEnregistrerConsommationMoyenne(idVehicule) // valeur pour le nouveau champ
        	);


        dto.setEtatVehicule(suivi.getVehicule().getEtat());
        dto.setDateCircu(suivi.getVehicule().getDateCircu());
        dto.setProchainSuivi(prochainSuivi);

        // 🔹 Injecter la consommation moyenne
        dto.setConsommationMoyenne(consommationMoyenne);

        return dto;
    }).toList();

    return ResponseEntity.ok(dtos);
}





// Méthode utilitaire pour convertir LocalDate en Date
private Date convertToDate(LocalDate localDate) {
    return localDate != null ? java.sql.Date.valueOf(localDate) : null;
}




    // ✅ Mettre à jour un suivi
    @PutMapping("/{id}")
    public ResponseEntity<Suivi> updateSuivi(@PathVariable Long id, @RequestBody Suivi suivi) {
        Suivi updated = suiviService.update(id, suivi);
        return (updated != null) ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    // ✅ Supprimer un suivi
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSuivi(@PathVariable Long id) {
        suiviService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // ✅ Récupérer le suivi d’un véhicule
    @GetMapping("/vehicule/{idVehicule}")
    public ResponseEntity<Suivi> getSuiviVehicule(@PathVariable Long idVehicule) {
        Suivi suivi = suiviService.getSuiviVehicule(idVehicule);
        return (suivi != null) ? ResponseEntity.ok(suivi) : ResponseEntity.notFound().build();
    }

    // ✅ Récupérer les alertes d’un véhicule
    @GetMapping("/vehicule/{idVehicule}/alertes")
    public ResponseEntity<List<String>> getAlertesVehicule(@PathVariable Long idVehicule) {
        return ResponseEntity.ok(suiviService.getAlertesVehicule(idVehicule));
    }

    // ✅ Forcer la mise à jour des alertes d’un véhicule
    @PutMapping("/vehicule/{idVehicule}/mettre-a-jour-alertes")
    public ResponseEntity<Suivi> updateAlertesVehicule(@PathVariable Long idVehicule) {
        suiviService.mettreAJourAlertes(idVehicule);
        Suivi suivi = suiviService.getSuiviVehicule(idVehicule);
        return ResponseEntity.ok(suivi);
    }


    // ✅ Récupérer toutes les alertes individuelles de tous les véhicules
   @GetMapping("/alertes-tous-vehicules")
public ResponseEntity<List<AlerteVehiculeDTO>> getAlertesTousVehicules() {
    List<AlerteHistorique> alertesHist = alerteRepo.findAll(); // toutes les alertes déjà enregistrées
    List<AlerteVehiculeDTO> alertesDTO = new ArrayList<>();

    for (AlerteHistorique a : alertesHist) {
        alertesDTO.add(new AlerteVehiculeDTO(
            a.getId(),
            a.getVehicule().getImmatriculation(),
            a.getMessage(),
            a.isResolue() // prend la valeur persistée en base
        ));
    }

    return ResponseEntity.ok(alertesDTO);
}


    // ✅ Récupérer toutes les alertes groupées par véhicule
    @GetMapping("/alertes-groupes")
    public ResponseEntity<Map<String, List<String>>> getAlertesGroupes() {
        List<Vehicule> vehicules = vehiculeRepository.findAll();
        Map<String, List<String>> result = new HashMap<>();

        for (Vehicule v : vehicules) {
            List<String> alertes = suiviService.getAlertesVehicule(v.getIdVehicule());
            if (!alertes.isEmpty()) {
                result.put(v.getImmatriculation(), alertes);
            }
        }

        return ResponseEntity.ok(result);
    }
    @PutMapping("/{id}/resolue")
    public ResponseEntity<AlerteHistorique> marquerCommeResolue(@PathVariable Long id) {
        AlerteHistorique alerte = alerteRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Alerte non trouvée"));
        alerte.setResolue(true);
        alerteRepo.save(alerte);
        return ResponseEntity.ok(alerte);
    }
    @GetMapping("/suivis-par-etat/{etat}")
public ResponseEntity<List<SuiviDTO>> getSuivisParEtat(@PathVariable String etat) {
    EtatVehicule etatVehicule;
    try {
        etatVehicule = EtatVehicule.valueOf(etat.toUpperCase());
    } catch (IllegalArgumentException e) {
        return ResponseEntity.badRequest().build();
    }

    List<Suivi> suivis = suiviService.findAll().stream()
        .filter(s -> s.getVehicule().getEtat() == etatVehicule)
        .toList();

    List<SuiviDTO> dtos = suivis.stream().map(suivi -> {
        Double consommationMoyenne = vehiculeService.calculerEtEnregistrerConsommationMoyenne(suivi.getVehicule().getIdVehicule());

        return new SuiviDTO(
            suivi.getVehicule().getIdVehicule(),
            suivi.getVehicule().getImmatriculation(),
            suivi.getKilometreActuel(),
            suivi.getEcheances(),
            convertToDate(suivi.getDateDerniersuivi()),
            convertToDate(suivi.getDateKm()),
            convertToDate(suivi.getDateTemp()),
            suivi.getAlertes(),
            suivi.isAlerteMaintenance(),
            suivi.getDescription(),
            suivi.getFrequence(),
            suivi.getDureeEstimé(),
            suivi.getNombreAccidents(),
            suivi.getNombrePannes(),
            suivi.getKilometresDepuisFreins(),
            suivi.getKilometresDepuisVidange(),
            suivi.getDureeVieBatterie(),
            consommationMoyenne
        );
    }).toList();

    return ResponseEntity.ok(dtos);
}

@PostMapping("/updateAfterMaintenance")
public ResponseEntity<Suivi> updateAfterMaintenance(@RequestBody Map<String, Object> data) {
    try {
        Long idSuivi = ((Number) data.get("suiviId")).longValue();
        String typeIncident = data.get("typeIncident") != null ? ((String) data.get("typeIncident")).toUpperCase() : "";
        List<Map<String, Object>> lignesMaintenance = (List<Map<String, Object>>) data.get("lignesMaintenance");

        // 🔹 Récupérer le suivi
        Suivi suivi = suiviService.findById(idSuivi);
        if (suivi == null) {
            return ResponseEntity.notFound().build();
        }

        // 🔹 Réinitialiser les compteurs spécifiques selon la maintenance effectuée
        if (lignesMaintenance != null) {
            for (Map<String, Object> ligne : lignesMaintenance) {
                String desc = ((String) ligne.get("description")).toUpperCase().trim();
                switch (desc) {
                    case "CHANGEMENT FREINS":
                    case "CHANGEMENT_FREINS":
                        suivi.setKilometresDepuisFreins(0);
                        break;

                    case "VIDANGE":
                        suivi.setKilometresDepuisVidange(0);
                        break;

                    case "CHANGEMENT BATTERIE":
                    case "CHANGEMENT_BATTERIE":
                        suivi.setDureeVieBatterie(0);
                        break;

                    case "REVISION GENERALE":
                        suivi.setKilometresDepuisFreins(0);
                        suivi.setKilometresDepuisVidange(0);
                        suivi.setDureeVieBatterie(0);
                        suivi.setNombrePannes(0);
                        suivi.setNombreAccidents(0);
                        break;
                }
            }
        }

        // 🔹 Réinitialiser selon le type d’incident
        if ("PANNE".equalsIgnoreCase(typeIncident)) {
            suivi.setNombrePannes(0);
        } else if ("ACCIDENT".equalsIgnoreCase(typeIncident)) {
            suivi.setNombreAccidents(0);
        }

        // 🔹 Mettre à jour la date de dernière maintenance
        suivi.setDateDerniersuivi(LocalDate.now());

        // 🔹 Remettre le véhicule à l’état DISPONIBLE
        if (suivi.getVehicule() != null) {
            suivi.getVehicule().setEtat(EtatVehicule.DISPONIBLE);
        }

        // 🔹 Sauvegarder le suivi et le véhicule
        Suivi saved = suiviService.save(suivi);
        vehiculeService.createVehicule(suivi.getVehicule());

        return ResponseEntity.ok(saved);

    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(500).build();
    }
}



 @GetMapping("/vehicule/{idVehicule}/dernier")
public ResponseEntity<SuiviDTO> getDernierSuiviVehicule(@PathVariable Long idVehicule) {
    // 🔹 Récupérer le dernier suivi via le service
    Suivi dernierSuivi = suiviService.getDernierSuiviVehicule(idVehicule);

    if (dernierSuivi == null) {
        return ResponseEntity.notFound().build();
    }

    // 🔹 Calculer la consommation moyenne via le service Vehicule
    Double consommationMoyenne = vehiculeService.calculerEtEnregistrerConsommationMoyenne(dernierSuivi.getVehicule().getIdVehicule());

    // 🔹 Convertir Suivi en SuiviDTO
    SuiviDTO dto = new SuiviDTO(
        dernierSuivi.getVehicule().getIdVehicule(),
        dernierSuivi.getVehicule().getImmatriculation(),
        dernierSuivi.getKilometreActuel(),
        dernierSuivi.getEcheances(),
        dernierSuivi.getDateDerniersuivi() != null ? java.sql.Date.valueOf(dernierSuivi.getDateDerniersuivi()) : null,
        dernierSuivi.getDateKm() != null ? java.sql.Date.valueOf(dernierSuivi.getDateKm()) : null,
        dernierSuivi.getDateTemp() != null ? java.sql.Date.valueOf(dernierSuivi.getDateTemp()) : null,
        dernierSuivi.getAlertes(),
        dernierSuivi.isAlerteMaintenance(),
        dernierSuivi.getDescription(),
        dernierSuivi.getFrequence(),
        dernierSuivi.getDureeEstimé(),
        dernierSuivi.getNombreAccidents(),
        dernierSuivi.getNombrePannes(),
        dernierSuivi.getKilometresDepuisFreins(),
        dernierSuivi.getKilometresDepuisVidange(),
        dernierSuivi.getDureeVieBatterie(),
        consommationMoyenne
    );

    return ResponseEntity.ok(dto);
}


  @GetMapping("/etat/{idVehicule}")
  public ResponseEntity<String> getEtatVehicule(@PathVariable Long idVehicule) {
      String resultat = suiviService.verifierEtatVehicule(idVehicule);
      return ResponseEntity.ok(resultat);
  }


}
