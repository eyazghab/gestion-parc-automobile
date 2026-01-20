package com.example.demo.Controller;

import com.example.demo.Repository.MaintenanceRepository;
import com.example.demo.Repository.VehiculeRepository;
import com.example.demo.model.Maintenance;
import com.example.demo.model.Vehicule;
import com.example.demo.service.MaintenanceIAService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ia/maintenance")
@CrossOrigin(origins = "*")
public class MaintenanceIAController {

    private final MaintenanceIAService iaService;
    private final MaintenanceRepository maintenanceRepository;
    private final VehiculeRepository vehiculeRepository;

    @Autowired
    public MaintenanceIAController(MaintenanceIAService iaService,
                                   MaintenanceRepository maintenanceRepository,
                                   VehiculeRepository vehiculeRepository) {
        this.iaService = iaService;
        this.maintenanceRepository = maintenanceRepository;
        this.vehiculeRepository = vehiculeRepository;
    }

    /**
     * Entraîner le modèle IA à partir de toutes les maintenances existantes
     */
    @PostMapping("/train")
    public Map<String, String> train() {
        Map<String, String> response = new HashMap<>();
        try {
            List<Maintenance> maintenances = maintenanceRepository.findAll();
            if (maintenances.isEmpty()) {
                response.put("message", "❌ Aucune maintenance disponible pour l'entraînement.");
            } else {
                iaService.trainModel(maintenances);
                response.put("message", "✅ Modèle entraîné avec " + maintenances.size() + " maintenances.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.put("message", "❌ Erreur lors de l'entraînement : " + e.getMessage());
        }
        return response;
    }

    /**
     * Prédire si une maintenance est probable pour un véhicule donné
     * Exemple d’appel : GET /api/ia/maintenance/predict/3?dernierCout=1500
     */
    @GetMapping("/predict/{vehiculeId}")
    public Map<String, String> predict(@PathVariable Long vehiculeId,
                                       @RequestParam(defaultValue = "0") double dernierCout) {
        Map<String, String> response = new HashMap<>();
        try {
            if (!iaService.isModelTrained()) {
                response.put("message", "❌ Modèle non entraîné. Lancez /train avant la prédiction.");
                return response;
            }

            Vehicule vehicule = vehiculeRepository.findById(vehiculeId)
                    .orElseThrow(() -> new RuntimeException("Véhicule introuvable"));

            String pred = iaService.predict(vehicule, dernierCout);
            response.put("vehicule", vehicule.getImmatriculation());
            response.put("prediction", pred);

        } catch (Exception e) {
            e.printStackTrace();
            response.put("message", "❌ Erreur lors de la prédiction : " + e.getMessage());
        }
        return response;
    }
}
