import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Article } from '../models/Article';

@Injectable({
  providedIn: 'root'
})
export class ArticleService {
  private apiUrl = 'http://localhost:8090/api/articles'; // à adapter selon ton backend

  constructor(private http: HttpClient) {}

  // Récupérer tous les articles
  getArticles(): Observable<Article[]> {
    return this.http.get<Article[]>(this.apiUrl);
  }

  // Créer un nouvel article (sans image)
  createArticle(article: Partial<Article>): Observable<Article> {
    return this.http.post<Article>(this.apiUrl, article);
  }

  // Mettre à jour un article existant (edit)
  updateArticle(id: number, article: Partial<Article>): Observable<Article> {
    return this.http.put<Article>(`${this.apiUrl}/${id}`, article);
  }

  // Upload de l'image pour un article
  uploadArticleImage(idArticle: number, file: File): Observable<any> {
    const fd = new FormData();
    fd.append('file', file); // "file" doit correspondre au paramètre côté backend
    return this.http.post(`${this.apiUrl}/upload/${idArticle}`, fd);
  }

  // Désactiver un article
  desactiverArticle(id: number): Observable<void> {
    return this.http.patch<void>(`${this.apiUrl}/${id}/desactiver`, null);
  }
  // Activer un article
  activerArticle(id: number): Observable<void> {
  return this.http.put<void>(`${this.apiUrl}/${id}/activer`, null);
}

  // Récupérer un article par son ID (utile pour edit)
  getArticleById(id: number): Observable<Article> {
    return this.http.get<Article>(`${this.apiUrl}/${id}`);
  }
}
