package com.example.demo.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Carburant;

@Repository
public interface CarburantRepository extends JpaRepository<Carburant, Long> {
    Optional<Carburant> findByCode(String code); // Méthode pour récupérer le carburant par code

}
