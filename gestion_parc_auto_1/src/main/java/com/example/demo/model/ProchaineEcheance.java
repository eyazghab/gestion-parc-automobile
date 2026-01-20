package com.example.demo.model;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProchaineEcheance {
    private String type;
    private LocalDate derniereDate;
    private Integer dernierKm;
    private LocalDate prochaineDate;
    private Integer prochainKm;
    private EtatAlerte etat;
}