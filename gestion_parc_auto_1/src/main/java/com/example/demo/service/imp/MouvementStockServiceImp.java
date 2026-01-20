package com.example.demo.service.imp;

import com.example.demo.Repository.MouvementStockRepository;
import com.example.demo.Repository.StockRepository;
import com.example.demo.model.MouvemmentStock;
import com.example.demo.model.Stock;
import com.example.demo.service.MouvementStockService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MouvementStockServiceImp implements MouvementStockService {

    private final MouvementStockRepository mouvementStockRepository;
    private final StockRepository stockRepository;

    public MouvementStockServiceImp(MouvementStockRepository mouvementStockRepository, StockRepository stockRepository) {
        this.mouvementStockRepository = mouvementStockRepository;
        this.stockRepository = stockRepository;
    }
    @Override
    public List<MouvemmentStock> getAllMouvements() {
        return mouvementStockRepository.findAllByOrderByDateDesc();
    }
    @Override
    public List<MouvemmentStock> getMouvementsByStock(Long stockId) {
        Stock stock = stockRepository.findById(stockId)
                .orElseThrow(() -> new RuntimeException("Stock introuvable"));
        return mouvementStockRepository.findByStockOrderByDateDesc(stock);
    }
}
