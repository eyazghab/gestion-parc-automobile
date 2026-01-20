import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Parking } from '../models/parking';  // Assure-toi d'avoir un modèle pour Parking

@Injectable({
  providedIn: 'root'
})
export class ParkingService {

  private baseUrl = 'http://localhost:8090/api/parkings';  // URL de ton API backend

  constructor(private http: HttpClient) { }

  // Récupérer tous les parkings
  getParkings(): Observable<Parking[]> {
    return this.http.get<Parking[]>(this.baseUrl);
  }
   ajouterParking(parking: Parking): Observable<Parking> {
    return this.http.post<Parking>(this.baseUrl, parking);
  }
  modifierParking(id: number, parking: any): Observable<any> {
  return this.http.put(`${this.baseUrl}/${id}`, parking);
}
 // Supprimer un parking
  supprimerParking(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}
