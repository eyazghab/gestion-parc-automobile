import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { place } from '../models/place';
import { HttpHeaders } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class PlaceService {
  private baseUrl = 'http://localhost:8090/api/places'; // adapte l’URL si besoin

  constructor(private http: HttpClient) {}

  // 🔄 Récupérer toutes les places
  getAllPlaces(): Observable<place[]> {
    return this.http.get<place[]>(this.baseUrl);
  }

  // 🔍 Récupérer une place par son ID
  getPlaceById(id: number): Observable<place> {
    return this.http.get<place>(`${this.baseUrl}/${id}`);
  }

 // ➕ Créer une nouvelle place
  createPlace(newPlace: place): Observable<place> {
    return this.http.post<place>(this.baseUrl, newPlace, {
      headers: { 'Content-Type': 'application/json' } // important
    });
  }

  // ✏️ Mettre à jour une place existante
  updatePlace(idPlaceParking: number, updatedPlace: place): Observable<place> {
    return this.http.put<place>(`${this.baseUrl}/${idPlaceParking}`, updatedPlace);
  }

  // ❌ Supprimer une place
  deletePlace(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }

  // 🔁 Basculer l’état occupée/libre d’une place
  toggleOccupancy(id: number): Observable<place> {
    return this.http.put<place>(`${this.baseUrl}/${id}/toggleOccupancy`, {});
  }
}
