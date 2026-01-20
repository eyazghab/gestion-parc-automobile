package com.example.demo.service;


import com.example.demo.model.Stock;

import java.util.List;

public interface StockService {
		Stock entreeStock(Long depotId, Long articleId, int quantite, String motif);
	    Stock sortieStock(Long depotId, Long articleId, int quantite, String motif);
	    void transfererStock(Long depotSourceId, Long depotDestinationId, Long articleId, int quantite, String motif);
	    List<Stock> getStocksByDepot(Long depotId);
	    List<Stock> getAllStocks();
	    Stock desactiverStock(Long id);
	    List<Stock> getStocksEnAlerte();
	    Stock updateStock(Long id, Long articleId, Long depotId, int quantite, String motif, Integer stockMin, Integer stockAlerte);



}

