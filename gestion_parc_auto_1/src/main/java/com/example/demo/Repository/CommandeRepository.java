package com.example.demo.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.Commande;

public interface CommandeRepository extends JpaRepository<Commande, Long>{
	Optional<Commande> findTopByOrderByNumeroCommandeDesc();

}
