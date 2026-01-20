import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Vehicule } from '../models/vehicule';
import { Maintenance } from '../models/maintenances';

@Injectable({
  providedIn: 'root'
})
export class MaintenancesService {
  private baseUrl = 'http://localhost:8090/api/maintenances';

  constructor(private http: HttpClient) {}

  // 🔹 Toutes les maintenances
  getAll(): Observable<any[]> {
    return this.http.get<any[]>(this.baseUrl);
  }

  // 🔹 Une maintenance par ID
  getById(id: number): Observable<any> {
    return this.http.get<any>(`${this.baseUrl}/${id}`);
  }
    getMaintenanceById(id: number): Observable<Maintenance> {
    return this.http.get<Maintenance>(`${this.baseUrl}/${id}`);
  }


  // 🔹 Créer une maintenance simple (JSON)
  createSimple(dto: any): Observable<any> {
    return this.http.post<any>(`${this.baseUrl}/simple`, dto, {
      headers: { 'Content-Type': 'application/json' }
    });
  }

  // 🔹 Créer une maintenance depuis un sinistre (FormData + fichiers)
  createFromSinistre(formData: FormData): Observable<any> {
    return this.http.post<any>(`${this.baseUrl}/sinistre`, formData);
  }

  // 🔹 Modifier une maintenance
  update(id: number, dto: any): Observable<any> {
    return this.http.put<any>(`${this.baseUrl}/${id}`, dto, {
      headers: { 'Content-Type': 'application/json' }
    });
  }

  // 🔹 Changer le statut
  changeStatut(id: number, statut: string): Observable<any> {
    return this.http.patch<any>(`${this.baseUrl}/${id}/statut?statut=${statut}`, {});
  }

  // 🔹 Supprimer une maintenance
  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }

  // 🔹 Véhicules en maintenance (optionnel)
  getVehiculesEnMaintenance(): Observable<Vehicule[]> {
    return this.http.get<Vehicule[]>(`${this.baseUrl}/maintenance`);
  }
}
export enum TypeIncident {
  ACCIDENT = 'ACCIDENT',
  PANNE = 'PANNE',
  PREVENTIVE = 'PREVENTIVE'
}