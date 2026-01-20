package com.example.demo.Controller;

import com.example.demo.Dto.MaintenanceDTO;
import com.example.demo.model.Maintenance;
import com.example.demo.service.MaintenanceService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/maintenances")
@CrossOrigin(origins = "http://localhost:4200")
public class MaintenanceController {

    private final MaintenanceService maintenanceService;

    public MaintenanceController(MaintenanceService maintenanceService) {
        this.maintenanceService = maintenanceService;
    }
    @PostMapping("/simple")
    public ResponseEntity<Maintenance> createSimple(@RequestBody MaintenanceDTO dto) {
        Maintenance saved = maintenanceService.saveMaintenance(dto);
        return ResponseEntity.ok(saved);
    }
    @PostMapping(value = "/sinistre", consumes = {"multipart/form-data"})
    public ResponseEntity<Maintenance> createFromSinistre(@ModelAttribute MaintenanceDTO dto) {
        // dto.files peut contenir les fichiers uploadés
        Maintenance saved = maintenanceService.saveMaintenance(dto);
        return ResponseEntity.ok(saved);
    }

    //  Modifier une maintenance avec lignes
    @PutMapping("/{id}")
    public ResponseEntity<Maintenance> update(@PathVariable Long id, @RequestBody MaintenanceDTO maintenanceDto) {
        Maintenance updated = maintenanceService.updateMaintenance(id, maintenanceDto);
        return ResponseEntity.ok(updated);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<MaintenanceDTO> getById(@PathVariable Long id) {
        MaintenanceDTO dto = maintenanceService.getMaintenanceDTOById(id);
        return ResponseEntity.ok(dto);
    }



    //  Changer le statut uniquement
    @PatchMapping("/{id}/statut")
    public ResponseEntity<Maintenance> changeStatut(@PathVariable Long id, @RequestParam String statut) {
        return ResponseEntity.ok(maintenanceService.changeStatut(id, statut));
    }

    //  Liste de toutes les maintenances avec DTO (y compris lignes)
    @GetMapping
    public ResponseEntity<List<MaintenanceDTO>> getAllMaintenances() {
        List<MaintenanceDTO> maintenances = maintenanceService.getAllMaintenancesDTO();
        return ResponseEntity.ok(maintenances);
    }

    //  Récupérer une maintenance spécifique
   /* @GetMapping("/{id}")
    public ResponseEntity<Maintenance> getById(@PathVariable Long id) {
        Maintenance maintenance = maintenanceService.getMaintenanceById(id);
        return ResponseEntity.ok(maintenance);
    }*/

    //  Supprimer une maintenance
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        maintenanceService.deleteMaintenance(id);
        return ResponseEntity.noContent().build();
    }
}
