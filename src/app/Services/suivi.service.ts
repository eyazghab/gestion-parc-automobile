import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { SuiviDTO } from '../models/SuiviDTO';

export interface Suivi {
  idVehicule: number;
  immatriculation: string;
  kmActuel: number;
  echeances: any;
  dateDerniersuivi: string;
  dateKm: string;
  dateTemp: string;
  alertes: string[];
  alerteMaintenance: boolean;
  
}

export interface AlerteVehiculeDTO {
  id: number;
  vehicule: string;
  message: string;
  resolue: boolean;
  date?: string; // ajout pour le calendrier
  selected?: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class SuiviService {
  private apiUrl = 'http://localhost:8090/api/suivis';

  constructor(private http: HttpClient) { }

  // --- Suivis CRUD ---
  
  // Retourne tous les suivis DTO
  getAllSuivis(): Observable<SuiviDTO[]> {
    return this.http.get<SuiviDTO[]>(`${this.apiUrl}`);
  }

  // Retourne le dernier suivi d’un véhicule
  getDernierSuivi(idVehicule: number): Observable<SuiviDTO> {
    return this.http.get<SuiviDTO>(`${this.apiUrl}/vehicule/${idVehicule}/dernier`);
  }

  getSuiviById(id: number): Observable<Suivi> {
    return this.http.get<Suivi>(`${this.apiUrl}/${id}`);
  }

  createSuivi(suivi: Suivi): Observable<Suivi> {
    return this.http.post<Suivi>(`${this.apiUrl}`, suivi);
  }

  updateSuivi(id: number, suivi: Suivi): Observable<Suivi> {
    return this.http.put<Suivi>(`${this.apiUrl}/${id}`, suivi);
  }

  deleteSuivi(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  // --- Alertes ---
  getAlertesVehicule(idVehicule: number): Observable<string[]> {
    return this.http.get<string[]>(`${this.apiUrl}/vehicule/${idVehicule}/alertes`);
  }

  updateAlertesVehicule(idVehicule: number): Observable<Suivi> {
    return this.http.put<Suivi>(`${this.apiUrl}/vehicule/${idVehicule}/mettre-a-jour-alertes`, {});
  }

  getAlertesTousVehicules(): Observable<AlerteVehiculeDTO[]> {
    return this.http.get<AlerteVehiculeDTO[]>(`${this.apiUrl}/alertes-tous-vehicules`);
  }

  getAlertesGroupes(): Observable<{ [vehicule: string]: string[] }> {
    return this.http.get<{ [vehicule: string]: string[] }>(`${this.apiUrl}/alertes-groupes`);
  }
    // Marquer une alerte comme résolue
  marquerAlerteCommeResolue(idAlerte: number): Observable<AlerteVehiculeDTO> {
    return this.http.put<AlerteVehiculeDTO>(`${this.apiUrl}/${idAlerte}/resolue`, {});
  }

   // --- Dernier suivi d’un véhicule ---
  getDernierSuiviVehicule(idVehicule: number): Observable<SuiviDTO> {
    return this.http.get<SuiviDTO>(`${this.apiUrl}/vehicule/${idVehicule}/dernier`);
  }
    // 🔹 Vérifier l'état réel d’un véhicule (calculé par le backend)
  getEtatVehicule(idVehicule: number): Observable<string> {
    return this.http.get(`${this.apiUrl}/etat/${idVehicule}`, { responseType: 'text' });
  }
    getHistoriqueVehicule(idVehicule: number): Observable<SuiviDTO[]> {
    return this.http.get<SuiviDTO[]>(`${this.apiUrl}/historique/${idVehicule}`);
  }
}
