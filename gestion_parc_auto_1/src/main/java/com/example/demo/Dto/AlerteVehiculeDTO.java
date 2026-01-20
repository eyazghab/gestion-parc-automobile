package com.example.demo.Dto;

public  class AlerteVehiculeDTO {
    private Long id;        // <- id de AlerteHistorique
    private String vehicule;
    private String message;
    private boolean resolue; // <- état résolu

    public AlerteVehiculeDTO(Long id, String vehicule, String message, boolean resolue) {
        this.id = id;
        this.vehicule = vehicule;
        this.message = message;
        this.resolue = resolue;
    }

    // getters / setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getVehicule() { return vehicule; }
    public void setVehicule(String vehicule) { this.vehicule = vehicule; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public boolean isResolue() { return resolue; }
    public void setResolue(boolean resolue) { this.resolue = resolue; }
}
