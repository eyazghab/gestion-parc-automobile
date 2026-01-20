import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AffectationIAService {

  private apiUrl = 'http://localhost:8090/api/ia/affectations'; // adapte le port si nécessaire

  constructor(private http: HttpClient) { }

  optimiser(): Observable<{ [vehiculeId: number]: number }> {
    return this.http.get<{ [vehiculeId: number]: number }>(`${this.apiUrl}/optimiser`);
  }
}
