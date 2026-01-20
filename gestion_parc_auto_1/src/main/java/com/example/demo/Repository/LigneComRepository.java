package com.example.demo.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.LigneCommande;

public interface LigneComRepository extends JpaRepository<LigneCommande, Long>{

}
