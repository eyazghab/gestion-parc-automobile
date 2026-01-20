package com.example.demo.Controller;

import com.example.demo.model.Commande;
import com.example.demo.model.Commande.StatutCommande;
import com.example.demo.service.CommandeService;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/commandes")
@CrossOrigin(origins = "http://localhost:4200")
public class CommandeController {

    private final CommandeService commandeService;

    public CommandeController(CommandeService commandeService) {
        this.commandeService = commandeService;
    }

    // ✅ Créer une commande (associer fournisseur + articles + commentaire + quantité)
    @PostMapping("/create")
public ResponseEntity<?> createCommande(
        @RequestParam Long fournisseurId,
        @RequestParam List<Long> articleIds,
        @RequestParam(required = false) String commentaire,
        @RequestParam(required = false, defaultValue = "1") int quantite) {
    try {
        Commande commande = commandeService.creerCommande(fournisseurId, articleIds, commentaire, quantite);
        return ResponseEntity.ok(commande);
    } catch (RuntimeException e) {
        return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    }
}

    @PutMapping("/livrer/{id}")
    public ResponseEntity<Commande> livrerCommande(@PathVariable Long id) {
        try {
            Commande commande = commandeService.livrerCommande(id);
            return ResponseEntity.ok(commande);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    // ✅ Récupérer toutes les commandes
    @GetMapping
    public ResponseEntity<List<Commande>> getAllCommandes() {
        return ResponseEntity.ok(commandeService.getCommandes());
    }

    // ✅ Récupérer une commande par ID
    @GetMapping("/{id}")
    public ResponseEntity<Commande> getCommandeById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(commandeService.getCommandeById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ✅ Changer le statut d’une commande
    @PutMapping("/{id}/statut")
    public ResponseEntity<Commande> updateStatut(@PathVariable Long id,
                                                 @RequestParam StatutCommande statut) {
        try {
            Commande commande = commandeService.changerStatut(id, statut);
            return ResponseEntity.ok(commande);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    @PostMapping("/create-with-quantites")
    public ResponseEntity<Commande> createWithQuantites(
            @RequestParam Long fournisseurId,
            @RequestParam List<Long> articleIds,
            @RequestParam List<Integer> quantites,
            @RequestParam(required = false) String commentaire) {

        Commande commande = commandeService.creerCommandeAvecQuantites(fournisseurId, articleIds, quantites, commentaire);
        return ResponseEntity.ok(commande);
    }

    
   public static class AnnulationDTO {
    public String justification;
}

@PutMapping("/{id}/annuler")
public ResponseEntity<Commande> annulerCommande(@PathVariable Long id, @RequestBody AnnulationDTO dto) {
    Commande existing = commandeService.findById(id);
    if (existing == null) return ResponseEntity.notFound().build();

    if (existing.getStatut() != StatutCommande.EN_COURS) {
        return ResponseEntity.badRequest().body(existing);
    }

    existing.setStatut(StatutCommande.ANNULEE);
    existing.setActif(false);
    existing.setJustificationAnnulation(dto.justification);

    Commande updated = commandeService.save(existing);
    return ResponseEntity.ok(updated);
}

 // Mettre à jour une commande existante
    @PutMapping("/update/{id}")
    public Commande updateCommande(
            @PathVariable("id") Long commandeId,
            @RequestParam Long fournisseurId,
            @RequestParam List<Long> articleIds,
            @RequestParam List<Integer> quantites,
            @RequestParam(required = false) String commentaire) {
        return commandeService.updateCommande(commandeId, fournisseurId, articleIds, quantites, commentaire);
    }
   @PutMapping("/{id}/commander")
public ResponseEntity<?> passerEnCours(@PathVariable Long id) {
    try {
        Commande existing = commandeService.findById(id);
        if (existing == null) return ResponseEntity.notFound().build();

        if (existing.getStatut() != StatutCommande.EN_ATTENTE) {
            return ResponseEntity.badRequest().body(Map.of("error", "La commande n'est pas en attente"));
        }

        Commande updated = commandeService.passerEnCours(id);
        return ResponseEntity.ok(updated);
    } catch (RuntimeException e) {
        return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    }
}
   @DeleteMapping("/delete/{id}")
   public ResponseEntity<?> deleteCommande(@PathVariable Long id) {
       try {
           commandeService.delete(id);
           return ResponseEntity.ok(Map.of("message", "Commande supprimée avec succès"));
       } catch (RuntimeException e) {
           return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
       }
   }
   @GetMapping("/{id}/facture/pdf")
public ResponseEntity<byte[]> downloadFacturePDF(@PathVariable Long id) throws Exception {
    Commande commande = commandeService.findById(id);
    if (commande == null || commande.getFacture() == null) {
        return ResponseEntity.notFound().build();
    }

    byte[] pdfBytes = commandeService.generateFacturePDFWithQRCode(commande);

    return ResponseEntity.ok()
            .header("Content-Disposition", "attachment; filename=Facture_Commande_" + commande.getNumeroCommande() + ".pdf")
            .contentType(MediaType.APPLICATION_PDF)
            .body(pdfBytes);
}


}
