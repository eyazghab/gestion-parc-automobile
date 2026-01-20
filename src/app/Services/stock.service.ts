import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Stock } from '../models/stock';

@Injectable({ providedIn: 'root' })
export class StockService {
  private apiUrl = 'http://localhost:8090/api/stocks'; // à adapter à ton backend

  constructor(private http: HttpClient) {}

  /** Récupérer tous les stocks */
  getStocks(): Observable<Stock[]> {
    return this.http.get<Stock[]>(this.apiUrl);
  }

  /** Entrée de stock */
  entreeStock(depotId: number, articleId: number, quantite: number, motif: string): Observable<Stock> {
    const params = new HttpParams()
      .set('quantite', quantite.toString())
      .set('motif', motif || '');
    return this.http.post<Stock>(`${this.apiUrl}/entree/${depotId}/${articleId}`, null, { params });
  }

  /** Sortie de stock */
  sortieStock(depotId: number, articleId: number, quantite: number, motif: string): Observable<Stock> {
    const params = new HttpParams()
      .set('quantite', quantite.toString())
      .set('motif', motif || '');
    return this.http.post<Stock>(`${this.apiUrl}/sortie/${depotId}/${articleId}`, null, { params });
  }

  /** Transfert de stock entre deux dépôts */
transfererStock(depotSourceId: number, depotDestinationId: number, articleId: number, quantite: number, motif: string): Observable<void> {
  const params = {
    quantite: quantite.toString(),
    motif: motif
  };

  return this.http.post<void>(
    `${this.apiUrl}/transfert/${depotSourceId}/${depotDestinationId}/${articleId}`,
    null, // body null
    { params }
  );
}


  /** Activer/Désactiver un stock */
 toggleActif(id: number, actif: boolean): Observable<Stock> {
  return this.http.put<Stock>(`${this.apiUrl}/${id}/toggleActif`, null, {
    params: { actif: actif.toString() }
  });
}

  /** Supprimer un stock */
  deleteStock(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
  //  Désactiver un stock
  desactiverStock(id: number): Observable<Stock> {
    return this.http.put<Stock>(`${this.apiUrl}/${id}/desactiver`, {});
  }
  // generer les alerts des stocks 
  getAlertes(): Observable<Stock[]> {
  return this.http.get<Stock[]>(`${this.apiUrl}/alertes`);
}
/**  Mise à jour d’un stock */
updateStock(
  id: number,
  articleId: number,
  depotId: number,
  quantite: number,
  stockMin: number,
  stockAlerte: number,
  dateDernierEntree?: string, // optionnel
  actif?: boolean,            // optionnel
  motif?: string              // optionnel
): Observable<Stock> {

  const payload: any = {
    articleId,
    depotId,
    quantite,
    stockMin,
    stockAlerte
  };

  if (dateDernierEntree) payload.dateDernierEntree = dateDernierEntree;
  if (actif !== undefined) payload.actif = actif;
  if (motif) payload.motif = motif;

  return this.http.put<Stock>(`${this.apiUrl}/${id}`, payload);
}


}

