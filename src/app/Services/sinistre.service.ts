import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Sinistre } from '../models/sinistre';


@Injectable({
  providedIn: 'root'
})
export class SinistreService {
   private apiUrl = 'http://localhost:8090/api/sinistres';

  constructor(private http: HttpClient) { }

  getAll(): Observable<Sinistre[]> {
    return this.http.get<Sinistre[]>(this.apiUrl);
  }

  getById(id: number): Observable<Sinistre> {
    return this.http.get<Sinistre>(`${this.apiUrl}/${id}`);
  }

  addSinistre(formData: FormData): Observable<Sinistre> {
    return this.http.post<Sinistre>(`${this.apiUrl}/add`, formData);
  }

  updateSinistre(id: number, sinistre: Partial<Sinistre>): Observable<Sinistre> {
    return this.http.put<Sinistre>(`${this.apiUrl}/${id}`, sinistre);
  }

  changeEtat(id: number, nouvelEtat: string): Observable<Sinistre> {
  return this.http.put<Sinistre>(`${this.apiUrl}/${id}/etat?nouvelEtat=${nouvelEtat}`, {});
}

  deleteSinistre(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
    prendreEnCharge(idSinistre: number, technicienId: number, circulation: string): Observable<Sinistre> {
    const params = new HttpParams()
      .set('technicienId', technicienId)
      .set('circulation', circulation);
    return this.http.put<Sinistre>(`${this.apiUrl}/${idSinistre}/prendre-en-charge`, null, { params });
  }
getAlertes(): Observable<Sinistre[]> {
  return this.http.get<Sinistre[]>('http://localhost:8090/api/sinistres/alertes');
}
}

