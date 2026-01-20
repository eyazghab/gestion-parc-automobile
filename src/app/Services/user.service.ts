import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';

export interface User {
  id: number;
  nom: string;
  prenom: string;
  email: string;
  telephone: string;
  // autres champs...
}

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private currentUserSubject = new BehaviorSubject<User | null>(null);
  public currentUser$ = this.currentUserSubject.asObservable();

  constructor(private http: HttpClient) {
    this.loadUserFromStorage();
  }

  private loadUserFromStorage() {
    const userJson = localStorage.getItem('currentUser');
    if (userJson) {
      this.currentUserSubject.next(JSON.parse(userJson));
    }
  }

  setUser(user: User) {
    localStorage.setItem('currentUser', JSON.stringify(user));
    this.currentUserSubject.next(user);
  }

  clearUser() {
    localStorage.removeItem('currentUser');
    localStorage.removeItem('token');
    this.currentUserSubject.next(null);
  }

  getUserFromApi(userId: number): Observable<User> {
    return this.http.get<User>(`http://localhost:8090/api/users/${userId}`);
  }

  updateUser(userId: number, updatedUser: Partial<User>): Observable<User> {
    return this.http.put<User>(`http://localhost:8090/api/users/${userId}`, updatedUser);
  }
}
