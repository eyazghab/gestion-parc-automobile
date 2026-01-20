package com.example.demo.Controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.model.TypeMaintenance;
import com.example.demo.service.TypeMaintenanceService;

@RestController
@RequestMapping("/api/types-maintenance")
@CrossOrigin(origins = "http://localhost:4200")
public class TypeMaintenanceController {

    private final TypeMaintenanceService typeMaintenanceService;

    public TypeMaintenanceController(TypeMaintenanceService typeMaintenanceService) {
        this.typeMaintenanceService = typeMaintenanceService;
    }

    // 🔹 Lister tous les types actifs
    @GetMapping
    public ResponseEntity<List<TypeMaintenance>> getAllActif() {
        List<TypeMaintenance> types = typeMaintenanceService.getAllActif();
        return new ResponseEntity<>(types, HttpStatus.OK);
    }

    // 🔹 Rechercher un type par libellé
    @GetMapping("/search")
    public ResponseEntity<TypeMaintenance> getByLibelle(@RequestParam String libelle) {
        Optional<TypeMaintenance> type = typeMaintenanceService.findByLibelle(libelle);
        return type.map(t -> new ResponseEntity<>(t, HttpStatus.OK))
                   .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // 🔹 Ajouter un nouveau type
    @PostMapping
    public ResponseEntity<TypeMaintenance> create(@RequestBody TypeMaintenance typeMaintenance) {
        TypeMaintenance saved = typeMaintenanceService.save(typeMaintenance);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    // 🔹 Mettre à jour un type existant
    @PutMapping("/{id}")
    public ResponseEntity<TypeMaintenance> update(@PathVariable Long id, @RequestBody TypeMaintenance typeMaintenance) {
        Optional<TypeMaintenance> existing = typeMaintenanceService.findByLibelle(typeMaintenance.getLibelle());
        if (existing.isPresent() && !existing.get().getId().equals(id)) {
            return new ResponseEntity<>(HttpStatus.CONFLICT); // libellé déjà utilisé
        }
        typeMaintenance.setId(id);
        TypeMaintenance updated = typeMaintenanceService.save(typeMaintenance);
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }

    // 🔹 Désactiver un type (soft delete)
    @PatchMapping("/{id}/desactiver")
    public ResponseEntity<Void> desactiver(@PathVariable Long id) {
        Optional<TypeMaintenance> type = typeMaintenanceService.getAllActif()
            .stream()
            .filter(t -> t.getId().equals(id))
            .findFirst();
        if (type.isPresent()) {
            TypeMaintenance t = type.get();
            t.setActif(false);
            typeMaintenanceService.save(t);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
