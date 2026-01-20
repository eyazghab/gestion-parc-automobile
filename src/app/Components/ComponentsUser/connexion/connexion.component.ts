import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../../Services/auth.service';

@Component({
  selector: 'app-connexion',
  templateUrl: './connexion.component.html',
  styleUrls: ['./connexion.component.css'],
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
})
export class ConnexionComponent {
  loginForm: FormGroup;
  message: string = '';

  constructor(
    private fb: FormBuilder,
    private http: HttpClient,
    private router: Router,
    private authService: AuthService
  ) {
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', Validators.required]
    });
  }

  onSubmit() {
    if (this.loginForm.invalid) {
      this.message = 'Veuillez remplir tous les champs correctement.';
      return;
    }

    const loginData = {
      email: this.loginForm.value.email,
      password: this.loginForm.value.password
    };

    this.http.post<any>('http://localhost:8090/api/auth/login', loginData).subscribe({
      next: res => {
        console.log('Réponse backend :', res);

        // Stocker le token et l'utilisateur
        localStorage.setItem('token', res.token || '');
        localStorage.setItem('user', JSON.stringify(res));

        // Mettre à jour l'utilisateur courant dans le service
        this.authService.setCurrentUser(res);

        // Récupérer le rôle
        const role = this.authService.getUserRole();
        console.log('Rôle de l’utilisateur :', role);

        // Redirection selon rôle
        if (role === 'ADMIN') {
          this.router.navigate(['/admin/admin-dashboard']);
        } else if (role === 'USER') {
          this.router.navigate(['/home']);
        } else {
          this.router.navigate(['/home']); // Par défaut
        }

        this.message = 'Connexion réussie ✅';
      },
      error: err => {
        console.error('Erreur :', err);
        this.message = err.error?.message || 'Identifiants incorrects ou erreur serveur.';
      }
    });
  }

  onCreateAccount() {
    this.router.navigate(['/inscription']);
  }
}
