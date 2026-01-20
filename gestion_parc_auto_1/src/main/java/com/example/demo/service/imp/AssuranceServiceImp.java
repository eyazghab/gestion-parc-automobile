package com.example.demo.service.imp;

import com.example.demo.Repository.AssuranceReposittory;
import com.example.demo.model.Assurance;
import com.example.demo.service.AssuranceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service

public class AssuranceServiceImp implements AssuranceService {


    @Autowired
    private AssuranceReposittory assuranceRepository;

    @Override
    public Assurance createAssurance(Assurance assurance) {
        return assuranceRepository.save(assurance);
    }

    @Override
    public Assurance getAssuranceById(Long id) {
        return assuranceRepository.findById(id).orElse(null);
    }

    @Override
    public List<Assurance> getAllAssurances() {
        return assuranceRepository.findAll();
    }

    @Override
    public Assurance updateAssurance(Long id, Assurance assurance) {
        Optional<Assurance> existing = assuranceRepository.findById(id);
        if (existing.isPresent()) {
            assurance.setIdAssurance(existing.get().getIdAssurance());
            return assuranceRepository.save(assurance);
        }
        return null;
    }

    @Override
    public void deleteAssurance(Long id) {
        assuranceRepository.deleteById(id);
    }
}
