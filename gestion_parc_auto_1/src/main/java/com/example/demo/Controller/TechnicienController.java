package com.example.demo.Controller;

import com.example.demo.Repository.TechnicienRepository;
import com.example.demo.model.Technicien;
import com.example.demo.service.TechnicienService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/techniciens")
@CrossOrigin(origins = "*")
public class TechnicienController {

    @Autowired
    private TechnicienService technicienService;
    
    @Autowired
    private TechnicienRepository technicienRepository;
    
    @PostMapping
    public Technicien addTechnicien(@RequestBody Technicien technicien) {
        return technicienService.saveTechnicien(technicien);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Technicien> updateTechnicien(@PathVariable Long id, @RequestBody Technicien technicien) {
        Technicien existing = technicienRepository.findById(id).orElse(null);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }

        // On met à jour les champs à modifier
        existing.setNom(technicien.getNom());
        existing.setPrenom(technicien.getPrenom());
        existing.setSpecialite(technicien.getSpecialite());
        existing.setEmailTech(technicien.getEmailTech());
        existing.setTelephoneTech(technicien.getTelephoneTech());
        existing.setActif(technicien.isActif()); // ← cette ligne est importante

        technicienRepository.save(existing);
        return ResponseEntity.ok(existing);
    }

    @DeleteMapping("/{id}")
    public void deleteTechnicien(@PathVariable Long id) {
        technicienService.deleteTechnicien(id);
    }

    @GetMapping("/{id}")
    public Technicien getTechnicien(@PathVariable Long id) {
        return technicienService.getTechnicienById(id);
    }

    @GetMapping
    public List<Technicien> getAllTechniciens() {
        return technicienService.getAllTechniciens();
    }
}
