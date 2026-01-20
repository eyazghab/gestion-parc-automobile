import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { ToastrService } from 'ngx-toastr';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { AuthService, Utilisateur } from '../../../Services/auth.service';

@Component({
  selector: 'app-profil',
  templateUrl: './profil.component.html',
  styleUrls: ['./profil.component.css'],
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, FormsModule],
})
export class ProfilComponent implements OnInit {
  user: Utilisateur | null = null;
  editable: boolean = false;
  departements: any[] = [];

  constructor(
    private authService: AuthService,
    private http: HttpClient,
    private toastr: ToastrService
  ) {}

  ngOnInit(): void {
    const localUser = this.authService.getCurrentUser();

    if (localUser) {
      // ✅ Charger utilisateur complet depuis le backend
      this.http.get<Utilisateur>(`http://localhost:8090/api/utilisateurs/${localUser.idUtilisateur}`).subscribe({
        next: (res) => {
          this.user = res;
        },
        error: (err) => {
          console.error('Erreur lors du chargement de l’utilisateur complet', err);
        }
      });

      // ✅ Charger les départements
      this.http.get<any[]>('http://localhost:8090/api/departements').subscribe({
        next: (data) => this.departements = data,
        error: (err) => console.error('Erreur chargement départements', err)
      });
    }
  }

  enableEdit() {
    this.editable = true;
  }

  saveChanges() {
    if (!this.user || !this.user.idUtilisateur) return;

    // ⚠️ S'assurer que departement est bien un objet avec id
    if (typeof this.user.departement === 'number') {
      this.user.departement = { id: this.user.departement };
    }

    this.http.put(`http://localhost:8090/api/utilisateurs/${this.user.idUtilisateur}`, this.user).subscribe({
      next: () => {
        this.editable = false;
        this.toastr.success('Informations mises à jour avec succès ✅');
        this.authService.setCurrentUser(this.user!);
      },
      error: (err) => {
        console.error('Erreur de mise à jour :', err);
        this.toastr.error('Erreur lors de la mise à jour ❌');
      }
    });
  }
}
