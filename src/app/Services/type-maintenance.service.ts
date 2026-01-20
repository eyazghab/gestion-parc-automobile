// src/app/Services/type-maintenance.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface TypeMaintenance {
  id: number;
  libelle: string;
  description?: string;
}

@Injectable({
  providedIn: 'root'
})
export class TypeMaintenanceService {
  private apiUrl = 'http://localhost:8090/api/types-maintenance'; // adapte l'URL à ton backend

  constructor(private http: HttpClient) {}

  getAllTypes(): Observable<TypeMaintenance[]> {
    return this.http.get<TypeMaintenance[]>(this.apiUrl);
  }
}
