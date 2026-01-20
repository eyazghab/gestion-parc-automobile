package com.example.demo.Dto;

import java.util.List;

public class UtilisateurAvecBonsDTO {
	private Long utilisateurId;
    private String nomUtilisateur;
    private List<MissionCarburantDTO> bons;

    public UtilisateurAvecBonsDTO(Long utilisateurId, String nomUtilisateur, List<MissionCarburantDTO> bons) {
        this.utilisateurId = utilisateurId;
        this.nomUtilisateur = nomUtilisateur;
        this.bons = bons;
    }

	public Long getUtilisateurId() {
		return utilisateurId;
	}

	public void setUtilisateurId(Long utilisateurId) {
		this.utilisateurId = utilisateurId;
	}

	public String getNomUtilisateur() {
		return nomUtilisateur;
	}

	public void setNomUtilisateur(String nomUtilisateur) {
		this.nomUtilisateur = nomUtilisateur;
	}

	public List<MissionCarburantDTO> getBons() {
		return bons;
	}

	public void setBons(List<MissionCarburantDTO> bons) {
		this.bons = bons;
	}

}
