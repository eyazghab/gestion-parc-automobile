import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { BonCarburant } from '../models/BonCarburant';
import { MissionCarburantDtO } from '../models/MissionCarburantDTO';

@Injectable({
  providedIn: 'root'
})
export class BonCarburantService {

  private apiUrl = 'http://localhost:8090/api/boncarburant';

  constructor(private http: HttpClient) { }

  // Tous les bons
  getAllBons(): Observable<BonCarburant[]> {
    return this.http.get<BonCarburant[]>(`${this.apiUrl}/all`);
  }

  // Bons par véhicule
  getBonsByVehicule(idVehicule: number): Observable<BonCarburant[]> {
    return this.http.get<BonCarburant[]>(`${this.apiUrl}/vehicule/${idVehicule}`);
  }

  // Consommation totale pour un véhicule
  getConsommationTotale(idVehicule: number): Observable<number> {
    return this.http.get<number>(`${this.apiUrl}/vehicule/${idVehicule}/total`);
  }

  // Ajouter un bon
  addBon(bon: any): Observable<BonCarburant> {
    return this.http.post<BonCarburant>(`${this.apiUrl}/ajouter`, bon); // <-- corrigé
  }
  
  // Supprimer un bon
  supprimer(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/supprimer/${id}`); // <-- corrigé
  }

  // Bons par utilisateur
  getBonsByUtilisateur(idUtilisateur: number): Observable<BonCarburant[]> {
    return this.http.get<BonCarburant[]>(`${this.apiUrl}/user/${idUtilisateur}`);
  }

  // Valider ou refuser un bon
  valider(id: number, action: string): Observable<any> {
    return this.http.put(`${this.apiUrl}/${id}/valider?action=${action}`, {}); // <-- corrigé
  }

  // 🔹 Nouveau : calcul carburant nécessaire pour un véhicule
 getCarburantNecessaire(idVehicule: number): Observable<number> {
  return this.http.get<number>(`${this.apiUrl}/vehicule/${idVehicule}/total`);
}
// Mettre à jour la quantité d'un bon
mettreAJourQuantite(id: number, quantite: number): Observable<BonCarburant> {
  return this.http.put<BonCarburant>(`${this.apiUrl}/${id}/modifier-quantite?quantite=${quantite}`, {});
}
 // 🔹 Vérifier la quantité disponible pour un bon
  verifierBon(bonId: number, vehiculeId: number): Observable<number> {
    return this.http.get<number>(`${this.apiUrl}/vehicule/${vehiculeId}/verifier/${bonId}`);
  }
  getBonsEnAttente(): Observable<any[]> {
  return this.http.get<any[]>(`${this.apiUrl}/en-attente`);
}
 getOrdresByVehicule(vehiculeId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/vehicule/${vehiculeId}`);
  }
  // Récupérer les missions liées à un bon
getMissionsParBon(bonId: number) {
  return this.http.get<MissionCarburantDtO[]>(`http://localhost:8090/api/ordres-mission/missions-par-bon/${bonId}`);
}
}
