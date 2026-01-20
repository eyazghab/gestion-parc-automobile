package com.example.demo.service.imp;

import com.example.demo.Dto.MaintenanceDTO;
import com.example.demo.Repository.*;
import com.example.demo.model.*;
import com.example.demo.service.EmailService;
import com.example.demo.service.MaintenanceService;

import jakarta.transaction.Transactional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class MaintenanceServiceImp implements MaintenanceService {

    private final MaintenanceRepository maintenanceRepository;
    private final TechnicienRepository technicienRepository;
    private final VehiculeRepository vehiculeRepository;
    private final SinistreRepository sinistreRepository;
    private final TypeMainRepository typeMaintenanceRepository;
    private final EmailService emailService;
    private final SuiviRepository suiviRepository;

    public MaintenanceServiceImp(MaintenanceRepository maintenanceRepository,
                                 TechnicienRepository technicienRepository,
                                 VehiculeRepository vehiculeRepository,
                                 SinistreRepository sinistreRepository,
                                 TypeMainRepository typeMaintenanceRepository,
                                 EmailService emailService,
                                 SuiviRepository suiviRepository) {
        this.maintenanceRepository = maintenanceRepository;
        this.technicienRepository = technicienRepository;
        this.vehiculeRepository = vehiculeRepository;
        this.sinistreRepository = sinistreRepository;
        this.typeMaintenanceRepository = typeMaintenanceRepository;
        this.emailService = emailService;
        this.suiviRepository = suiviRepository;
    }

    // ===================== CREATE =====================

  @Override
@Transactional
public Maintenance saveMaintenance(MaintenanceDTO dto) {
    Technicien technicien = technicienRepository.findById(dto.getTechnicienId())
            .orElseThrow(() -> new RuntimeException("Technicien non trouvé"));
    Vehicule vehicule = vehiculeRepository.findById(dto.getVehiculeId())
            .orElseThrow(() -> new RuntimeException("Véhicule non trouvé"));

    Maintenance maintenance = new Maintenance();
    maintenance.setObservations(dto.getObservations());
    maintenance.setStatut(dto.getStatut());
    maintenance.setCoutPiece(dto.getCoutPiece());
    maintenance.setCoutExterne(dto.getCoutExterne());
    maintenance.setDateMaintenance(dto.getDateMaintenance());
    maintenance.setDateEffectuee(dto.getDateEffectuee());
    maintenance.setTechnicien(technicien);
    maintenance.setVehicule(vehicule);

    // 🔗 Type Maintenance
    TypeMaintenance typeMaintenance = null;
    if (dto.getTypeMaintenanceId() != null) {
        typeMaintenance = typeMaintenanceRepository.findById(dto.getTypeMaintenanceId())
                .orElseThrow(() -> new RuntimeException("TypeMaintenance non trouvé"));
        maintenance.setTypeMaintenance(typeMaintenance);
    }

    // 🔗 Sinistre & Type Incident
    if (dto.getSinistreId() != null) {
        Sinistre sinistre = sinistreRepository.findById(dto.getSinistreId())
                .orElseThrow(() -> new RuntimeException("Sinistre non trouvé"));
        maintenance.setSinistre(sinistre);
        maintenance.setTypeIncident(sinistre.getTypeIncident());
    } else if (dto.getTypeIncident() != null) {
        maintenance.setTypeIncident(dto.getTypeIncident());
    }

    // 🔧 Lignes de maintenance
    List<LigneMaintenance> lignes = new ArrayList<>();
    if (dto.getLignesMaintenance() != null) {
        for (LigneMaintenance l : dto.getLignesMaintenance()) {
            LigneMaintenance ligne = new LigneMaintenance();
            ligne.setDescription(l.getDescription());
            ligne.setQuantite(l.getQuantite());
            ligne.setMontant(l.getMontant());
            if (l.getTypeMaintenance() != null) {
                TypeMaintenance tm = typeMaintenanceRepository.findById(l.getTypeMaintenance().getId())
                        .orElseThrow(() -> new RuntimeException("TypeMaintenance ligne non trouvé"));
                ligne.setTypeMaintenance(tm);
            }
            ligne.setMaintenance(maintenance);
            lignes.add(ligne);
        }
    }
    maintenance.setLignes(lignes);

    // 💰 Calcul du coût total
    double total = lignes.stream()
            .mapToDouble(l -> l.getMontant() != null ? l.getMontant() : 0)
            .sum();

    if (maintenance.getCoutPiece() != null) total += maintenance.getCoutPiece();
    if (maintenance.getCoutExterne() != null) total += maintenance.getCoutExterne();

    // ⚙️ Gestion des types d’interventions
    if (dto.getTypeIntervention() != null) {
        for (TypeIntervention ti : dto.getTypeIntervention()) {
            total += switch (ti) {
                case VIDANGE -> 50;
                case changement_FREINS -> 120;
                case changement_ROUES -> 300;
                case changement_BATTERIE -> 200;
                case AUTRE -> 150;
            };
        }
        maintenance.setTypeIntervention(dto.getTypeIntervention());
    }

    // 💬 Gestion du cas AUTRE avec description libre
    if (dto.getTypeIntervention() != null &&
        dto.getTypeIntervention().contains(TypeIntervention.AUTRE) &&
        dto.getAutreDescription() != null && !dto.getAutreDescription().isEmpty()) {
        
        // On ajoute la description dans les observations
        String obs = (maintenance.getObservations() != null ? maintenance.getObservations() + " | " : "")
                   + "Autre intervention : " + dto.getAutreDescription();
        maintenance.setObservations(obs);
    }

    maintenance.setCoutTotal(total);

    Maintenance saved = maintenanceRepository.save(maintenance);

    vehicule.setEtat(EtatVehicule.MAINTENANCE); // ou "EN_MAINTENANCE" selon ton enum
    vehiculeRepository.save(vehicule);
    
    // Génération du numéro de maintenance
    if (saved.getNumeroMaintenance() == null || saved.getNumeroMaintenance().isEmpty()) {
        saved.setNumeroMaintenance("MAIN-" + LocalDate.now().getYear() + "-" + saved.getIdMaintenance());
        saved = maintenanceRepository.save(saved);
    }

    // 🔄 Mise à jour du suivi véhicule
    List<Suivi> suivis = suiviRepository.findByVehicule_IdVehicule(vehicule.getIdVehicule());
    Suivi suivi = suivis.isEmpty() ? new Suivi() : suivis.get(0);
    suivi.setVehicule(vehicule);
    suivi.setDateDerniersuivi(LocalDate.now());

    if (saved.getTypeIncident() == TypeIncident.ACCIDENT)
        suivi.setNombreAccidents(suivi.getNombreAccidents() + 1);
    if (saved.getTypeIncident() == TypeIncident.PANNE)
        suivi.setNombrePannes(suivi.getNombrePannes() + 1);

    if (typeMaintenance != null) {
        switch (typeMaintenance.getLibelle()) {
            case "Freins" -> suivi.setKilometresDepuisFreins(0);
            case "Vidange" -> suivi.setKilometresDepuisVidange(0);
            case "Changement batterie" -> suivi.setDureeVieBatterie(0);
        }
    }

    suiviRepository.save(suivi);

    // 📧 Envoi d'email au technicien
    if (technicien.getEmailTech() != null && !technicien.getEmailTech().isEmpty()) {
        String subject = "Nouvelle maintenance à réaliser";
        String html = "<h3>Bonjour,</h3><p>Une nouvelle maintenance a été créée pour le véhicule : <strong>"
                + saved.getVehicule().getImmatriculation() + "</strong></p>"
                + "<ul>"
                + "<li>Numéro Maintenance : " + saved.getNumeroMaintenance() + "</li>"
                + "<li>Type d'incident : " + (saved.getTypeIncident() != null ? saved.getTypeIncident() : "N/A") + "</li>"
                + "<li>Observations : " + saved.getObservations() + "</li>"
                + "</ul>";
        emailService.sendHtmlMail(technicien.getEmailTech(), subject, html);
    }

    return saved;
}


@Override
@Transactional
public Maintenance updateMaintenance(Long id, MaintenanceDTO dto) {
    Maintenance existing = maintenanceRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Maintenance non trouvée"));

    // -------------------- Mise à jour des champs de base --------------------
    existing.setNumeroMaintenance(dto.getNumeroMaintenance());
    existing.setObservations(dto.getObservations());
    existing.setStatut(dto.getStatut());
    existing.setCoutPiece(dto.getCoutPiece());
    existing.setCoutExterne(dto.getCoutExterne());

    if (dto.getDateMaintenance() != null) existing.setDateMaintenance(dto.getDateMaintenance());
    if (dto.getDateFinMaintenance() != null) existing.setDateFinMaintenance(dto.getDateFinMaintenance());

    if (dto.getTechnicienId() != null) {
        Technicien tech = technicienRepository.findById(dto.getTechnicienId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Technicien non trouvé"));
        existing.setTechnicien(tech);
    }

    if (dto.getVehiculeId() != null) {
        Vehicule vehicule = vehiculeRepository.findById(dto.getVehiculeId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Véhicule non trouvé"));
        existing.setVehicule(vehicule);
    }

    if (dto.getTypeMaintenanceId() != null) {
        TypeMaintenance typeMaintenance = typeMaintenanceRepository.findById(dto.getTypeMaintenanceId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "TypeMaintenance non trouvé"));
        existing.setTypeMaintenance(typeMaintenance);
    }

    if (dto.getTypeIncident() != null) existing.setTypeIncident(dto.getTypeIncident());
    if (dto.getDateDepartPlanifiee() != null) existing.setDateDepartPlanifiee(dto.getDateDepartPlanifiee());
    if (dto.getDateFinPlanifiee() != null) existing.setDateFinPlanifiee(dto.getDateFinPlanifiee());

    // -------------------- Lignes de maintenance --------------------
    if (dto.getLignesMaintenance() != null) {
        Set<Long> dtoIds = dto.getLignesMaintenance().stream()
                .map(LigneMaintenance::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        existing.getLignes().removeIf(ligne -> ligne.getId() != null && !dtoIds.contains(ligne.getId()));

        for (LigneMaintenance l : dto.getLignesMaintenance()) {
            LigneMaintenance ligne = (l.getId() != null)
                    ? existing.getLignes().stream()
                            .filter(e -> e.getId().equals(l.getId()))
                            .findFirst()
                            .orElse(new LigneMaintenance())
                    : new LigneMaintenance();

            ligne.setDescription(l.getDescription());
            ligne.setQuantite(l.getQuantite());
            ligne.setMontant(l.getMontant());

            if (l.getTypeMaintenance() != null) {
                TypeMaintenance tm = typeMaintenanceRepository.findById(l.getTypeMaintenance().getId())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "TypeMaintenance ligne non trouvé"));
                ligne.setTypeMaintenance(tm);
            }

            ligne.setMaintenance(existing);
            if (!existing.getLignes().contains(ligne)) existing.getLignes().add(ligne);
        }
    }

    // -------------------- Calcul du coût total --------------------
    double total = existing.getLignes().stream()
            .mapToDouble(l -> l.getMontant() != null ? l.getMontant() : 0)
            .sum();
    if (existing.getCoutPiece() != null) total += existing.getCoutPiece();
    if (existing.getCoutExterne() != null) total += existing.getCoutExterne();

    if (dto.getTypeIntervention() != null) {
        for (TypeIntervention ti : dto.getTypeIntervention()) {
            total += switch (ti) {
                case VIDANGE -> 50;
                case changement_FREINS -> 120;
                case changement_ROUES -> 300;
                case changement_BATTERIE -> 200;
                case AUTRE -> 150;
            };
        }
        existing.setTypeIntervention(dto.getTypeIntervention());
    }

    // Gestion du cas AUTRE
    if (dto.getTypeIntervention() != null &&
        dto.getTypeIntervention().contains(TypeIntervention.AUTRE) &&
        dto.getAutreDescription() != null && !dto.getAutreDescription().isEmpty()) {

        String obs = (existing.getObservations() != null ? existing.getObservations() + " | " : "")
                   + "Autre intervention : " + dto.getAutreDescription();
        existing.setObservations(obs);
    }

    existing.setCoutTotal(total);

    // -------------------- Réinitialisation du suivi du véhicule --------------------
    if ("Terminee".equalsIgnoreCase(dto.getStatut()) || "Terminée".equalsIgnoreCase(dto.getStatut())) {
        if (existing.getDateEffectuee() == null) existing.setDateEffectuee(LocalDate.now());
        if (existing.getDateFinMaintenance() == null) existing.setDateFinMaintenance(LocalDate.now());

        Vehicule vehicule = existing.getVehicule();
        if (vehicule != null && vehicule.getSuivis() != null && !vehicule.getSuivis().isEmpty()) {

            // 🔹 Récupérer le dernier suivi (par dateDerniersuivi)
            Suivi dernierSuivi = vehicule.getSuivis().stream()
                    .filter(s -> s.getDateDerniersuivi() != null)
                    .max(Comparator.comparing(Suivi::getDateDerniersuivi))
                    .orElse(null);

            if (dernierSuivi != null) {
                // Réinitialisation selon type d'intervention
                if (dto.getTypeIntervention() != null) {
                    for (TypeIntervention ti : dto.getTypeIntervention()) {
                        switch (ti) {
                            case changement_FREINS -> dernierSuivi.setKilometresDepuisFreins(0);
                            case VIDANGE -> dernierSuivi.setKilometresDepuisVidange(0);
                            case changement_BATTERIE -> dernierSuivi.setDureeVieBatterie(0);
                            default -> {}
                        }
                    }
                }

                // Réinitialisation selon type d'incident
                if (dto.getTypeIncident() != null) {
                    switch (dto.getTypeIncident()) {
                        case PANNE -> dernierSuivi.setNombrePannes(0);
                        case ACCIDENT -> dernierSuivi.setNombreAccidents(0);
                        default -> {}
                    }
                }

                // Sauvegarde du suivi modifié
                suiviRepository.save(dernierSuivi);
            }
        }
    }

    // Sauvegarde finale
    return maintenanceRepository.save(existing);
}








// Méthode utilitaire pour convertir String -> LocalDate en toute sécurité
private LocalDate parseLocalDate(String dateStr) {
    if (dateStr == null || dateStr.isBlank()) return null;
    try {
        return LocalDate.parse(dateStr); // Spring/Jackson attend format yyyy-MM-dd
    } catch (DateTimeParseException e) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Date invalide : " + dateStr);
    }
}



    // ===================== CLOTURE =====================
