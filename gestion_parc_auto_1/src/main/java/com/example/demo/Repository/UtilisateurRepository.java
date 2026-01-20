package com.example.demo.Repository;



import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Utilisateur;


@Repository
public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {
	Optional<Utilisateur> findByEmail(String email);
	Optional<Utilisateur> findByEmailAndCodeVerification(String email, String codeVerification);
	Optional<Utilisateur> findByCin(String cin);
	boolean existsByCodeCnam(String codeCnam);
    List<Utilisateur> findByDepartementId(Long departementId);
    List<Utilisateur> findByRole(String role);
    Optional<Utilisateur> findByNom(String nom);



}

