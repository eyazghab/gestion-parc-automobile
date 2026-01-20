package com.example.demo.service;

import org.springframework.stereotype.Service;

@Service
public class DistanceService {
	 /**
     * Calcule une distance fictive selon la destination
     * @param destination : nom de la ville ou lieu
     * @return distance estimée en km
     */
    public double calculerDistance(String destination) {
        if (destination == null) return 50.0; // valeur par défaut

        switch(destination.toLowerCase()) {
            case "tunis": return 30.0;
            case "sousse": return 150.0;
            case "sfax": return 300.0;
            case "monastir": return 170.0;
            case "bizerte": return 60.0;
            default: return 50.0; // valeur par défaut si la destination n'est pas connue
        }
    }
}

