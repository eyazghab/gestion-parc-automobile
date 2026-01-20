import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { MissionCarburantDtO } from '../models/MissionCarburantDTO';
export interface UtilisateurAvecBonsDTO {
  utilisateurId: number;
  nomUtilisateur: string;
  bons: MissionCarburantDtO[];
}
@Injectable({
  providedIn: 'root'
})
export class CarburantService {
  private apiUrl = 'http://localhost:8090/api/carburants'; // adapte l’URL selon ton backend

  constructor(private http: HttpClient) {}

  getCarburants(): Observable<any[]> {
    return this.http.get<any[]>(this.apiUrl);
  }
    getMissionsCarburant(vehiculeId: number) {
  return this.http.get<MissionCarburantDtO[]>(`${this.apiUrl}/missions-carburant/${vehiculeId}`);
}
 getMissionsParVehicule(vehiculeId: number): Observable<MissionCarburantDtO[]> {
    return this.http.get<MissionCarburantDtO[]>(`${this.apiUrl}/missions-carburant/${vehiculeId}`);
  }
   getUtilisateursAvecBons(): Observable<UtilisateurAvecBonsDTO[]> {
    return this.http.get<UtilisateurAvecBonsDTO[]>(`${this.apiUrl}/utilisateurs-bons`);
  }
}
