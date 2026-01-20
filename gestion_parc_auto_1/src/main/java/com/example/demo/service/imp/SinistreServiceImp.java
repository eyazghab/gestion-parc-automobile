package com.example.demo.service.imp;

import com.example.demo.Repository.MaintenanceRepository;
import com.example.demo.Repository.NotificationRepository;
import com.example.demo.Repository.SinistreRepository;
import com.example.demo.Repository.SuiviRepository;
import com.example.demo.Repository.TechnicienRepository;
import com.example.demo.Repository.UtilisateurRepository;
import com.example.demo.Repository.VehiculeRepository;
import com.example.demo.model.Circulation;
import com.example.demo.model.EtatSinistre;
import com.example.demo.model.EtatVehicule;
import com.example.demo.model.Maintenance;
import com.example.demo.model.Notification;
import com.example.demo.model.Sinistre;
import com.example.demo.model.Suivi;
import com.example.demo.model.Technicien;
import com.example.demo.model.Utilisateur;
import com.example.demo.model.Vehicule;
import com.example.demo.service.SinistreService;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class SinistreServiceImp implements SinistreService {

    @Autowired
    private SinistreRepository sinistreRepository;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private VehiculeRepository vehiculeRepository;
    
    @Autowired
    private TechnicienRepository technicienRepository;
    
    @Autowired
    private MaintenanceRepository maintenanceRepository;
    
    @Autowired
    private SuiviRepository suiviRepository;
    
    private final String uploadDir = "uploads/sinistres/";
@Override
@Transactional
public Sinistre saveSinistreEtNotifier(Sinistre sinistre, MultipartFile[] files) {
    // 0️⃣ Initialiser état si null
    if (sinistre.getEtat() == null) {
        sinistre.setEtat(EtatSinistre.DECLARE);
    }

    // 1️⃣ Date déclaration
    if (sinistre.getDateDeclaration() == null) {
        sinistre.setDateDeclaration(new Date());
    }

    // 2️⃣ Numéro unique
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
    String dateStr = sdf.format(sinistre.getDateDeclaration());
    int countToday = sinistreRepository.countByDateDeclaration(stripTime(sinistre.getDateDeclaration()));
    sinistre.setNumDeclaration(String.format("S-%s-%03d", dateStr, countToday + 1));

    // 3️⃣ Véhicule
    Vehicule vehicule = vehiculeRepository.findById(sinistre.getVehicule().getIdVehicule())
            .orElseThrow(() -> new RuntimeException("Véhicule non trouvé"));

    if (vehicule.getEtat() != EtatVehicule.ACCIDENTE) {
        vehicule.setEtat(EtatVehicule.ACCIDENTE);
        vehiculeRepository.save(vehicule);
    }
    sinistre.setVehicule(vehicule);

    // 4️⃣ Upload fichiers
    List<String> photoUrls = new ArrayList<>();
    if (files != null) {
        Path uploadPath = Paths.get(uploadDir);
        try { Files.createDirectories(uploadPath); } 
        catch (IOException e) { throw new RuntimeException("Impossible de créer le dossier d'upload", e); }

        for (MultipartFile file : files) {
            try {
                String cleanFilename = System.currentTimeMillis() + "_" +
                        file.getOriginalFilename().replaceAll("[^a-zA-Z0-9\\.\\-]", "_");
                Path filePath = uploadPath.resolve(cleanFilename);
                Files.write(filePath, file.getBytes());

                String fileUrl = "http://localhost:8090/uploads/sinistres/" + cleanFilename;
                photoUrls.add(fileUrl);
            } catch (IOException e) {
                throw new RuntimeException("Erreur lors de l'upload du fichier: " + file.getOriginalFilename(), e);
            }
        }
    }
    sinistre.setPhotos(photoUrls);

  
 // 5️⃣ Mettre à jour le suivi du véhicule
    Suivi suivi = suiviRepository.findByVehicule_IdVehicule(vehicule.getIdVehicule())
                     .stream()
                     .findFirst() // prend le premier si existant
                     .orElseGet(() -> {
                         Suivi newSuivi = new Suivi();
                         newSuivi.setVehicule(vehicule);
                         newSuivi.setNombreAccidents(0);
                         newSuivi.setNombrePannes(0);
                         return newSuivi;
                     });

    // 🔹 Incrémenter le compteur selon le type de sinistre
    if (sinistre.getTypeIncident() != null) {
        switch (sinistre.getTypeIncident()) {
            case ACCIDENT:
                suivi.setNombreAccidents(suivi.getNombreAccidents() + 1);
                break;
            case PANNE:
                suivi.setNombrePannes(suivi.getNombrePannes() + 1);
                break;
            default:
                // Aucun compteur à incrémenter
        }
    }

    // Sauvegarder le suivi (nouveau ou existant)
    suiviRepository.save(suivi);

    suiviRepository.save(suivi);

    // 6️⃣ Sauvegarder le sinistre
    Sinistre savedSinistre = sinistreRepository.save(sinistre);

    // 7️⃣ Notifications admins
    List<Utilisateur> admins = utilisateurRepository.findByRole("ADMIN");
    for (Utilisateur admin : admins) {
        Notification notif = new Notification();
        notif.setTitre("Nouveau sinistre déclaré");
        notif.setMessage("Un sinistre a été déclaré pour le véhicule " + vehicule.getImmatriculation() +
                         " (N° " + savedSinistre.getNumDeclaration() + ")");
        notif.setDateNotif(LocalDateTime.now());
        notif.setLue(false);
        notif.setDestinataire(admin);
        notificationRepository.save(notif);
    }

    return savedSinistre;
}








@Override
public Sinistre changerEtat(Long id, EtatSinistre nouvelEtat) {
    Sinistre sinistre = sinistreRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Sinistre introuvable"));

    Vehicule vehicule = sinistre.getVehicule();

    switch (nouvelEtat) {
        case A_MAINTENIR:
            sinistre.setEtat(EtatSinistre.A_MAINTENIR);
            break;

        case PAS_DE_TRAITEMENT_NECESSAIRE:
            sinistre.setEtat(EtatSinistre.PAS_DE_TRAITEMENT_NECESSAIRE);
            if (vehicule != null) {
                vehicule.setEtat(EtatVehicule.DISPONIBLE);
                vehiculeRepository.save(vehicule);
            }
            break;

        case PRISE_EN_CHARGE:
            sinistre.setEtat(EtatSinistre.PRISE_EN_CHARGE);
            if (vehicule != null) {
                vehicule.setEtat(EtatVehicule.MAINTENANCE);
                vehiculeRepository.save(vehicule);
            }
            break;

        case DECLARE:
            sinistre.setEtat(EtatSinistre.DECLARE);
            if (vehicule != null) {
                vehicule.setEtat(EtatVehicule.ACCIDENTE);
                vehiculeRepository.save(vehicule);
            }
            break;

        default:
            sinistre.setEtat(nouvelEtat);
    }

    return sinistreRepository.save(sinistre);
}




@Override
@Transactional
public Sinistre prendreEnCharge(Long idSinistre, Long idTechnicien, Circulation circulation) {
    Sinistre sinistre = sinistreRepository.findById(idSinistre)
            .orElseThrow(() -> new RuntimeException("Sinistre introuvable"));

    // ⚡ Mettre à jour état du sinistre
    sinistre.setEtat(EtatSinistre.PRISE_EN_CHARGE);
    sinistre.setCirculation(circulation);

    // ⚡ Récupérer véhicule et modifier état
    Vehicule vehicule = sinistre.getVehicule();
    if (vehicule != null) {
        if (circulation == Circulation.INTERDITE) {
            vehicule.setEtat(EtatVehicule.EN_PANNE);
        } else {
            vehicule.setEtat(EtatVehicule.MAINTENANCE);
        }
        vehiculeRepository.save(vehicule);
    }

    // ⚡ Affecter un technicien et créer la maintenance
    Technicien technicien = technicienRepository.findById(idTechnicien)
            .orElseThrow(() -> new RuntimeException("Technicien introuvable"));

    Maintenance maintenance = new Maintenance();
    maintenance.setSinistre(sinistre);
    maintenance.setTechnicien(technicien);
    maintenance.setDateMaintenance(LocalDate.now());
    maintenance.setStatut("Planifie");
    maintenanceRepository.save(maintenance);

    return sinistreRepository.save(sinistre);
}



    private Date stripTime(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    @Override
    public Sinistre findById(Long id) {
        return sinistreRepository.findById(id).orElse(null);
    }

    @Override
    public List<Sinistre> findAll() {
        return sinistreRepository.findAll();
    }

    @Override
    public Sinistre update(Long id, Sinistre sinistre) {
        if (sinistreRepository.existsById(id)) {
            sinistre.setId(id);
            return sinistreRepository.save(sinistre);
        }
        return null;
    }

    @Override
    public void delete(Long id) {
        sinistreRepository.deleteById(id);
    }

    @Override
    public List<Sinistre> getAllSinistresWithVehicule() {
        return sinistreRepository.findAllWithVehicule();
    }
    @Override
    public List<Sinistre> findByEtat(EtatSinistre etat) {
        return sinistreRepository.findByEtat(etat);
    }
}
