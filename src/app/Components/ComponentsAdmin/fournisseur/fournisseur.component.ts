import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import {  FournisseurService } from '../../../Services/fournisseur.service';
import { Fournisseur } from '../../../models/fournisseur';

@Component({
  selector: 'app-fournisseur',
  templateUrl: './fournisseur.component.html',
  styleUrls: ['./fournisseur.component.css'],
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
})
export class FournisseurComponent implements OnInit {
  fournisseurs: Fournisseur[] = [];
  searchTerm = '';
  selectedFournisseur: Fournisseur = { nomFournisseur: '', adresse: '', contact: '', typeService: '', actif: true };
  showForm: boolean = false; // Contrôle l'affichage du formulaire

  constructor(private fournisseurService: FournisseurService) {}

  ngOnInit(): void {
    this.loadFournisseurs();
  }

  loadFournisseurs(): void {
    this.fournisseurService.getAll().subscribe(data => {
      this.fournisseurs = data;
    });
  }

  search(): void {
    if (this.searchTerm) {
      this.fournisseurs = this.fournisseurs.filter(f =>
        f.nomFournisseur.toLowerCase().includes(this.searchTerm.toLowerCase())
      );
    } else {
      this.loadFournisseurs();
    }
  }

  // Affiche le formulaire pour ajouter un nouveau fournisseur
  showFormForAdd(): void {
    this.selectedFournisseur = { nomFournisseur: '', adresse: '', contact: '', typeService: '', actif: true };
    this.showForm = true;
  }

  edit(f: Fournisseur): void {
    this.selectedFournisseur = { ...f };
    this.showForm = true; // Affiche le formulaire pour modification
  }

  cancelForm(): void {
    this.showForm = false; // Masque le formulaire
  }

  save(): void {
    if (this.selectedFournisseur.idFournisseur) {
      this.fournisseurService.update(this.selectedFournisseur.idFournisseur, this.selectedFournisseur)
        .subscribe(() => {
          this.loadFournisseurs();
          this.showForm = false;
        });
    } else {
      this.fournisseurService.create(this.selectedFournisseur)
        .subscribe(() => {
          this.loadFournisseurs();
          this.showForm = false;
        });
    }
    this.selectedFournisseur = { nomFournisseur: '', adresse: '', contact: '', typeService: '', actif: true };
  }

  delete(id: number): void {
    this.fournisseurService.delete(id).subscribe(() => this.loadFournisseurs());
  }

  toggleActivation(f: Fournisseur): void {
    const updated = { ...f, actif: !f.actif };
    this.fournisseurService.update(f.idFournisseur!, updated).subscribe(() => this.loadFournisseurs());
  }
}
