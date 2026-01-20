package com.example.demo.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.Fournisseur;

public interface FournisseurRepository extends JpaRepository<Fournisseur, Long> {}
