package com.example.demo.Controller;

import com.example.demo.model.Departement;
import com.example.demo.service.DepartementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/departements")
@CrossOrigin("*")
public class DepartementController {

    @Autowired
    private DepartementService departementService;

    @PostMapping
    public Departement createDepartement(@RequestBody Departement departement) {
        return departementService.createDepartement(departement);
    }

    @GetMapping("/{id}")
    public Departement getDepartementById(@PathVariable Long id) {
        return departementService.getDepartementById(id);
    }

    @GetMapping
    public List<Departement> getAllDepartements() {
        return departementService.getAllDepartements();
    }

    @PutMapping("/{id}")
    public Departement updateDepartement(@PathVariable Long id, @RequestBody Departement departement) {
        return departementService.updateDepartement(id, departement);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> supprimerDepartement(@PathVariable Long id) {
        try {
            departementService.deleteDepartement(id);
            return ResponseEntity.ok(Map.of("message", "Département supprimé avec succès."));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Erreur interne du serveur."));
        }
    }
}