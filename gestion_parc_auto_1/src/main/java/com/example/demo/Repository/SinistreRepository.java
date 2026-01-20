package com.example.demo.Repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.model.EtatSinistre;
import com.example.demo.model.Sinistre;

public interface SinistreRepository  extends JpaRepository<Sinistre, Long>{
	  @Query("SELECT MAX(s.numDeclaration) FROM Sinistre s")
	    Integer findMaxNumDeclaration();
	  @Query("SELECT COUNT(s) FROM Sinistre s WHERE s.dateDeclaration = :dateDeclaration")
	  int countByDateDeclaration(@Param("dateDeclaration") Date dateDeclaration);
	  @Query("SELECT s FROM Sinistre s JOIN FETCH s.vehicule")
	  List<Sinistre> findAllWithVehicule();
	  List<Sinistre> findByEtat(EtatSinistre etat);

}
