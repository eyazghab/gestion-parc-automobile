package com.example.demo.Controller;

import com.example.demo.Repository.StockRepository;
import com.example.demo.model.Stock;
import com.example.demo.service.StockService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:4200") //  autorise Angular
@RequestMapping("/api/stocks")
public class StockController {

    private final StockService stockService;

    @Autowired
    private StockRepository stockRepository;
    public StockController(StockService stockService) {
        this.stockService = stockService;
    }
    @GetMapping
    public List<Stock> getAllStocks() {
        return stockService.getAllStocks();
    }
    // Entrée de stock
    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping("/entree/{depotId}/{articleId}")
    public ResponseEntity<Stock> entreeStock(
    		   @PathVariable Long depotId,
    	        @PathVariable Long articleId,
    	        @RequestParam int quantite,
    	        @RequestParam(required = false) String motif) {
        Stock stock = stockService.entreeStock(depotId, articleId, quantite, motif);
        return ResponseEntity.ok(stock);
    }

    // Sortie de stock
    @PostMapping("/sortie/{depotId}/{articleId}")
    public ResponseEntity<Stock> sortieStock(@PathVariable Long depotId,
                                             @PathVariable Long articleId,
                                             @RequestParam int quantite,
                                             @RequestParam(required = false) String motif) {
        return ResponseEntity.ok(stockService.sortieStock(depotId, articleId, quantite, motif));
    }

    // Transfert entre dépôts
    @PostMapping("/transfert/{depotSourceId}/{depotDestinationId}/{articleId}")
    public ResponseEntity<Void> transfererStock(
    		@PathVariable Long depotSourceId,
            @PathVariable Long depotDestinationId,
            @PathVariable Long articleId,
            @RequestParam int quantite,
            @RequestParam(required = false) String motif){
        stockService.transfererStock(depotSourceId, depotDestinationId, articleId, quantite, motif);
        return ResponseEntity.ok().build();
    }

    // Liste des stocks par dépôt
    @GetMapping("/parDepot/{depotId}")
    public ResponseEntity<List<Stock>> getStocksByDepot(@PathVariable Long depotId) {
        List<Stock> stocks = stockService.getStocksByDepot(depotId);
        return ResponseEntity.ok(stocks);
    }
    @CrossOrigin(origins = "http://localhost:4200")
    @PutMapping("/{id}/desactiver")
    public ResponseEntity<Stock> desactiverStock(@PathVariable Long id) {
        Stock stock = stockRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Stock introuvable"));
        stock.setActif(false); // champs boolean 'actif'
        return ResponseEntity.ok(stockRepository.save(stock));
    }
    @GetMapping("/alertes")
    public List<Stock> getAlertesStock() {
        return stockService.getStocksEnAlerte();
    }
    @PutMapping("/{id}")
    public ResponseEntity<Stock> updateStock(@PathVariable Long id, @RequestBody Map<String, Object> payload) {
        Long articleId = ((Number) payload.get("articleId")).longValue();
        Long depotId = ((Number) payload.get("depotId")).longValue();
        int quantite = ((Number) payload.get("quantite")).intValue();
        String motif = (String) payload.get("motif");

        Integer stockMin = payload.get("stockMin") != null ? ((Number) payload.get("stockMin")).intValue() : null;
        Integer stockAlerte = payload.get("stockAlerte") != null ? ((Number) payload.get("stockAlerte")).intValue() : null;

        Stock updatedStock = stockService.updateStock(id, articleId, depotId, quantite, motif, stockMin, stockAlerte);
        return ResponseEntity.ok(updatedStock);
    }

    @PutMapping("/{id}/toggleActif")
    public ResponseEntity<Stock> toggleActif(@PathVariable Long id, @RequestParam boolean actif) {
        Stock stock = stockRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Stock introuvable"));
        stock.setActif(actif);
        return ResponseEntity.ok(stockRepository.save(stock));
    }
}
