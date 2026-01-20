package com.example.demo.Controller;


import com.example.demo.model.LigneCommande;
import com.example.demo.service.LigneCommandeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ligne-commandes")
@CrossOrigin("*")
public class LigneCommandeController {

    @Autowired
    private LigneCommandeService ligneCommandeService;

    @PostMapping
    public LigneCommande createLigneCommande(@RequestBody LigneCommande ligneCommande) {
        return ligneCommandeService.createLigneCommande(ligneCommande);
    }

    @GetMapping("/{id}")
    public LigneCommande getLigneCommandeById(@PathVariable Long id) {
        return ligneCommandeService.getLigneCommandeById(id);
    }

    @GetMapping
    public List<LigneCommande> getAllLigneCommandes() {
        return ligneCommandeService.getAllLigneCommandes();
    }

    @PutMapping("/{id}")
    public LigneCommande updateLigneCommande(@PathVariable Long id, @RequestBody LigneCommande ligneCommande) {
        return ligneCommandeService.updateLigneCommande(id, ligneCommande);
    }

    @DeleteMapping("/{id}")
    public void deleteLigneCommande(@PathVariable Long id) {
        ligneCommandeService.deleteLigneCommande(id);
    }
}
