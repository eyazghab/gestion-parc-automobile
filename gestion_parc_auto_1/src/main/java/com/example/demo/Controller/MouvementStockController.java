package com.example.demo.Controller;

import com.example.demo.Repository.MouvementStockRepository;
import com.example.demo.model.Depot;
import com.example.demo.model.MouvemmentStock;
import com.example.demo.service.MouvementStockService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:4200") //  autorise Angular
@RequestMapping("/api/mouvements")
public class MouvementStockController {

    private final MouvementStockService mouvementStockService;
    @Autowired
    private MouvementStockRepository mouvementStockRepository;

   
    public MouvementStockController(MouvementStockService mouvementStockService,
			MouvementStockRepository mouvementStockRepository) {
		super();
		this.mouvementStockService = mouvementStockService;
		this.mouvementStockRepository = mouvementStockRepository;
	}
	// Tous les mouvements
    @GetMapping
    public List<MouvemmentStock> getAllMouvements() {
        return mouvementStockService.getAllMouvements();
    }
    // Mouvements par stock
    @GetMapping("/stock/{stockId}")
    public ResponseEntity<List<MouvemmentStock>> getMouvementsByStock(@PathVariable Long stockId) {
        List<MouvemmentStock> mouvements = mouvementStockService.getMouvementsByStock(stockId);
        return ResponseEntity.ok(mouvements);
    }
 // Mouvements par article
    @GetMapping("/article/{articleId}")
    public List<MouvemmentStock> getMouvementsByArticle(@PathVariable Long articleId) {
        return mouvementStockRepository.findByArticleId(articleId);
    }
}
