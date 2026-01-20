package com.example.demo.service;

import com.example.demo.model.Maintenance;
import com.example.demo.model.Vehicule;
import org.springframework.stereotype.Service;
import smile.classification.RandomForest;
import smile.data.DataFrame;
import smile.data.Tuple;
import smile.data.formula.Formula;
import smile.data.vector.DoubleVector;
import smile.data.vector.IntVector;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Properties;

@Service
public class MaintenanceIAService {

    private RandomForest model;
    private DataFrame schemaDf;
    private boolean modelTrained = false;

    public boolean isModelTrained() { return modelTrained; }

    public void setModelTrained(boolean trained) { this.modelTrained = trained; }

    // 🔹 utilitaire pour convertir Date -> LocalDate
    private LocalDate convertToLocalDate(Date date) {
        if (date == null) return null;

        if (date instanceof java.sql.Date sqlDate) {
            return sqlDate.toLocalDate(); // ✅ pour java.sql.Date
        }

        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(); // java.util.Date
    }

    /** Entraînement du modèle IA */
    public void trainModel(List<Maintenance> maintenances) {
        List<Maintenance> validMaintenances = maintenances.stream()
                .filter(m -> m.getVehicule() != null)
                .toList();

        if (validMaintenances.isEmpty()) {
            throw new RuntimeException("Aucune maintenance valide avec véhicule pour entraîner le modèle");
        }

        int n = validMaintenances.size();
        double[] km = new double[n];
        double[] coutTotal = new double[n];
        double[] ageVehicule = new double[n];
        double[] consommation = new double[n];
        double[] nbMoisDepuisFacture = new double[n];
        int[] labels = new int[n];

        for (int i = 0; i < n; i++) {
            Maintenance m = validMaintenances.get(i);
            Vehicule v = m.getVehicule();

            km[i] = v.getKilometrageActuel() != null ? v.getKilometrageActuel() : 0.0;
            coutTotal[i] = m.getCoutTotal() != null ? m.getCoutTotal() : 0.0;

            LocalDate dateCircu = convertToLocalDate(v.getDateCircu());
            ageVehicule[i] = dateCircu != null
                    ? Period.between(dateCircu, LocalDate.now()).getYears()
                    : 0.0;

            consommation[i] = v.getConsommationMoyenne() != null ? v.getConsommationMoyenne() : 0.0;

            LocalDate dateFacture = convertToLocalDate(m.getDateFacture());
            nbMoisDepuisFacture[i] = dateFacture != null
                    ? Period.between(dateFacture, LocalDate.now()).toTotalMonths()
                    : 0.0;

            labels[i] = coutTotal[i] > 2000 ? 1 : 0;
        }

        schemaDf = DataFrame.of(
                DoubleVector.of("km", km),
                DoubleVector.of("coutTotal", coutTotal),
                DoubleVector.of("ageVehicule", ageVehicule),
                DoubleVector.of("consommation", consommation),
                DoubleVector.of("nbMoisDepuisFacture", nbMoisDepuisFacture),
                IntVector.of("label", labels)
        );

        Properties props = new Properties();
        props.setProperty("trees", "100");
        props.setProperty("maxDepth", "10");
        props.setProperty("maxLeaves", "5");
        props.setProperty("minNodeSize", "2");
        props.setProperty("maxFeatures", "0");

        this.model = RandomForest.fit(Formula.lhs("label"), schemaDf, props);
        this.modelTrained = true;

        System.out.println("✅ Modèle entraîné avec succès sur " + n + " maintenances");
    }

    /** Prédiction */
    public String predict(Vehicule v, double dernierCout) {
        if (!modelTrained || model == null) {
            return "❌ Modèle non entraîné";
        }

        double kmDouble = v.getKilometrageActuel() != null ? v.getKilometrageActuel() : 0.0;
        double coutTotal = dernierCout;

        LocalDate dateCircu = convertToLocalDate(v.getDateCircu());
        double ageVehicule = dateCircu != null
                ? Period.between(dateCircu, LocalDate.now()).getYears()
                : 0.0;

        double consommation = v.getConsommationMoyenne() != null ? v.getConsommationMoyenne() : 0.0;

        double nbMoisDepuisFacture = 0.0; // inconnu lors de la prédiction

        Tuple input = Tuple.of(
                new Object[]{kmDouble, coutTotal, ageVehicule, consommation, nbMoisDepuisFacture},
                schemaDf.schema()
        );

        int pred = model.predict(input);

        return pred == 1
                ? "⚠️ Maintenance probable"
                : "✅ Pas de maintenance urgente";
    }
}
