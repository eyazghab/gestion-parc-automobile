package com.example.demo.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.Technicien;

public interface TechnicienRepository extends JpaRepository<Technicien, Long>{}
