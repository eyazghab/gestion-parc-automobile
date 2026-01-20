import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class TypeCarrosserieService {
  private url = 'http://localhost:8090/api/type-carrosseries';
  constructor(private http: HttpClient) {}
  getTypeCarrosseries() : Observable<any[]> {
    return this.http.get<any[]>(this.url);
  }
}