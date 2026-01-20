import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Technicien } from '../models/technicien';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class TechnicienService {
  private baseUrl = 'http://localhost:8090/api/techniciens';

  constructor(private http: HttpClient) {}

  getAllTechniciens(): Observable<Technicien[]> {
    return this.http.get<Technicien[]>(this.baseUrl);
  }

  updateTechnicien(id: number, technicien: Technicien): Observable<Technicien> {
  return this.http.put<Technicien>(`${this.baseUrl}/${id}`, technicien);
}

  deleteTechnicien(idTechnicien: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${idTechnicien}`);
  }

  addTechnicien(technicien: Technicien): Observable<Technicien> {
  return this.http.post<Technicien>(this.baseUrl, technicien);
}
}
