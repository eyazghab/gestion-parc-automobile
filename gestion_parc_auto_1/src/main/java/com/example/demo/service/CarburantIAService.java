package com.example.demo.service;


import com.example.demo.model.BonCarburant;
import com.example.demo.model.Vehicule;
import org.springframework.stereotype.Service;
import smile.classification.RandomForest;
import smile.data.DataFrame;
import smile.data.Tuple;
import smile.data.formula.Formula;
import smile.data.vector.DoubleVector;
import smile.data.vector.IntVector;

import java.util.List;
import java.util.Properties;

@Service
public class CarburantIAService {

    private RandomForest model;
    private DataFrame schemaDf;
    private boolean modelTrained = false;

    public boolean isModelTrained() { return modelTrained; }

    // Entraîner le modèle sur l'historique de consommation
    public void trainModel(List<BonCarburant> bons) {
        int n = bons.size();
        if (n == 0) throw new RuntimeException("Pas de données pour entraîner le modèle");

        double[] km = new double[n];
        double[] quantite = new double[n];
        int[] labels = new int[n];

        for (int i = 0; i < n; i++) {
            BonCarburant bon = bons.get(i);
            Vehicule v = bon.getVehicule();

            km[i] = v.getKilometrageActuel() != null ? v.getKilometrageActuel() : 0;
            quantite[i] = bon.getQuantite() != null ? bon.getQuantite() : 0;
            labels[i] = bon.getQuantite() != null && bon.getQuantite() < 50 ? 1 : 0; // seuil arbitraire
        }

        schemaDf = DataFrame.of(
            DoubleVector.of("km", km),
            DoubleVector.of("quantite", quantite),
            IntVector.of("label", labels)
        );

        Properties props = new Properties();
        props.setProperty("trees", "100");
        props.setProperty("maxDepth", "10");

        this.model = RandomForest.fit(Formula.lhs("label"), schemaDf, props);
        this.modelTrained = true;
        System.out.println("✅ Modèle consommation entraîné");
    }

    // Prédiction pour un véhicule
    public String predict(Vehicule vehicule, double quantite) {
        if (!modelTrained || model == null) return "❌ Modèle non entraîné";

        double kmDouble = vehicule.getKilometrageActuel() != null ? vehicule.getKilometrageActuel() : 0;
        double quantiteDouble = quantite;

        Tuple input = Tuple.of(new Object[]{kmDouble, quantiteDouble}, schemaDf.schema());
        int pred = model.predict(input);

        return pred == 1 ? "⚠️ Consommation élevée" : "✅ Consommation normale";
    }
}
