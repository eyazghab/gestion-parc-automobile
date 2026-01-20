package com.example.demo.service.imp;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo.Repository.DepotRepository;
import com.example.demo.model.Depot;
import com.example.demo.service.DepotService;
@Service
public class DepotServiceImpl implements  DepotService {

    private final DepotRepository depotRepository;

    public DepotServiceImpl(DepotRepository depotRepository) {
        this.depotRepository = depotRepository;
    }

    @Override
    public Depot saveDepot(Depot depot) {
        return depotRepository.save(depot);
    }

    @Override
    public Depot getDepotById(Long id) {
        return depotRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Depot introuvable"));
    }

    @Override
    public List<Depot> getAllDepots() {
        return depotRepository.findAll();
    }

    @Override
    public void deleteDepot(Long id) {
        depotRepository.deleteById(id);
    }
}
