import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Commande } from '../models/commande';

@Injectable({
  providedIn: 'root'
})
export class CommandeService {
  private apiUrl = 'http://localhost:8090/api/commandes';

  constructor(private http: HttpClient) { }

  // Récupérer toutes les commandes
  getCommandes(): Observable<Commande[]> {
    return this.http.get<Commande[]>(this.apiUrl);
  }

  // Récupérer une commande par ID
  getById(id: number): Observable<Commande> {
    return this.http.get<Commande>(`${this.apiUrl}/${id}`);
  }

  // ✅ Créer une commande avec articles + quantités
  createWithQuantites(
    fournisseurId: number,
    articleIds: number[],
    quantites: number[],
    commentaire?: string
  ): Observable<Commande> {
    let params = new HttpParams().set('fournisseurId', fournisseurId.toString());

    articleIds.forEach(id => params = params.append('articleIds', id.toString()));
    quantites.forEach(q => params = params.append('quantites', q.toString()));

    if (commentaire) {
      params = params.set('commentaire', commentaire);
    }

    return this.http.post<Commande>(`${this.apiUrl}/create-with-quantites`, null, { params });
  }

  // ✅ Mettre à jour une commande existante
  updateCommande(
    commandeId: number,
    fournisseurId: number,
    articleIds: number[],
    quantites: number[],
    commentaire?: string
  ): Observable<Commande> {
    let params = new HttpParams().set('fournisseurId', fournisseurId.toString());

    articleIds.forEach(id => params = params.append('articleIds', id.toString()));
    quantites.forEach(q => params = params.append('quantites', q.toString()));

    if (commentaire) {
      params = params.set('commentaire', commentaire);
    }

    return this.http.put<Commande>(`${this.apiUrl}/update/${commandeId}`, null, { params });
  }

  // ✅ Annuler une commande avec justification
annulerCommande(id: number, justification: string): Observable<any> {
  return this.http.put<any>(`${this.apiUrl}/${id}/annuler`, { justification }, {
    headers: { 'Content-Type': 'application/json' }
  });
}



  // ✅ Passer une commande en cours
 passerEnCours(id: number) {
  return this.http.put<any>(`${this.apiUrl}/${id}/commander`, {}); 
}

  // ✅ Supprimer une commande
  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/delete/${id}`);
  }
  livrerCommande(id: number): Observable<any> {
  return this.http.put(`${this.apiUrl}/livrer/${id}`, {});
}
telechargerPDF(commandeId: number): Observable<Blob> {
  return this.http.get(`http://localhost:8090/api/commandes/${commandeId}/facture/pdf`, {
    responseType: 'blob' // très important pour un fichier binaire
  });
}

}
