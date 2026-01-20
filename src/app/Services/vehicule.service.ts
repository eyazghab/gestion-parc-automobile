import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Vehicule } from '../models/vehicule';

export interface PredictionDTO {
  message: string;
  risk?: number;
  maintenanceDate?: string;
  vehicule?: string;
  showDetails?: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class VehiculeService {
  private baseUrl = 'http://localhost:8090/api/vehicules';

  constructor(private http: HttpClient) {}

  getVehicules(): Observable<Vehicule[]> {
    return this.http.get<Vehicule[]>(this.baseUrl);
  }

  getVehiculeById(id: number): Observable<Vehicule> {
    return this.http.get<Vehicule>(`${this.baseUrl}/${id}`);
  }

  addVehicule(vehicule: Vehicule): Observable<Vehicule> {
    return this.http.post<Vehicule>(`${this.baseUrl}`, vehicule);
  }

  updateVehicule(id: number, vehicule: Vehicule): Observable<Vehicule> {
    return this.http.put<Vehicule>(`${this.baseUrl}/${id}`, vehicule);
  }

  deleteVehicule(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }

  getVehiculesLibres(): Observable<Vehicule[]> {
    return this.http.get<Vehicule[]>(`${this.baseUrl}/vehicules-libres`);
  }

  getVehiculesDisponibles(dateDebut: string, dateFin: string): Observable<Vehicule[]> {
    const params = new HttpParams()
      .set('dateDebut', dateDebut)
      .set('dateFin', dateFin);
    return this.http.get<Vehicule[]>(`${this.baseUrl}/vehicules-disponibles`, { params });
  }

  changerEtatVehicule(id: number, etat: string) {
    return this.http.patch<Vehicule>(`${this.baseUrl}/${id}/etat?etat=${etat}`, {});
  }

  getVehiculesDisponiblesOuReserves(): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/disponibles-et-reserves`);
  }

  getPrediction(vehiculeId: number): Observable<PredictionDTO> {
    return this.http.get<PredictionDTO>(`${this.baseUrl}/predict/${vehiculeId}`);
  }
  getConsommationMoyenne(idVehicule: number): Observable<number> {
  return this.http.get<number>(`${this.baseUrl}/${idVehicule}/consommation`);
}

}
