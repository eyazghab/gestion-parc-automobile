package com.example.demo.Controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.Repository.NotificationRepository;
import com.example.demo.Repository.UtilisateurRepository;
import com.example.demo.model.Notification;
import com.example.demo.model.Utilisateur;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "*") // pour autoriser les requêtes depuis Angular
public class NotificationController {

    @Autowired
    private NotificationRepository notificationRepository;

    // 📥 Créer une notification
    @Autowired
    private UtilisateurRepository utilisateurRepository;

   @PostMapping("/creer")
public ResponseEntity<Notification> creerNotification(@RequestBody Notification notification) {
    // Vérifie que l'ID du destinataire est présent
    if (notification.getDestinataire() == null || notification.getDestinataire().getIdUtilisateur() == null) {
        return ResponseEntity.badRequest().build();
    }

    // Recherche de l'utilisateur dans la base
    Optional<Utilisateur> destinataireOpt = utilisateurRepository.findById(notification.getDestinataire().getIdUtilisateur());

    // Si utilisateur non trouvé, retour erreur
    if (destinataireOpt.isEmpty()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    // Réassocier proprement l'utilisateur depuis la base
    Utilisateur destinataire = destinataireOpt.get();
    notification.setDestinataire(destinataire);

    // Initialisation des champs par défaut
    notification.setLue(false);
    notification.setDateNotif(LocalDateTime.now());

    // Enregistrement en base
    Notification savedNotif = notificationRepository.save(notification);

    return ResponseEntity.ok(savedNotif);
}


    // 🔔 Obtenir les notifications d’un utilisateur (admin)
    @GetMapping("/admin/{idUtilisateur}")
    public List<Notification> getNotificationsPourAdmin(@PathVariable Long idUtilisateur) {
        return notificationRepository.findByDestinataireIdUtilisateurOrderByDateNotifDesc(idUtilisateur);
    }

    // 📊 Compter les notifications non lues
    @GetMapping("/admin/{idUtilisateur}/non-lues")
    public long getNombreNonLues(@PathVariable Long idUtilisateur) {
        return notificationRepository.countByDestinataireIdUtilisateurAndLueFalse(idUtilisateur);
    }

    // ✅ Marquer une notification comme lue
    @PutMapping("/marquer-lue/{id}")
    public ResponseEntity<?> marquerCommeLue(@PathVariable Long id) {
        Notification notif = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification introuvable"));
        notif.setLue(true);
        notificationRepository.save(notif);
        return ResponseEntity.ok("Notification marquée comme lue");
    }
    @GetMapping("/utilisateur/{idUtilisateur}")
    public ResponseEntity<List<Notification>> getNotificationsParUtilisateur(@PathVariable Long idUtilisateur) {
        List<Notification> notifications = notificationRepository
            .findByDestinataireIdUtilisateurOrderByDateNotifDesc(idUtilisateur);

        if (notifications.isEmpty()) {
            return ResponseEntity.noContent().build(); // 204 No Content
        }

        return ResponseEntity.ok(notifications); // 200 OK avec contenu
    }

}
