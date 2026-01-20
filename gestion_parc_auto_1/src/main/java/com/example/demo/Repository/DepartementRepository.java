package com.example.demo.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.Departement;


public interface DepartementRepository extends JpaRepository<Departement, Long> {
	
}
