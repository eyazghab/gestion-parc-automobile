import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { map, Observable } from 'rxjs';
import { OrdreMission } from '../models/ordreMission';
import { OrdreMissionDto } from '../models/ordreMissionDto';
import { BonCarburant } from '../models/BonCarburant';

@Injectable({
  providedIn: 'root'
})
export class OrdreMissionService {

  private baseUrl = 'http://localhost:8090/api/ordres-mission'; // adapte à ton backend

  constructor(private http: HttpClient) {}

  // Créer une mission
  createOrdreMission(ordre: OrdreMission): Observable<any> {
    return this.http.post(`${this.baseUrl}/create`, ordre);
  }

  // Récupérer toutes les missions
 
  getAllMissions(): Observable<OrdreMissionDto[]> {
    return this.http.get<OrdreMissionDto[]>(`${this.baseUrl}`);
  }

  // Récupérer toutes les missions avec état EN_ATTENTE, ACCEPTEE (missions en cours)
getMissionsEnCours(): Observable<OrdreMissionDto[]> {
  const etats = ['EN_ATTENTE', 'ACCEPTEE'];
  const params = new HttpParams().set('etats', etats.join(','));

  return this.http.get<OrdreMission[]>(`${this.baseUrl}/by-etats`, { params }).pipe(
    map(data =>
      data.map(mission => ({
        id: mission.id!,
        destination: mission.destination,
        motif: mission.motif,
        dateDepart: mission.dateDepart,
        dateRetour: mission.dateRetour,
        etat: (mission as any).etat || 'EN_ATTENTE',
        utilisateurNom: mission.utilisateur?.nom || '',  // obligatoire pour OrdreMissionDto
        vehiculeImmatriculation: mission.vehicule?.immatriculation || '',
        vehiculesDisponibles: [],
        selectedVehiculeId: undefined,
        vehicule: mission.vehicule
          ? {
              idVehicule: mission.vehicule.idVehicule,
              immatriculation: mission.vehicule.immatriculation || ''
            }
          : undefined
      }))
    )
  );
}

 

  // Optionnel : supprimer une mission
  deleteMission(id: number): Observable<any> {
    return this.http.delete(`${this.baseUrl}/delete/${id}`);
  }


  // Optionnel : mettre à jour une mission
  updateMission(ordre: OrdreMission): Observable<any> {
    return this.http.put(`${this.baseUrl}/update/${ordre.id}`, ordre);
  }
// Récupérer toutes les missions d'un utilisateur
  getMesMissions(idUtilisateur: number): Observable<OrdreMission[]> {
    return this.http.get<OrdreMission[]>(`${this.baseUrl}/utilisateur/${idUtilisateur}`);
  }
 annulerOrdreMission(id: number): Observable<OrdreMission> {
    return this.http.put<OrdreMission>(`${this.baseUrl}/annuler/${id}`, {});
  }
   terminerMission(id: number): Observable<any> {
    return this.http.put(`${this.baseUrl}/${id}/terminer`, {});
  }
getOrdresByUtilisateur(idUtilisateur: number) {
  return this.http.get<any[]>(`http://localhost:8090/api/ordres-mission/utilisateur/${idUtilisateur}`);
}
 demarrerMission(id: number): Observable<any> {
    return this.http.put<any>(`${this.baseUrl}/${id}/demarrer`, {});
  }
  getMissionsEnAttente(): Observable<any[]> {
  return this.http.get<any[]>(`${this.baseUrl}/en-attente`);
}
assignVehicule(missionId: number, vehiculeId: number): Observable<OrdreMissionDto> {
  return this.http.put<OrdreMissionDto>(
    `${this.baseUrl}/missions/${missionId}/assign-vehicule/${vehiculeId}`, {}
  );
}

updateEtatMission(id: number, etat: string): Observable<string> {
  return this.http.put<string>(`${this.baseUrl}/${id}/etat?etat=${etat}`, {});
}
getBonsByMission(missionId: number): Observable<BonCarburant[]> {
    return this.http.get<BonCarburant[]>(`${this.baseUrl}/${missionId}/bons`);
  }
}
