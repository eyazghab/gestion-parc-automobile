package com.example.demo.service.imp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.example.demo.Dto.MissionCarburantDTO;
import com.example.demo.Dto.OrdreMissionDTO;
import com.example.demo.Repository.NotificationRepository;
import com.example.demo.Repository.OrdreMissionRepository;
import com.example.demo.Repository.SuiviRepository;
import com.example.demo.Repository.UtilisateurRepository;
import com.example.demo.Repository.VehiculeRepository;
import com.example.demo.model.EtatMission;
import com.example.demo.model.EtatVehicule;
import com.example.demo.model.Notification;
import com.example.demo.model.OrdreMission;
import com.example.demo.model.Suivi;
import com.example.demo.model.Utilisateur;
import com.example.demo.model.Vehicule;
import com.example.demo.service.DistanceService;
import com.example.demo.service.OrdreMissionService;

import jakarta.transaction.Transactional;

@Service
public class OrdreMissionServiceImpl implements OrdreMissionService {

    @Autowired
    private OrdreMissionRepository ordreMissionRepository;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private VehiculeRepository vehiculeRepository;

    @Autowired
    private NotificationRepository notificationRepository;
    
    @Autowired
    private DistanceService distanceService;
    
    @Autowired
    private SuiviRepository suiviRepository;
    
   
    @Override
    public OrdreMission saveOrdreMission(OrdreMission ordreMission) {
        return ordreMissionRepository.save(ordreMission);
    }

    @Override
    public List<OrdreMissionDTO> getAllOrdresMission() {
        return ordreMissionRepository.findAll()
                .stream()
                .map(OrdreMissionDTO::new)
                .collect(Collectors.toList()); 
    }

    @Override
    public OrdreMission getOrdreMissionById(Long id) {
        return ordreMissionRepository.findById(id).orElse(null);
    }

    @Override
    public void deleteOrdreMission(Long id) {
        ordreMissionRepository.deleteById(id);
    }

    @Override
   public OrdreMission createOrdreMission(OrdreMission mission) {
    if (mission.getUtilisateur() == null) {
        throw new IllegalArgumentException("Utilisateur non défini.");
    }

    if (mission.getDateRetour().isBefore(mission.getDateDepart())) {
        throw new IllegalArgumentException("La date de retour ne peut pas être antérieure à la date de départ.");
    }

    Optional<Utilisateur> utilisateurOpt = utilisateurRepository.findById(mission.getUtilisateur().getIdUtilisateur());
    if (utilisateurOpt.isEmpty()) {
        throw new IllegalArgumentException("Utilisateur introuvable.");
    }

    Utilisateur utilisateur = utilisateurOpt.get();

    // Vérifier chevauchement de mission utilisateur
    if (isUtilisateurEnMission(utilisateur.getIdUtilisateur(), mission.getDateDepart(), mission.getDateRetour())) {
        throw new IllegalStateException("Cet utilisateur a déjà une mission à cette période.");
    }

    mission.setUtilisateur(utilisateur);
    mission.setDateOrdre(LocalDateTime.now());
    mission.setValidee(false);
    mission.setEtat(EtatMission.EN_ATTENTE);

    // 🚫 Ne pas affecter de véhicule ici → admin le fera plus tard
    mission.setVehicule(null);

    // Calculer distance si nécessaire
    double distance = distanceService.calculerDistance(mission.getDestination());
    mission.setDistanceEstimee(distance);

    // Carburant nécessaire = 0 tant que pas de véhicule choisi
    mission.setCarburantNecessaire(0.0);

    OrdreMission saved = ordreMissionRepository.save(mission);

    // Notifier les admins
    List<Utilisateur> admins = utilisateurRepository.findByRole("ADMIN");
    for (Utilisateur admin : admins) {
        Notification notif = new Notification();
        notif.setTitre("Nouvelle demande d'ordre de mission");
        notif.setMessage("L'utilisateur " + utilisateur.getNom() + " a créé une nouvelle demande de mission (sans véhicule).");
        notif.setDateNotif(LocalDateTime.now());
        notif.setLue(false);
        notif.setDestinataire(admin);
        notificationRepository.save(notif);
    }

    return saved;
}


