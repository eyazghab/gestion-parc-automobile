package com.example.demo.service;


import com.example.demo.model.Departement;
import java.util.List;
import org.springframework.stereotype.Service;
@Service
public interface DepartementService {
    Departement createDepartement(Departement departement);
    Departement getDepartementById(Long id);
    List<Departement> getAllDepartements();
    Departement updateDepartement(Long id, Departement departement);
    void deleteDepartement(Long id);
}
