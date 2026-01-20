package com.example.demo.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.TypeMaintenance;

public interface TypeMainRepository extends JpaRepository<TypeMaintenance, Long>{
	  Optional<TypeMaintenance> findByLibelleIgnoreCaseAndActifTrue(String libelle);
	  List<TypeMaintenance> findAllByActifTrue();

}
