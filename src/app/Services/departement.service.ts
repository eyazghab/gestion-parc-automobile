import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Departement } from '../models/departement';

@Injectable({
  providedIn: 'root'
})
export class DepartementService {
  private apiUrl = 'http://localhost:8090/api/departements';

  constructor(private http: HttpClient) {}

  getDepartements(): Observable<Departement[]> {
  return this.http.get<Departement[]>(this.apiUrl);
}

  getDepartementById(id: number): Observable<Departement> {
    return this.http.get<Departement>(`${this.apiUrl}/${id}`);
  }

  updateDepartement(departement: Departement): Observable<Departement> {
    return this.http.put<Departement>(`${this.apiUrl}/${departement.id}`, departement);
  }

  deleteDepartement(id: number): Observable<{ message: string }> {
  return this.http.delete<{ message: string }>(`${this.apiUrl}/${id}`);
}
  createDepartement(dep: Departement): Observable<Departement> {
  return this.http.post<Departement>(this.apiUrl, dep);
}
}
