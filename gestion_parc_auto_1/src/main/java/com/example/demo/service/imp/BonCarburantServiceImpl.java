package com.example.demo.service.imp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.example.demo.Dto.BonCarburantCreateDTO;
import com.example.demo.Dto.BonCarburantDTO;
import com.example.demo.Dto.VehiculeSimpleDTO;
import com.example.demo.Repository.BonCarburantRepository;
import com.example.demo.Repository.CarburantRepository;
import com.example.demo.Repository.NotificationRepository;
import com.example.demo.Repository.OrdreMissionRepository;
import com.example.demo.Repository.UtilisateurRepository;
import com.example.demo.Repository.VehiculeRepository;
import com.example.demo.model.BonCarburant;
import com.example.demo.model.BonCarburant.EtatBon;
import com.example.demo.model.Carburant;
import com.example.demo.model.Notification;
import com.example.demo.model.OrdreMission;
import com.example.demo.model.Utilisateur;
import com.example.demo.model.Vehicule;
import com.example.demo.service.BonCarburantService;

@Service
public class BonCarburantServiceImpl implements BonCarburantService {

    @Autowired
    private BonCarburantRepository bonRepo;
    
    @Autowired
    private UtilisateurRepository utilisateurRepo;

   @Autowired 
   private VehiculeRepository vehiculeRepo;
   
   @Autowired
   private NotificationRepository notificationRepository;
   
   @Autowired
   private CarburantServiceImpl carburantServiceimp;
    
    @Autowired
    private CarburantRepository carburantRepo;
    
    @Autowired
    private OrdreMissionRepository missionRepo;
@Override
public BonCarburant addBon(BonCarburantCreateDTO dto) {

    // 1️⃣ Valider les IDs reçus
    if (dto.getVehiculeId() == null) {
        throw new IllegalArgumentException("L'ID du véhicule est obligatoire.");
    }
    if (dto.getCarburantId() == null) {
        throw new IllegalArgumentException("L'ID du carburant est obligatoire.");
    }
    if (dto.getUtilisateurId() == null) {
        throw new IllegalArgumentException("L'ID de l'utilisateur est obligatoire.");
    }
    if (dto.getMissionId() == null) {
        throw new IllegalArgumentException("L'ID de la mission est obligatoire.");
    }

    // 2️⃣ Récupérer les entités depuis la base
    Vehicule v = vehiculeRepo.findById(dto.getVehiculeId())
            .orElseThrow(() -> new RuntimeException("Véhicule introuvable pour l'ID : " + dto.getVehiculeId()));

    Carburant c = carburantRepo.findById(dto.getCarburantId())
            .orElseThrow(() -> new RuntimeException("Carburant introuvable pour l'ID : " + dto.getCarburantId()));

    Utilisateur u = utilisateurRepo.findById(dto.getUtilisateurId())
            .orElseThrow(() -> new RuntimeException("Utilisateur introuvable pour l'ID : " + dto.getUtilisateurId()));

    OrdreMission mission = missionRepo.findById(dto.getMissionId())
            .orElseThrow(() -> new RuntimeException("Mission introuvable pour l'ID : " + dto.getMissionId()));

    // 3️⃣ Créer le bon carburant
    BonCarburant bon = new BonCarburant();
    bon.setVehicule(v);
    bon.setCarburant(c);
    bon.setUtilisateur(u);
    bon.setMission(mission);  // 👈 Lier la mission

    // Quantité
    if (dto.getQuantite() == null || dto.getQuantite() <= 0) {
        throw new IllegalArgumentException("La quantité doit être supérieure à 0.");
    }
    bon.setQuantite(dto.getQuantite());

    // Montant (autorisé à être null)
    if (dto.getMontant() != null && dto.getMontant() < 0) {
        throw new IllegalArgumentException("Le montant doit être positif.");
    }
    bon.setMontant(dto.getMontant());  // null possible

    // Date achat
    bon.setDateAchat(dto.getDateAchat() != null ? dto.getDateAchat() : new Date());

    // État
    bon.setEtat(dto.getEtat() != null ? dto.getEtat() : EtatBon.EN_ATTENTE);

    // 4️⃣ Notification admin (facultatif)
    try {
        Utilisateur admin = utilisateurRepo.findById(1L).orElse(null);
        if (admin != null) {
            Notification notif = new Notification();
            notif.setMessage("Nouveau bon carburant créé par " + u.getNom() +
                             " pour le véhicule " + v.getImmatriculation() +
                             " (Mission : " + mission.getDestination() + ")");
            notif.setDestinataire(admin);
            notif.setDateNotif(LocalDateTime.now());
            notificationRepository.save(notif);
        }
    } catch (Exception e) {
        e.printStackTrace(); // ne bloque pas la création
    }

    // 5️⃣ Sauvegarder le bon
    try {
        return bonRepo.save(bon);
    } catch (Exception e) {
        e.printStackTrace();
        throw new RuntimeException("Erreur lors de la sauvegarde du bon carburant : " + e.getMessage(), e);
    }
}





