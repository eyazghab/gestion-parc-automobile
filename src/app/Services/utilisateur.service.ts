import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Utilisateur } from '../models/utilisateur';

@Injectable({
  providedIn: 'root'
})
export class UtilisateurService {
  private baseUrl = 'http://localhost:8090/api/utilisateurs';  // Change l'URL selon ton API backend

  constructor(private http: HttpClient) {}

  // Récupérer la liste des utilisateurs
  getUtilisateurs(): Observable<Utilisateur[]> {
    return this.http.get<Utilisateur[]>(this.baseUrl);
  }
  updateUtilisateur(utilisateur: Utilisateur): Observable<Utilisateur> {
  return this.http.put<Utilisateur>(`${this.baseUrl}/${utilisateur.idUtilisateur}`, utilisateur);
}

}
