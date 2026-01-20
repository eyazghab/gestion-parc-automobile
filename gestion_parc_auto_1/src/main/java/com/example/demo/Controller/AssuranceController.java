package com.example.demo.Controller;


import com.example.demo.model.Assurance;
import com.example.demo.service.AssuranceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/assurances")
@CrossOrigin("*")
public class AssuranceController {

    @Autowired
    private AssuranceService assuranceService;

    @PostMapping
    public Assurance createAssurance(@RequestBody Assurance assurance) {
        return assuranceService.createAssurance(assurance);
    }

    @GetMapping("/{id}")
    public Assurance getAssuranceById(@PathVariable Long id) {
        return assuranceService.getAssuranceById(id);
    }

    @GetMapping
    public List<Assurance> getAllAssurances() {
        return assuranceService.getAllAssurances();
    }

    @PutMapping("/{id}")
    public Assurance updateAssurance(@PathVariable Long id, @RequestBody Assurance assurance) {
        return assuranceService.updateAssurance(id, assurance);
    }

    @DeleteMapping("/{id}")
    public void deleteAssurance(@PathVariable Long id) {
        assuranceService.deleteAssurance(id);
    }
}
