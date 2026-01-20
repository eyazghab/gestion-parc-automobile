import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { Parking } from '../../../models/parking';
import { ParkingService } from '../../../Services/parkings.service';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-parking',
  templateUrl: './parking.component.html',
  styleUrls: ['./parking.component.css'],
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  standalone: true,
})
export class ParkingComponent implements OnInit {
  parkings: Parking[] = [];
  searchTerm = '';
  currentPage = 1;

  showForm = false;
  newParking: Parking = { id: 0, nomParking: '', adresse: '', capacite: 0, estDisponible: true };
  parkingEnEdition: Parking | null = null;

  constructor(
    private parkingService: ParkingService,
    private toastr: ToastrService
  ) {}

  ngOnInit(): void {
    this.getParkings();
  }

  // Charger les parkings depuis le backend
  getParkings(): void {
    this.parkingService.getParkings().subscribe({
      next: (data) => (this.parkings = data),
      error: (err) => {
        console.error('Erreur lors de la récupération des parkings', err);
        this.toastr.error('Impossible de charger les parkings');
      },
    });
  }

  // Filtrer la liste selon la recherche
  filteredParkings(): Parking[] {
    if (!this.searchTerm.trim()) return this.parkings;
    return this.parkings.filter((p) =>
      p.nomParking.toLowerCase().includes(this.searchTerm.toLowerCase())
    );
  }

  // Préparer l’édition
  modifier(parking: Parking): void {
    this.parkingEnEdition = { ...parking };
    this.newParking = { ...parking };
    this.showForm = true;
  }
toggleForm(): void {
  if (this.showForm) {
    this.annuler(); // fermer le formulaire
  } else {
    this.showForm = true;
    this.newParking = { id: 0, nomParking: '', adresse: '', capacite: 0, estDisponible: true };
    this.parkingEnEdition = null;
  }
}

  // Sauvegarder (ajout ou modification)
  enregistrerParking(): void {
  if (this.parkingEnEdition) {
    // 🔁 Modification
    this.parkingService
      .modifierParking(this.parkingEnEdition.id!, this.newParking)
      .subscribe({
        next: (updated) => {
          const index = this.parkings.findIndex(
            (p) => p.id === this.parkingEnEdition?.id
          );
          if (index !== -1) this.parkings[index] = updated;
          this.toastr.success('Parking modifié avec succès');
          this.annuler();
        },
        error: () => this.toastr.error('Erreur lors de la modification'),
      });
  } else {
    // 🆕 Ajout
    const parkingToAdd = { ...this.newParking };
    delete (parkingToAdd as any).id; // 🚀 Supprimer l'id avant envoi

    this.parkingService.ajouterParking(parkingToAdd).subscribe({
      next: (created) => {
        this.parkings.push(created);
        this.toastr.success('Parking ajouté avec succès');
        this.annuler();
      },
      error: () => this.toastr.error("Erreur lors de l'ajout"),
    });
  }
}


  // Supprimer un parking
  supprimer(parking: Parking): void {
    this.parkingService.supprimerParking(parking.id!).subscribe({
      next: () => {
        this.parkings = this.parkings.filter((p) => p.id !== parking.id);
        this.toastr.success(`Parking "${parking.nomParking}" supprimé !`);
      },
      error: () => this.toastr.error('Erreur lors de la suppression'),
    });
  }

  // Changer disponibilité
  changerDisponibilite(parking: Parking): void {
    const updatedParking = { ...parking, estDisponible: !parking.estDisponible };
    this.parkingService.modifierParking(parking.id!, updatedParking).subscribe({
      next: () => {
        parking.estDisponible = !parking.estDisponible;
        this.toastr.info(
          `Parking "${parking.nomParking}" est maintenant ${
            parking.estDisponible ? 'disponible' : 'indisponible'
          }`
        );
      },
      error: () =>
        this.toastr.error("Erreur lors du changement de disponibilité"),
    });
  }

  // Réinitialiser formulaire
  annuler(): void {
    this.showForm = false;
    this.newParking = { id: 0, nomParking: '', adresse: '', capacite: 0, estDisponible: true };
    this.parkingEnEdition = null;
  }
}
