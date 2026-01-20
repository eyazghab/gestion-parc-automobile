// src/app/Services/auth.service.ts
import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

export interface Departement {
  id: number;
  nom?: string;
}

export interface Utilisateur {
  idUtilisateur: number;
  nom: string;
  prenom: string;
  email: string;
  motDePasse: string;
  role: string;
  verifie: boolean;
  actif: boolean;
  codeVerification?: string;
  expirationCode?: string;
  telephone?: string;
  cin: string;
  dateEmbauche: string;
  departement: Departement | number; // ← permet de gérer les deux cas
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private currentUserSubject = new BehaviorSubject<Utilisateur | null>(this.getStoredUser());
  currentUser$ = this.currentUserSubject.asObservable();

  private getStoredUser(): Utilisateur | null {
    const user = localStorage.getItem('user');
    return user ? JSON.parse(user) : null;
  }

  setCurrentUser(user: Utilisateur): void {
    localStorage.setItem('user', JSON.stringify(user));
    this.currentUserSubject.next(user);
  }

  getCurrentUser(): Utilisateur | null {
    return this.currentUserSubject.value;
  }

  isLoggedIn(): boolean {
    return this.getCurrentUser() !== null;
  }

  logout(): void {
    localStorage.removeItem('user');
    this.currentUserSubject.next(null);
  }
  getUserRole(): string | null {
  return this.getCurrentUser()?.role || null;
}
getConnectedUser(): any {
    return this.currentUserSubject.value;
  }
}
