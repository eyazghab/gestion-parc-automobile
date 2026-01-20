package com.example.demo.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.Entreprise;



public interface EntrepriseRepository extends JpaRepository<Entreprise, Long> {}
