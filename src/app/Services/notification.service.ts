import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class NotificationService {
  private baseUrl = 'http://localhost:8090/api/notifications';

  constructor(private http: HttpClient) {}

  // ✅ Créer une notification
  createNotification(notification: Notification): Observable<Notification> {
    return this.http.post<Notification>(`${this.baseUrl}/creer`, notification);
  }

  // ✅ Récupérer les notifications d'un utilisateur
  getNotificationsPourUtilisateur(idUtilisateur: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/utilisateur/${idUtilisateur}`);
  }

  // ✅ Marquer une notification comme lue (ajout de responseType: 'text')
  marquerCommeLue(id: number): Observable<any> {
    return this.http.put(`${this.baseUrl}/marquer-lue/${id}`, {}, {
      responseType: 'text' as 'json'  // <-- important pour éviter HttpErrorResponse avec status 200
    });
  }

  // ✅ Récupérer les notifications pour l'admin
  getNotificationsPourAdmin(idAdmin: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/admin/${idAdmin}`);
  }

  // ✅ Marquer toutes les notifications comme lues
  markAllAsRead(idUtilisateur: number): Observable<void> {
    return this.http.put<void>(`${this.baseUrl}/marquer-lues/${idUtilisateur}`, {});
  }
}