@Override
@Transactional
public Maintenance cloturerMaintenance(Long idMaintenance) {
    // 1️⃣ Récupérer la maintenance
    Maintenance maintenance = maintenanceRepository.findById(idMaintenance)
            .orElseThrow(() -> new RuntimeException("Maintenance introuvable"));

    // 2️⃣ Mettre à jour le statut et la date de fin
    maintenance.setStatut("Termine");
    LocalDate today = LocalDate.now();
    maintenance.setDateEffectuee(today);
    
    // Optionnel : définir une date de fin planifiée si elle n'existe pas encore
    if (maintenance.getDateFinMaintenance() == null) {
        maintenance.setDateFinMaintenance(today); // ici la date de clôture réelle
    }

    // 3️⃣ Si un sinistre est associé, le marquer comme traité
    if (maintenance.getSinistre() != null) {
        maintenance.getSinistre().setEtat(EtatSinistre.TRAITE);
        sinistreRepository.save(maintenance.getSinistre());
    }

    // 4️⃣ Remettre le véhicule en état disponible
    if (maintenance.getVehicule() != null) {
        maintenance.getVehicule().setEtat(EtatVehicule.DISPONIBLE);
        vehiculeRepository.save(maintenance.getVehicule());
    }

    // 5️⃣ Mise à jour du suivi du véhicule si nécessaire
    if (maintenance.getVehicule() != null) {
        List<Suivi> suivis = suiviRepository.findByVehicule_IdVehicule(maintenance.getVehicule().getIdVehicule());
        Suivi suivi = suivis.isEmpty() ? new Suivi() : suivis.get(0);
        suivi.setVehicule(maintenance.getVehicule());
        suivi.setDateDerniersuivi(today);

        if (maintenance.getTypeIncident() == TypeIncident.ACCIDENT) {
            suivi.setNombreAccidents(suivi.getNombreAccidents() + 1);
        }
        if (maintenance.getTypeIncident() == TypeIncident.PANNE) {
            suivi.setNombrePannes(suivi.getNombrePannes() + 1);
        }

        suiviRepository.save(suivi);
    }

    // 6️⃣ Sauvegarder et retourner la maintenance mise à jour
    return maintenanceRepository.save(maintenance);
}


    // ===================== CHANGER STATUT =====================
    @Override
    @Transactional
    public Maintenance changeStatut(Long id, String statut) {
        Maintenance maintenance = maintenanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Maintenance non trouvée"));
        maintenance.setStatut(statut);
        return maintenanceRepository.save(maintenance);
    }

    // ===================== GET =====================
    @Override
    public Maintenance getMaintenanceById(Long id) {
        return maintenanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Maintenance non trouvée"));
    }

    @Override
    public MaintenanceDTO getMaintenanceDTOById(Long id) {
        Maintenance maintenance = maintenanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Maintenance non trouvée"));
        return new MaintenanceDTO(maintenance);
    }
    
