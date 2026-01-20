import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class CarburantIAService {

  private baseUrl = 'http://localhost:8090/api/ia/carburant';

  constructor(private http: HttpClient) {}

  // Entraîner le modèle
  train(): Observable<{ message: string }> {
    return this.http.post<{ message: string }>(`${this.baseUrl}/train`, {});
  }

  // Prédiction pour un véhicule
  predict(vehiculeId: number, quantite: number): Observable<{ message: string }> {
    return this.http.get<{ message: string }>(
      `${this.baseUrl}/predict/${vehiculeId}?quantite=${quantite || 0}`
    );
  }
}
