package com.example.demo.service.imp;

import com.example.demo.Repository.*;
import com.example.demo.model.*;
import com.example.demo.service.SuiviService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class SuiviServiceImp implements SuiviService {

    private static final Logger logger = LoggerFactory.getLogger(SuiviServiceImp.class);

    private final SuiviRepository suiviRepository;
    private final VehiculeRepository vehiculeRepository;
    private final NotificationRepository notificationRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final AlerteHistoriqueRepository alerteHistoriqueRepository;

    // Paramètres configurables
    @Value("${alertes.vidange.km}")
    private int kmVidange;

    @Value("${alertes.vidange.mois}")
    private int moisVidange;

    @Value("${alertes.assurance.jours}")
    private int joursAssuranceAlert;

    @Value("${alertes.maintenance.group}")
    private boolean groupNotifications;

    public SuiviServiceImp(SuiviRepository suiviRepository,
                           VehiculeRepository vehiculeRepository,
                           NotificationRepository notificationRepository,
                           UtilisateurRepository utilisateurRepository,
                           AlerteHistoriqueRepository alerteHistoriqueRepository) {
        this.suiviRepository = suiviRepository;
        this.vehiculeRepository = vehiculeRepository;
        this.notificationRepository = notificationRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.alerteHistoriqueRepository = alerteHistoriqueRepository;
    }

    @Override
    public Suivi save(Suivi suivi) {
        if (suivi.getAlertes() == null) {
            suivi.setAlertes(new ArrayList<>());
        }
        return suiviRepository.save(suivi);
    }

    @Override
    public Suivi findById(Long id) {
        return suiviRepository.findById(id).orElse(null);
    }

    @Override
    public List<Suivi> findAll() {
        return suiviRepository.findAll();
    }

    @Override
    public Suivi update(Long id, Suivi suivi) {
        Suivi existing = findById(id);
        if (existing != null) {
            existing.setDescription(suivi.getDescription());
            existing.setDateDerniersuivi(suivi.getDateDerniersuivi());
            existing.setDureeEstimé(suivi.getDureeEstimé());
            existing.setFrequence(suivi.getFrequence());
            return suiviRepository.save(existing);
        }
        return null;
    }

    @Override
    public void delete(Long id) {
        suiviRepository.deleteById(id);
    }

    // ✅ Correction : éviter erreur si plusieurs suivis existent
    @Override
    public Suivi getSuiviVehicule(Long idVehicule) {
        List<Suivi> suivis = suiviRepository.findByVehicule_IdVehicule(idVehicule);
        if (suivis.isEmpty()) return null;
        // On prend le plus récent
        return suivis.stream()
                .max(Comparator.comparing(Suivi::getDateDerniersuivi, Comparator.nullsLast(Comparator.naturalOrder())))
                .orElse(suivis.get(0));
    }

@Override
public List<String> getAlertesVehicule(Long idVehicule) {
    Optional<Vehicule> vehiculeOpt = vehiculeRepository.findById(idVehicule);
    if (vehiculeOpt.isEmpty()) return Collections.emptyList();

    Vehicule vehicule = vehiculeOpt.get();
    List<String> alertes = new ArrayList<>();

    // 🔹 1. Vérifier les assurances
    if (vehicule.getAssurances() != null && !vehicule.getAssurances().isEmpty()) {
        vehicule.getAssurances().forEach(assurance -> {
            LocalDate dateFin = convertToLocalDate(assurance.getDateFin());
            if (dateFin != null && dateFin.isBefore(LocalDate.now().plusDays(joursAssuranceAlert))) {
                alertes.add("Assurance à renouveler pour le véhicule " + vehicule.getImmatriculation());
            }
        });
    }

    // 🔹 2. Vérifier les maintenances
    if (vehicule.getMaintenances() != null && !vehicule.getMaintenances().isEmpty()) {
        vehicule.getMaintenances().forEach(maintenance -> {
            LocalDate dateMaintenance = convertToLocalDate(maintenance.getDateMaintenance());
            if (dateMaintenance != null &&
                !"Terminé".equalsIgnoreCase(maintenance.getStatut()) &&
                !dateMaintenance.isAfter(LocalDate.now())) {

                alertes.add("Maintenance prévue : " + maintenance.getTypeMaintenance());
            }
        });
    }

    // 🔹 3. Récupérer le dernier suivi (le plus récent)
    Suivi suivi = null;
    if (vehicule.getSuivis() != null && !vehicule.getSuivis().isEmpty()) {
        suivi = vehicule.getSuivis().stream()
                .filter(s -> s.getDateDerniersuivi() != null)
                .max(Comparator.comparing(Suivi::getDateDerniersuivi))
                .orElse(null);
    }

    // 🔹 4. Vérifier la vidange selon le dernier suivi
    if (suivi != null) {
        Integer kmActuel = vehicule.getKilometrageActuel();
        Integer kmDernierSuivi = suivi.getKilometreActuel();

        // Vérification kilométrage
        if (kmActuel != null && kmDernierSuivi != null && (kmActuel - kmDernierSuivi) >= kmVidange) {
            alertes.add("Vidange à faire (kilométrage dépassé)");
        }

        // Vérification date
        LocalDate dateDernierSuivi = convertToLocalDate(suivi.getDateDerniersuivi());
        if (dateDernierSuivi != null &&
            dateDernierSuivi.plusMonths(moisVidange).isBefore(LocalDate.now())) {

            alertes.add("Vidange à faire (plus de " + moisVidange + " mois écoulés)");
        }
    }

    return alertes;
}



    private LocalDate convertToLocalDate(Object date) {
        if (date instanceof LocalDate localDate) return localDate;
        if (date instanceof java.sql.Date sqlDate) return sqlDate.toLocalDate();
        if (date instanceof java.util.Date utilDate) return utilDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return null;
    }

    @Transactional
    @Scheduled(cron = "0 0 8 * * ?") // Tous les jours à 8h
    public void verifierAlertes() {
        List<Vehicule> vehicules = vehiculeRepository.findAll();
        vehicules.forEach(v -> {
            try {
                logger.info("Vérification alertes pour le véhicule ID : {}", v.getIdVehicule());
                mettreAJourAlertes(v.getIdVehicule());
            } catch (Exception e) {
                logger.error("Erreur vérification alertes pour le véhicule ID {} : {}", v.getIdVehicule(), e.getMessage());
            }
        });
    }

    @Transactional
    @Override
    public void mettreAJourAlertes(Long idVehicule) {
        List<Suivi> suivis = suiviRepository.findByVehicule_IdVehicule(idVehicule);
        if (suivis.isEmpty()) return;

        Suivi suivi = suivis.stream()
                .max(Comparator.comparing(Suivi::getDateDerniersuivi, Comparator.nullsLast(Comparator.naturalOrder())))
                .orElse(suivis.get(0));

        List<String> alertes = new ArrayList<>(getAlertesVehicule(idVehicule));
        boolean hasAlert = !alertes.isEmpty();

        suivi.setAlertes(alertes);
        suivi.setAlerteMaintenance(hasAlert);
        suiviRepository.save(suivi);

        if (!hasAlert) return;

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) return;

        String username = auth.getName();
        Utilisateur adminConnecte = utilisateurRepository.findByNom(username).orElse(null);
        if (adminConnecte == null) return;

        String messageNotif = groupNotifications
                ? "Véhicule " + suivi.getVehicule().getImmatriculation() + " : " + String.join(", ", alertes)
                : null;

        LocalDateTime today = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);

        for (String alert : alertes) {
            String msg = groupNotifications ? messageNotif : "Véhicule " + suivi.getVehicule().getImmatriculation() + " : " + alert;

            Optional<AlerteHistorique> exist = alerteHistoriqueRepository
                    .findByVehiculeAndDestinataireAndMessageAndDateEnvoiAfter(
                            suivi.getVehicule(), adminConnecte, msg, today);
            if (exist.isEmpty()) {
                Notification notif = new Notification();
                notif.setTitre("Alerte maintenance");
                notif.setMessage(msg);
                notif.setDateNotif(LocalDateTime.now());
                notif.setLue(false);
                notif.setDestinataire(adminConnecte);
                notificationRepository.save(notif);

                AlerteHistorique hist = new AlerteHistorique();
                hist.setVehicule(suivi.getVehicule());
                hist.setDestinataire(adminConnecte);
                hist.setMessage(msg);
                hist.setDateEnvoi(LocalDateTime.now());
                alerteHistoriqueRepository.save(hist);

                logger.info("Notification créée pour {}: {}", username, msg);
            }
        }
    }
    @Override
    public Suivi getDernierSuiviVehicule(Long idVehicule) {
        return suiviRepository
            .findTopByVehiculeIdVehiculeOrderByDateDerniersuiviDesc(idVehicule)
            .orElse(null);
    }
    
     @Override
    public String verifierEtatVehicule(Long idVehicule) {

        // 🔹 1. Récupérer le dernier suivi du véhicule
        Optional<Suivi> dernierSuiviOpt =
                suiviRepository.findTopByVehiculeIdVehiculeOrderByDateDerniersuiviDesc(idVehicule);

        if (dernierSuiviOpt.isEmpty()) {
            return "Aucun suivi trouvé pour ce véhicule.";
        }

        Suivi dernierSuivi = dernierSuiviOpt.get();

        // 🔹 2. Vérifier si le véhicule est en maintenance ou panne
        Vehicule vehicule = dernierSuivi.getVehicule();
        if (vehicule == null) {
            return "Aucun véhicule associé à ce suivi.";
        }

        EtatVehicule etatVehicule = vehicule.getEtat();

        if (etatVehicule == EtatVehicule.MAINTENANCE || etatVehicule == EtatVehicule.EN_PANNE) {
            return "Le véhicule est actuellement en " + etatVehicule.name().toLowerCase();
        }

        // 🔹 3. Calculer l’état à partir du score
        String etatCalcule = getEtatSimple(dernierSuivi);

        return "Dernier état calculé du véhicule : " + etatCalcule;
    }

    /**
     * 🔧 Méthode privée pour calculer un état simplifié du véhicule selon les indicateurs du suivi.
     */
    private String getEtatSimple(Suivi suivi) {
        int score = 0;
        int poids = 20;

        if (suivi.getKilometresDepuisFreins() > 20000) score += poids;
        if (suivi.getKilometresDepuisVidange() > 15000) score += poids;
        if (suivi.getDureeVieBatterie() > 24) score += poids;
        if (suivi.getNombrePannes() > 0) score += poids;
        if (suivi.getNombreAccidents() > 0) score += poids;

        if (score < 50) return "bon";
        else if (score <= 75) return "maintenance";
        else return "panne";
    }
    // 🔹 Implémentation de getSuivisParVehicule
    @Override
    public List<Suivi> getSuivisParVehicule(Long idVehicule) {
        Vehicule vehicule = vehiculeRepository.findById(idVehicule).orElse(null);
        if (vehicule == null) return List.of();
        return suiviRepository.findByVehiculeOrderByDateDerniersuiviDesc(vehicule);
    }

}
