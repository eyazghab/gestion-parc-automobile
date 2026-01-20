package com.example.demo.service;




import java.util.List;

import com.example.demo.model.MouvemmentStock;

public interface MouvementStockService {

	List<MouvemmentStock> getMouvementsByStock(Long stockId);
    List<MouvemmentStock> getAllMouvements();

}
