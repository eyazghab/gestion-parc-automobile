import { Component, OnInit } from '@angular/core';
import { Depot } from '../../../models/Depot';
import { DepotService } from '../../../Services/depot.service';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

@Component({
  selector: 'app-depot',
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './depot.component.html',
  styleUrl: './depot.component.css'
})
export class DepotComponent implements OnInit {

 depots: Depot[] = [];
  error = '';
  showAddForm = false;

  newDepot: Depot = {
    nom: '',
    localisation: ''
  };

  constructor(private depotService: DepotService) {}

  ngOnInit(): void {
    this.loadDepots();
  }

  loadDepots(): void {
    this.depotService.getAllDepots().subscribe({
      next: data => this.depots = data,
      error: err => this.error = 'Erreur lors du chargement des dépôts'
    });
  }

  addDepot(): void {
    this.depotService.addDepot(this.newDepot).subscribe({
      next: added => {
        this.depots.push(added);
        this.newDepot = { nom: '', localisation: '' };
        this.showAddForm = false;
        this.error = '';
      },
      error: err => this.error = 'Erreur lors de l\'ajout du dépôt'
    });
  }

  deleteDepot(id: number | undefined): void {
    if (!id) return;
    if (!confirm('Voulez-vous vraiment supprimer ce dépôt ?')) return;

    this.depotService.deleteDepot(id).subscribe({
      next: () => this.depots = this.depots.filter(d => d.idDepot !== id),
      error: () => this.error = 'Erreur lors de la suppression'
    });
  }
}