import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class ModeleService {
  private url = 'http://localhost:8090/api/modeles'; // endpoint API
  constructor(private http: HttpClient) {}
  getModelees() : Observable<any[]> {
    return this.http.get<any[]>(this.url);
  }
}