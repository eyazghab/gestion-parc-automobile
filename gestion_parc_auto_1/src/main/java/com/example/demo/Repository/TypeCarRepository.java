package com.example.demo.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.TypeCarrosserie;

public interface TypeCarRepository extends JpaRepository<TypeCarrosserie, Long>{}
