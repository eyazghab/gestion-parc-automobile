import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { Utilisateur } from '../../../models/utilisateur';
import { UtilisateurService } from '../../../Services/utilisateur.service';

@Component({
  selector: 'app--utilisateurs',
  templateUrl: './utilisateurs.component.html',
  styleUrls: ['./utilisateurs.component.css'] , // <-- ici
  imports: [CommonModule,FormsModule,ReactiveFormsModule],

})
export class UtilisateursComponent implements OnInit {
  utilisateurs: Utilisateur[] = [];
  utilisateursFiltres: Utilisateur[] = [];
  searchCin: string = '';
  error: string | null = null;

  constructor(private utilisateurService: UtilisateurService) {}

  ngOnInit(): void {
    this.utilisateurService.getUtilisateurs().subscribe({
      next: (data) => {
        this.utilisateurs = data;
        this.utilisateursFiltres = data;
      },
      error: (err) => {
        console.error('Erreur lors du chargement des utilisateurs :', err);
        this.error = "Impossible de charger les utilisateurs.";
      }
    });
  }

  filtrerUtilisateurs(): void {
    const search = this.searchCin.toLowerCase();
    this.utilisateursFiltres = this.utilisateurs.filter(u =>
      u.cin.toLowerCase().includes(search)
    );
  }

  activerDesactiver(utilisateur: Utilisateur): void {
    utilisateur.actif = !utilisateur.actif;
    this.utilisateurService.updateUtilisateur(utilisateur).subscribe();
  }
}
