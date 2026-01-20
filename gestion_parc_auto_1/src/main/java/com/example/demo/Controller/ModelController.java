package com.example.demo.Controller;


import com.example.demo.model.Modele;
import com.example.demo.service.ModeleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/modeles")
@CrossOrigin("*")
public class ModelController {

    @Autowired
    private ModeleService modeleService;

    @PostMapping
    public ResponseEntity<Modele> createModele(@RequestBody Modele modele) {
        return ResponseEntity.ok(modeleService.saveModele(modele));
    }

    @GetMapping
    public ResponseEntity<List<Modele>> getAllModeles() {
        return ResponseEntity.ok(modeleService.getAllModeles());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Modele> getModeleById(@PathVariable Long id) {
        return modeleService.getModeleById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Modele> updateModele(@PathVariable Long id, @RequestBody Modele modele) {
        return ResponseEntity.ok(modeleService.updateModele(id, modele));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteModele(@PathVariable Long id) {
        modeleService.deleteModele(id);
        return ResponseEntity.noContent().build();
    }
}
