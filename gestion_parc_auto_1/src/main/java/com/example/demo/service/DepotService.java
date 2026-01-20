package com.example.demo.service;

import java.util.List;


import com.example.demo.model.Depot;

public interface DepotService {
	 	Depot saveDepot(Depot depot);
	    Depot getDepotById(Long id);
	    List<Depot> getAllDepots();
	    void deleteDepot(Long id);
}
