package com.example.demo.Repository;


import com.example.demo.model.Depot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DepotRepository extends JpaRepository<Depot, Long> {
    // Exemple : rechercher un dépôt par nom
    Depot findByNom(String nom);
}

