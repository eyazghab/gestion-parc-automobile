package com.example.demo.Dto;

public class PredictionDTO {
    private String status; // success | model_not_trained | error
    private String message;
    private Double risk;
    private String maintenanceDate; // toujours String pour JSON

    // Constructeur principal
    public PredictionDTO(String message, Double risk, String maintenanceDate) {
        this.message = message;
        this.risk = risk;
        this.maintenanceDate = maintenanceDate;
    }

    // Constructeur pour erreurs / modèle non entraîné
    public PredictionDTO(String status, String message) {
        this.status = status;
        this.message = message;
    }

    // Getters et setters
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Double getRisk() { return risk; }
    public void setRisk(Double risk) { this.risk = risk; }

    public String getMaintenanceDate() { return maintenanceDate; }
    public void setMaintenanceDate(String maintenanceDate) { this.maintenanceDate = maintenanceDate; }
}