    @Override
    public List<BonCarburant> getBonsByVehicule(Long idVehicule) {
        return bonRepo.findByVehiculeIdVehicule(idVehicule);
    }


    @Override
    public List<BonCarburantDTO> getTousLesBons() {
        return bonRepo.findAll().stream()
            .map(b -> new BonCarburantDTO(
                b.getId(),
                b.getMontant(),
                b.getQuantite(),
                b.getDateAchat(),
                b.getResponsable(),
                b.getVehicule() != null ? new VehiculeSimpleDTO(
                    b.getVehicule().getIdVehicule(),
                    b.getVehicule().getImmatriculation()
                ) : null,
                b.getCarburant() != null ? b.getCarburant().getTypeCarburant() : null, // nom carburant
                b.getUtilisateur() != null ? b.getUtilisateur().getNom() : null,      // nom utilisateur
                b.getEtat()
            ))
            .collect(Collectors.toList());
    }



    @Override
    public void supprimerBon(Long id) {
        bonRepo.deleteById(id);
    }
   

    @Override
    public double getConsommationTotale(Long vehiculeId) {
        List<BonCarburant> bons = bonRepo.findByVehiculeIdVehicule(vehiculeId);
        return bons.stream().mapToDouble(BonCarburant::getQuantite).sum();
    }
    
   @Override
public BonCarburant demanderBon(Vehicule vehicule, Carburant carburant, Double quantite, Long utilisateurId) {
    Utilisateur user = utilisateurRepo.findById(utilisateurId)
                      .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

    BonCarburant bon = new BonCarburant();
    bon.setVehicule(vehicule);
    bon.setCarburant(carburant);
    bon.setQuantite(quantite);
    bon.setUtilisateur(user);
    bon.setEtat(EtatBon.EN_ATTENTE);
    bon.setDateDemande(LocalDate.now());

    return bonRepo.save(bon);
}

   
@Override
public BonCarburant validerBon(Long bonId, String action) {
    BonCarburant bon = bonRepo.findById(bonId)
                       .orElseThrow(() -> new RuntimeException("Bon non trouvé"));

    String messageNotif;

    if ("ACCEPTE".equalsIgnoreCase(action)) {

        // 1️⃣ Calcul du carburant nécessaire pour les missions validées
        double necessaire = carburantServiceimp.calculCarburantNecessaire(bon.getVehicule());

        // 2️⃣ Somme des bons déjà acceptés pour ce véhicule (hors celui qu'on valide)
        double dejaAttribue = bonRepo.findByVehiculeIdVehicule(bon.getVehicule().getIdVehicule())
                                     .stream()
                                     .filter(bc -> bc.getEtat() == BonCarburant.EtatBon.ACCEPTE && !bc.getId().equals(bonId))
                                     .mapToDouble(BonCarburant::getQuantite)
                                     .sum();

        // 3️⃣ Quantité restante disponible pour valider ce bon
        double restant = necessaire - dejaAttribue;

        // 4️⃣ Vérification que la quantité du bon modifié ne dépasse pas le restant
        if (bon.getQuantite() > restant) {
            messageNotif = "Votre bon carburant #" + bon.getId() + " pour le véhicule " +
                           bon.getVehicule().getImmatriculation() +
                           " ne peut pas être validé. Quantité maximale disponible : " + restant + " L.";

            if (bon.getUtilisateur() != null) {
                Notification notif = new Notification();
                notif.setMessage(messageNotif);
                notif.setDestinataire(bon.getUtilisateur());
                notif.setDateNotif(LocalDateTime.now());
                notificationRepository.save(notif);
            }

            throw new RuntimeException(messageNotif);
        }

        // ✅ Validation du bon
        bon.setEtat(BonCarburant.EtatBon.ACCEPTE);
        messageNotif = "Votre bon carburant #" + bon.getId() + " pour le véhicule " +
                       bon.getVehicule().getImmatriculation() + " a été accepté ✅.";

    } else if ("REFUSE".equalsIgnoreCase(action)) {
        bon.setEtat(BonCarburant.EtatBon.REFUSE);
        messageNotif = "Votre bon carburant #" + bon.getId() + " a été refusé ❌.";
    } else {
        throw new RuntimeException("Action non reconnue (ACCEPTE ou REFUSE attendu)");
    }

    bon.setDateValidation(LocalDate.now());
    BonCarburant savedBon = bonRepo.save(bon);

    if (bon.getUtilisateur() != null) {
        Notification notif = new Notification();
        notif.setMessage(messageNotif);
        notif.setDestinataire(bon.getUtilisateur());
        notif.setDateNotif(LocalDateTime.now());
        notificationRepository.save(notif);
    }

    return savedBon;
}



