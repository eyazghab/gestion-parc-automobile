import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Depot } from '../models/Depot';

@Injectable({
  providedIn: 'root'
})
export class DepotService {

  private apiUrl = 'http://localhost:8090/api/depots';  // adapte l’URL selon ton backend

  constructor(private http: HttpClient) {}

  getAllDepots(): Observable<Depot[]> {
    return this.http.get<Depot[]>(this.apiUrl);
  }

  addDepot(depot: Depot): Observable<Depot> {
    return this.http.post<Depot>(this.apiUrl, depot);
  }
  deleteDepot(id: number): Observable<void> {
  return this.http.delete<void>(`${this.apiUrl}/${id}`);
}
}
