package com.example.demo.Dto;

public class VehiculeSimpleDTO {
	 private Long idVehicule;
	    private String immatriculation;

	    public VehiculeSimpleDTO(Long idVehicule, String immatriculation) {
	        this.idVehicule = idVehicule;
	        this.immatriculation = immatriculation;
	    }

		public Long getIdVehicule() {
			return idVehicule;
		}

		public void setIdVehicule(Long idVehicule) {
			this.idVehicule = idVehicule;
		}

		public String getImmatriculation() {
			return immatriculation;
		}

		public void setImmatriculation(String immatriculation) {
			this.immatriculation = immatriculation;
		}
	    
}
