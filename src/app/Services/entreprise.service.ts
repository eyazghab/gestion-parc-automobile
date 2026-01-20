import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Entreprise } from '../models/entreprise';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class EntrepriseService {
  private apiUrl = 'http://localhost:8090/api/entreprises';

  constructor(private http: HttpClient) {}

  getEntreprises(): Observable<Entreprise[]> {
    return this.http.get<Entreprise[]>(this.apiUrl);
  }
  
}