   @Override
   public List<BonCarburantDTO> getBonsByUtilisateur(Long utilisateurId) {
       return bonRepo.findByUtilisateurIdUtilisateur(utilisateurId).stream()
           .map(b -> new BonCarburantDTO(
               b.getId(),
               b.getMontant(),
               b.getQuantite(),
               b.getDateAchat(),
               b.getResponsable(),
               b.getVehicule() != null ? new VehiculeSimpleDTO(
                   b.getVehicule().getIdVehicule(),
                   b.getVehicule().getImmatriculation()
               ) : null,
               b.getCarburant() != null ? b.getCarburant().getTypeCarburant() : null, // nom carburant
               b.getUtilisateur() != null ? b.getUtilisateur().getNom() : null,       // nom utilisateur
               b.getEtat()
           ))
           .collect(Collectors.toList());
   }
 public BonCarburant modifierQuantite(Long id, double quantite) {
    BonCarburant bon = bonRepo.findById(id)
            .orElseThrow(() -> new RuntimeException("Bon non trouvé"));

    // Mettre à jour la quantité
    bon.setQuantite(quantite);

    // Recalculer le montant seulement si le carburant et son prix sont définis
    if (bon.getCarburant() != null && bon.getCarburant().getPrix() != null) {
        double prixParLitre = bon.getCarburant().getPrix();
        bon.setMontant(quantite * prixParLitre);
    }

    return bonRepo.save(bon);
}


   @Override
   public List<BonCarburant> findByEtat(EtatBon etat) {
       return bonRepo.findByEtat(etat);
   }
   @Override
   public double verifierBon(Long bonId) {
       BonCarburant bon = bonRepo.findById(bonId)
               .orElseThrow(() -> new RuntimeException("Bon non trouvé"));

       // 1️⃣ Calcul du carburant nécessaire pour les missions ACCEPTÉES
       double necessaire = carburantServiceimp.calculCarburantNecessaire(bon.getVehicule());

       // 2️⃣ Somme des bons déjà acceptés (hors celui qu'on vérifie)
       double dejaAttribue = bonRepo.findByVehiculeIdVehicule(bon.getVehicule().getIdVehicule())
                                    .stream()
                                    .filter(bc -> bc.getEtat() == BonCarburant.EtatBon.ACCEPTE
                                            && !bc.getId().equals(bonId))
                                    .mapToDouble(BonCarburant::getQuantite)
                                    .sum();

       // 3️⃣ Quantité maximale disponible
       double restant = necessaire - dejaAttribue;

       if (restant < 0) restant = 0;

       return restant;
   }

}



