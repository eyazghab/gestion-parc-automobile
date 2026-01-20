package com.example.demo.service;

import com.example.demo.model.Technicien;
import java.util.List;

public interface TechnicienService {
    Technicien saveTechnicien(Technicien technicien);
    Technicien updateTechnicien(Long id, Technicien technicien);
    void deleteTechnicien(Long id);
    Technicien getTechnicienById(Long id);
    List<Technicien> getAllTechniciens();
}