    @Override
    public boolean isVehiculeEnMission(Long vehiculeId, LocalDateTime dateDepart, LocalDateTime dateRetour) {
        List<OrdreMission> missions = ordreMissionRepository
            .findByVehiculeIdVehiculeAndEtatIn(
                vehiculeId, 
                List.of(EtatMission.EN_ATTENTE, EtatMission.ACCEPTEE)
            );

        for (OrdreMission mission : missions) {
            if (datesChevauchent(
                    mission.getDateDepart(), 
                    mission.getDateRetour(), 
                    dateDepart, 
                    dateRetour)) {
                return true; // chevauchement trouvé => refuser la nouvelle mission
            }
        }
        return false; // pas de chevauchement => accepter la nouvelle mission
    }


    @Override
    public boolean isUtilisateurEnMission(Long utilisateurId, LocalDateTime dateDepart, LocalDateTime dateRetour) {
        List<OrdreMission> missions = ordreMissionRepository
                .findByUtilisateurIdUtilisateurAndEtatIn(utilisateurId, List.of(EtatMission.EN_ATTENTE, EtatMission.ACCEPTEE));

        for (OrdreMission mission : missions) {
            if (datesChevauchent(mission.getDateDepart(), mission.getDateRetour(), dateDepart, dateRetour)) {
                return true;
            }
        }
        return false;
    }

    private boolean datesChevauchent(LocalDateTime debut1, LocalDateTime fin1, LocalDateTime debut2, LocalDateTime fin2) {
        return !debut2.isAfter(fin1) && !fin2.isBefore(debut1);
    }

    @Override
    public List<OrdreMission> findByEtatIn(List<String> etats) {
        List<EtatMission> etatsEnum = etats.stream()
                .map(String::toUpperCase)
                .map(EtatMission::valueOf)
                .collect(Collectors.toList());
        return ordreMissionRepository.findByEtatIn(etatsEnum);
    }

    @Transactional
    @Override
    public OrdreMission changerEtatEtNotifier(Long idMission, EtatMission nouvelEtat) {
    OrdreMission mission = ordreMissionRepository.findById(idMission)
            .orElseThrow(() -> new RuntimeException("Mission non trouvée avec l'ID : " + idMission));

    mission.setEtat(nouvelEtat);

    Vehicule vehicule = mission.getVehicule();
    if ((nouvelEtat == EtatMission.ANNULEE || nouvelEtat == EtatMission.REFUSEE) && vehicule != null) {
        vehicule.setEtat(EtatVehicule.DISPONIBLE);
        vehiculeRepository.save(vehicule);
    } else if (nouvelEtat == EtatMission.ACCEPTEE && vehicule != null) {
        vehicule.setEtat(EtatVehicule.RESERVE);
        vehiculeRepository.save(vehicule);
    }

    OrdreMission missionMiseAJour = ordreMissionRepository.save(mission);

    Notification notification = new Notification();
    notification.setTitre("Mise à jour de votre mission");

    // Inclure l'immatriculation du véhicule si disponible
    String immatriculation = (vehicule != null && vehicule.getImmatriculation() != null)
            ? vehicule.getImmatriculation()
            : "aucun véhicule assigné";

    String message;
    switch (nouvelEtat) {
        case ACCEPTEE:
            message = "Votre demande de mission a été acceptée. Véhicule assigné : " + immatriculation;
            break;
        case REFUSEE:
            message = "Votre demande de mission a été refusée. Véhicule assigné : " + immatriculation;
            break;
        case ANNULEE:
            message = "Votre mission a été annulée. Le véhicule est désormais disponible : " + immatriculation;
            break;
        default:
            message = "L'état de votre mission a été mis à jour : " + nouvelEtat.name() +
                      ". Véhicule assigné : " + immatriculation;
            break;
    }

    notification.setMessage(message);
    notification.setDateNotif(LocalDateTime.now());
    notification.setLue(false);
    notification.setDestinataire(mission.getUtilisateur());

    notificationRepository.save(notification);

    return missionMiseAJour;
}


