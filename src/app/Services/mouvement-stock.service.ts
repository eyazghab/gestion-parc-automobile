import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface MouvementStock {
  id: number;
  date: Date;
  type: string; // ENTREE / SORTIE
  quantite: number;
  quantiteAvant: number;
  quantiteApres: number;
  motif: string;
  commentaire?: string;
  stock: any;
  article: any;
}

@Injectable({
  providedIn: 'root'
})
export class MouvementStockService {

  private apiUrl = 'http://localhost:8090/api/mouvements';

  constructor(private http: HttpClient) { }

  getAllMouvements(): Observable<MouvementStock[]> {
    return this.http.get<MouvementStock[]>(this.apiUrl);
  }

  getMouvementsByStock(stockId: number): Observable<MouvementStock[]> {
    return this.http.get<MouvementStock[]>(`${this.apiUrl}/stock/${stockId}`);
  }

  getMouvementsByArticle(articleId: number): Observable<MouvementStock[]> {
    return this.http.get<MouvementStock[]>(`${this.apiUrl}/article/${articleId}`);
  }
}
