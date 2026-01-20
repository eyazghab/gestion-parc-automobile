package com.example.demo.Controller;


import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.model.LigneMaintenance;
import com.example.demo.service.LigneMaintenanceService;

@RestController
@RequestMapping("/api/lignes-maintenance")
@CrossOrigin(origins = "*") // Autoriser les appels front-end
public class LigneMaintenanceController {

    private final LigneMaintenanceService ligneMaintenanceService;

    public LigneMaintenanceController(LigneMaintenanceService ligneMaintenanceService) {
        this.ligneMaintenanceService = ligneMaintenanceService;
    }

    @PostMapping
    public ResponseEntity<LigneMaintenance> create(@RequestBody LigneMaintenance ligneMaintenance) {
        return ResponseEntity.ok(ligneMaintenanceService.save(ligneMaintenance));
    }

    @GetMapping
    public ResponseEntity<List<LigneMaintenance>> getAll() {
        return ResponseEntity.ok(ligneMaintenanceService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LigneMaintenance> getById(@PathVariable Long id) {
        return ligneMaintenanceService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<LigneMaintenance> update(@PathVariable Long id, @RequestBody LigneMaintenance ligneMaintenance) {
        return ResponseEntity.ok(ligneMaintenanceService.update(id, ligneMaintenance));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        ligneMaintenanceService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
