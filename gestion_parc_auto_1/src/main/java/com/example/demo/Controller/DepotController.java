package com.example.demo.Controller;

import com.example.demo.model.Depot;
import com.example.demo.service.DepotService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/depots")
public class DepotController {

    private final DepotService depotService;

    public DepotController(DepotService depotService) {
        this.depotService = depotService;
    }

    @GetMapping
    public List<Depot> getAllDepots() {
        return depotService.getAllDepots();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Depot> getDepotById(@PathVariable Long id) {
        Depot depot = depotService.getDepotById(id);
        return ResponseEntity.ok(depot);
    }

    @PostMapping
    public ResponseEntity<Depot> createDepot(@RequestBody Depot depot) {
        Depot saved = depotService.saveDepot(depot);
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Depot> updateDepot(@PathVariable Long id, @RequestBody Depot depot) {
        depot.setIdDepot(id);
        Depot updated = depotService.saveDepot(depot);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDepot(@PathVariable Long id) {
        depotService.deleteDepot(id);
        return ResponseEntity.noContent().build();
    }
}
