package com.example.demo.cnam;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "codes_cnam")
public class CodeCnam {
    @Id
    private String code;  // Exemple : CNAM12345

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
    
}