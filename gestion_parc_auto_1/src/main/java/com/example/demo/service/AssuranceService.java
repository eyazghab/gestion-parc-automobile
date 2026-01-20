package com.example.demo.service;


import java.util.List;

import com.example.demo.model.Assurance;

public interface AssuranceService {
    Assurance createAssurance(Assurance assurance);
    Assurance getAssuranceById(Long id);
    List<Assurance> getAllAssurances();
    Assurance updateAssurance(Long id, Assurance assurance);
    void deleteAssurance(Long id);
}
