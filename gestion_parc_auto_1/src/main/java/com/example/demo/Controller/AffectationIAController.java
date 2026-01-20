package com.example.demo.Controller;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.Repository.OrdreMissionRepository;
import com.example.demo.Repository.VehiculeRepository;
import com.example.demo.model.OrdreMission;
import com.example.demo.model.Vehicule;
import com.example.demo.service.AffectationIAService;

@RestController
@RequestMapping("/api/ia/affectations")
@CrossOrigin(origins = "*")
public class AffectationIAController {

    private final AffectationIAService iaService;
    private final VehiculeRepository vehiculeRepo;
    private final OrdreMissionRepository missionRepo;

    public AffectationIAController(AffectationIAService iaService,
                                   VehiculeRepository vehiculeRepo,
                                   OrdreMissionRepository missionRepo) {
        this.iaService = iaService;
        this.vehiculeRepo = vehiculeRepo;
        this.missionRepo = missionRepo;
    }

    @GetMapping("/optimiser")
    public Map<Long, Long> optimiser() {
        List<Vehicule> vehicules = vehiculeRepo.findAll();
        List<OrdreMission> missions = missionRepo.findAll();

        return iaService.optimiserAffectations(vehicules, missions);
    }
}

