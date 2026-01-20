package com.example.demo.RepositoryCnam;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.cnam.CodeCnam;

@Repository
public interface CodeCnamRepository extends JpaRepository<CodeCnam, String> {
}