@Override
public List<MaintenanceDTO> getAllMaintenancesDTO() {
    return maintenanceRepository.findAllWithLignes().stream()
            .map(m -> {
                TypeIncident typeIncident = m.getSinistre() != null 
                        ? m.getSinistre().getTypeIncident() 
                        : m.getTypeIncident();

                // ✅ Corrigé : si c’est déjà une liste
                List<TypeIntervention> typeInterventions =
                        m.getTypeIntervention() != null ? new ArrayList<>(m.getTypeIntervention()) : new ArrayList<>();

                return new MaintenanceDTO(
                        m.getIdMaintenance(),
                        m.getNumeroMaintenance(),
                        m.getObservations(),
                        m.getTypeMaintenance() != null ? m.getTypeMaintenance().getLibelle() : null,
                        m.getStatut(),
                        m.getCoutPiece(),
                        m.getCoutExterne(),
                        m.getCoutTotal(),
                        m.getDateMaintenance(),
                        m.getDateEffectuee(),
                        m.getVehicule() != null ? m.getVehicule().getIdVehicule() : null,
                        m.getVehicule() != null ? m.getVehicule().getImmatriculation() : null,
                        m.getTechnicien() != null ? m.getTechnicien().getIdTechnicien() : null,
                        m.getTechnicien() != null ? m.getTechnicien().getNom() : null,
                        m.getLignes() != null ? m.getLignes() : new ArrayList<>(),
                        m.getTypeMaintenance() != null ? m.getTypeMaintenance().getId() : null,
                        m.getTypeMaintenance() != null ? m.getTypeMaintenance().getLibelle() : null,
                        m.getSuivi() != null ? m.getSuivi().getId() : null,
                        typeIncident,
                        typeInterventions
                );
            })
            .collect(Collectors.toList());
}




    // ===================== DELETE =====================
    @Override
    @Transactional
    public void deleteMaintenance(Long id) {
        Maintenance maintenance = maintenanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Maintenance non trouvée"));
        maintenanceRepository.delete(maintenance);
    }
}
