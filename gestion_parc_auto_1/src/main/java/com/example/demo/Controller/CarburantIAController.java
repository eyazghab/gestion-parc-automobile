package com.example.demo.Controller;

import com.example.demo.Repository.BonCarburantRepository;
import com.example.demo.Repository.VehiculeRepository;
import com.example.demo.model.BonCarburant;
import com.example.demo.model.Vehicule;
import com.example.demo.service.CarburantIAService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ia/carburant")
@CrossOrigin(origins = "http://localhost:4200") 
public class CarburantIAController {

    private final CarburantIAService iaService;
    private final BonCarburantRepository bonCarburantRepository;
    private final VehiculeRepository vehiculeRepository;

    @Autowired
    public CarburantIAController(CarburantIAService iaService,
                                 BonCarburantRepository bonCarburantRepository,
                                 VehiculeRepository vehiculeRepository) {
        this.iaService = iaService;
        this.bonCarburantRepository = bonCarburantRepository;
        this.vehiculeRepository = vehiculeRepository;
    }

    // 🔹 Entraîner le modèle
    @PostMapping("/train")
    public Map<String, String> train() {
        Map<String, String> response = new HashMap<>();
        try {
            List<BonCarburant> bons = bonCarburantRepository.findAll();
            if (bons.isEmpty()) {
                response.put("message", "❌ Aucun bon carburant disponible pour l'entraînement.");
            } else {
                iaService.trainModel(bons);
                response.put("message", "✅ Modèle entraîné avec " + bons.size() + " bons carburant.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.put("message", "❌ Erreur lors de l'entraînement : " + e.getMessage());
        }
        return response;
    }

    // 🔹 Prédire la consommation pour un véhicule donné
    @GetMapping("/predict/{vehiculeId}")
    public Map<String, String> predict(@PathVariable Long vehiculeId,
                                       @RequestParam(defaultValue = "0") double quantite) {
        Map<String, String> response = new HashMap<>();
        try {
            if (!iaService.isModelTrained()) {
                response.put("message", "❌ Modèle non entraîné");
                return response;
            }

            Vehicule vehicule = vehiculeRepository.findById(vehiculeId)
                    .orElseThrow(() -> new RuntimeException("Véhicule introuvable"));

            String pred = iaService.predict(vehicule, quantite);
            response.put("message", pred);

        } catch (Exception e) {
            e.printStackTrace();
            response.put("message", "❌ Erreur lors de la prédiction : " + e.getMessage());
        }
        return response;
    }
}
