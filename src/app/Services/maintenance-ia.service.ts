import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})

export class MaintenanceIAService {
  private apiUrl = 'http://localhost:8090/api/ia/maintenance';

  constructor(private http: HttpClient) {}

// Appeler le backend pour entraîner le modèle
 
  // Appeler le backend pour entraîner le modèle
  trainModel(): Observable<any> {
    return this.http.post(`${this.apiUrl}/train`, {}); // corps vide
  }
 // Nouvelle méthode : récupérer la prédiction pour un véhicule
  getPrediction(idVehicule: number): Observable<string> {
    return this.http.get<string>(`${this.apiUrl}/predict/${idVehicule}`);
  }
 // Prédiction
  predict(vehiculeId: number, dernierCout: number = 0): Observable<PredictionResponse> {
    return this.http.get<PredictionResponse>(
      `${this.apiUrl}/predict/${vehiculeId}?dernierCout=${dernierCout}`
    );
  }
}

// Type de la réponse de prédiction
export interface PredictionResponse {
  vehicule?: string;
  prediction?: string;
  message?: string; // Pour les erreurs ou modèle non entraîné
}

interface IAResponse {
  message: string;
}