    @Override
    public List<OrdreMissionDTO> getOrdresMissionByUtilisateurId(Long idUtilisateur) {
        List<OrdreMission> ordres = ordreMissionRepository.findByUtilisateurIdUtilisateur(idUtilisateur);
        // Conversion en DTO
        return ordres.stream()
                     .map(OrdreMissionDTO::new)
                     .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public OrdreMission annulerOrdreMission(Long idOrdreMission) {
        OrdreMission ordre = ordreMissionRepository.findById(idOrdreMission)
                .orElseThrow(() -> new RuntimeException("Ordre de mission non trouvé avec ID : " + idOrdreMission));

        ordre.setEtat(EtatMission.ANNULEE);

        Vehicule vehicule = ordre.getVehicule();
        if (vehicule != null) {
            vehicule.setEtat(EtatVehicule.DISPONIBLE);
            vehiculeRepository.save(vehicule);
        }

        return ordreMissionRepository.save(ordre);
    }

    /*@Override
    public void mettreAJourEtatDesMissions() {
        List<OrdreMission> toutesLesMissions = ordreMissionRepository.findAll();
        LocalDateTime maintenant = LocalDateTime.now();

        for (OrdreMission mission : toutesLesMissions) {
            if (mission.getEtat() == EtatMission.ACCEPTEE || mission.getEtat() == EtatMission.EN_COURS) {
                if (maintenant.isAfter(mission.getDateRetour())) {
                    mission.setEtat(EtatMission.TERMINEE);
                    ordreMissionRepository.save(mission);
                } else if (!maintenant.isBefore(mission.getDateDepart()) && maintenant.isBefore(mission.getDateRetour())) {
                    mission.setEtat(EtatMission.EN_COURS);
                    ordreMissionRepository.save(mission);
                }
            }
        }
    }
    // Méthode exécutée toutes les 5 minutes (exemple)
    @Scheduled(fixedRate = 300000)  // 300000 ms = 5 minutes
    public void tachePlanifieeMiseAJourEtat() {
        mettreAJourEtatDesMissions();
    }*/
@Override
public OrdreMission demarrerMission(Long idMission) {
    OrdreMission mission = ordreMissionRepository.findById(idMission)
            .orElseThrow(() -> new RuntimeException("Mission introuvable"));

    if (mission.getEtat() != EtatMission.ACCEPTEE) {
        throw new RuntimeException("La mission ne peut pas être démarrée car son état est : " + mission.getEtat());
    }

    // ✅ Sauvegarde de la date de départ réelle automatiquement
    mission.setDateDepartReelle(LocalDateTime.now());

    // Passer mission à EN_COURS
    mission.setEtat(EtatMission.EN_COURS);

    // Sauvegarde
    ordreMissionRepository.save(mission);

    // Véhicule reste réservé
    Vehicule vehicule = mission.getVehicule();
    if (vehicule != null) {
        vehicule.setEtat(EtatVehicule.RESERVE);
        vehiculeRepository.save(vehicule);
    }

    return mission;
}

@Override
@Transactional
public OrdreMission terminerMission(Long id) {
    // Récupération de la mission
    OrdreMission mission = ordreMissionRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Mission non trouvée"));

    if (mission.getEtat() != EtatMission.EN_COURS) {
        throw new IllegalArgumentException("Seules les missions en cours peuvent être terminées.");
    }

    // ✅ Mise à jour automatique de la date retour réelle
    mission.setDateRetourReelle(LocalDateTime.now());

    // Si pas déjà définie (sécurité)
    if (mission.getDateDepartReelle() == null) {
        mission.setDateDepartReelle(LocalDateTime.now());
    }

    // Changement de l'état de la mission
    mission.setEtat(EtatMission.TERMINEE);

    // Libération du véhicule
    Vehicule vehicule = mission.getVehicule();
    if (vehicule != null) {
        Vehicule vehiculeBD = vehiculeRepository.findById(vehicule.getIdVehicule())
                .orElseThrow(() -> new IllegalArgumentException("Véhicule non trouvé"));
        vehiculeBD.setEtat(EtatVehicule.DISPONIBLE);
        vehiculeRepository.save(vehiculeBD);
    }

    return ordreMissionRepository.save(mission);
}

   @Override
   public List<OrdreMission> findByEtat(EtatMission etat) {
       return ordreMissionRepository.findByEtat(etat);
   }
@Override
@Transactional
public OrdreMissionDTO assignVehicule(Long missionId, Long vehiculeId) {
    // 1️⃣ Récupération des entités
    OrdreMission mission = ordreMissionRepository.findById(missionId)
            .orElseThrow(() -> new IllegalArgumentException("Mission introuvable"));

    Vehicule vehicule = vehiculeRepository.findById(vehiculeId)
            .orElseThrow(() -> new IllegalArgumentException("Véhicule introuvable"));

    // 2️⃣ Vérification chevauchement
    if (isVehiculeEnMission(vehicule.getIdVehicule(), mission.getDateDepart(), mission.getDateRetour())) {
        throw new IllegalStateException("Ce véhicule est déjà en mission à cette période.");
    }

    // 3️⃣ Assignation véhicule & mise à jour état mission
    mission.setVehicule(vehicule);
    mission.setEtat(EtatMission.ACCEPTEE);

    // 4️⃣ Calcul consommation carburant
    double consommationMoyenne = (vehicule.getConsommationMoyenne() != null) ? vehicule.getConsommationMoyenne() : 8.0;
    mission.setCarburantNecessaire(
            mission.getDistanceEstimee() != null ? mission.getDistanceEstimee() * consommationMoyenne / 100.0 : 0
    );

    // 5️⃣ Mise à jour état véhicule
    vehicule.setEtat(EtatVehicule.RESERVE);

    // 6️⃣ Incrémentation kilométrage du véhicule
    int kmAvant = vehicule.getKilometrageActuel() != null ? vehicule.getKilometrageActuel() : 0;
    double kmParcourus = mission.getDistanceEstimee() != null ? mission.getDistanceEstimee() : 0;
    vehicule.setKilometrageActuel(kmAvant + (int) kmParcourus);
    vehiculeRepository.save(vehicule);

    // 7️⃣ Mise à jour du suivi
    Suivi suivi = suiviRepository.findTopByVehiculeIdVehiculeOrderByDateDerniersuiviDesc(vehicule.getIdVehicule())
            .orElseGet(() -> {
                Suivi s = new Suivi();
                s.setVehicule(vehicule);
                s.setKilometreActuel(0);
                s.setKilometresDepuisVidange(0);
                s.setKilometresDepuisFreins(0);
                s.setNombreAccidents(0);
                s.setNombrePannes(0);
                s.setDateDerniersuivi(LocalDate.now());
                return s;
            });

    suivi.setKilometreActuel(suivi.getKilometreActuel() + (int) kmParcourus);
    suivi.setKilometresDepuisVidange(suivi.getKilometresDepuisVidange() + (int) kmParcourus);
    suivi.setKilometresDepuisFreins(suivi.getKilometresDepuisFreins() + (int) kmParcourus);
    suivi.setDateDerniersuivi(LocalDate.now());
    suiviRepository.save(suivi);

    // 8️⃣ Sauvegarde de la mission
    ordreMissionRepository.save(mission);

    // 9️⃣ Création de la notification
    Notification notification = new Notification();
    notification.setTitre("Mission acceptée");
    notification.setMessage("Votre mission a été acceptée. Véhicule assigné : " +
            vehicule.getImmatriculation() +
            " | Distance prévue : " + kmParcourus + " km");
    notification.setDateNotif(LocalDateTime.now());
    notification.setLue(false);
    notification.setDestinataire(mission.getUtilisateur());
    notificationRepository.save(notification);

    // 🔟 Retourner DTO pour Angular
    return new OrdreMissionDTO(mission);
}




public Suivi getDernierSuiviDuVehicule(Long idVehicule) {
    // 1️⃣ Récupérer le véhicule
    Vehicule vehicule = vehiculeRepository.findById(idVehicule)
            .orElseThrow(() -> new IllegalArgumentException("Véhicule introuvable"));

    // 2️⃣ Récupérer le suivi le plus récent
    return suiviRepository.findTopByVehiculeIdVehiculeOrderByDateDerniersuiviDesc(vehicule.getIdVehicule())
            .orElseGet(() -> {
                // Si aucun suivi existant, créer un suivi vierge
                Suivi s = new Suivi();
                s.setVehicule(vehicule);
                s.setKilometreActuel(0);
                s.setKilometresDepuisVidange(0);
                s.setKilometresDepuisFreins(0);
                s.setNombreAccidents(0);
                s.setNombrePannes(0);
                s.setDateDerniersuivi(LocalDate.now());
                return s;
            });
}




  @Override
  public List<MissionCarburantDTO> getMissionsByBonId(Long bonId) {
      OrdreMission mission = ordreMissionRepository.findMissionByBonId(bonId);
      if (mission == null) {
          return List.of(); // liste vide
      }
      return List.of(new MissionCarburantDTO(mission));
  }
}
