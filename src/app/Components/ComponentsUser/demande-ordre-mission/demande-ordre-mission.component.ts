import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';

import { OrdreMissionService } from '../../../Services/ordre-mission.service';
import { VehiculeService } from '../../../Services/vehicule.service';
import { AuthService } from '../../../Services/auth.service';
import { OrdreMission } from '../../../models/ordreMission';

@Component({
  selector: 'app-demande-ordre-mission',
  templateUrl: './demande-ordre-mission.component.html',
  styleUrls: ['./demande-ordre-mission.component.css'],
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
})
export class DemandeOrdreMissionComponent implements OnInit {
  ordreForm: FormGroup;
  vehicules: any[] = [];
  missionsEnCours: any[] = [];
  isLoggedIn = false;

  minDate!: string;
  maxDate!: string;

  constructor(
    private fb: FormBuilder,
    private ordreService: OrdreMissionService,
    private vehiculeService: VehiculeService,
    private authService: AuthService,
    private router: Router,
    private toastr: ToastrService
  ) {
    const now = new Date();
    const oneHourLater = new Date(now.getTime() + 60 * 60 * 1000);
    const endOfMonth = new Date(now.getFullYear(), now.getMonth() + 1, 0, 23, 59);

    this.minDate = this.formatDateTimeLocal(now);
    this.maxDate = this.formatDateTimeLocal(endOfMonth);

    this.ordreForm = this.fb.group({
      objetMission: ['', Validators.required],
      destination: ['', Validators.required],
      motif: ['', Validators.required],
      dateDepart: [this.formatDateTimeLocal(now), Validators.required],
      dateRetour: [this.formatDateTimeLocal(oneHourLater), Validators.required],
    });
  }

  ngOnInit(): void {
    this.isLoggedIn = !!this.authService.getCurrentUser();
    this.loadMissionsEnCours();
    this.watchDatesAndLoadVehicules();
  }

  // ✅ Format compatible datetime-local
  formatDateTimeLocal(date: Date): string {
    const tzOffset = date.getTimezoneOffset() * 60000; // décalage en ms
    const localISOTime = new Date(date.getTime() - tzOffset).toISOString();
    return localISOTime.slice(0, 16); // yyyy-MM-ddTHH:mm
  }

  watchDatesAndLoadVehicules(): void {
  this.ordreForm.get('dateDepart')?.valueChanges.subscribe(() => this.checkAndLoadVehicules());
  this.ordreForm.get('dateRetour')?.valueChanges.subscribe(() => this.checkAndLoadVehicules());
}


  loadVehiculesLibres(): void {
    this.vehiculeService.getVehiculesLibres().subscribe({
      next: (data) => {
        this.vehicules = data;
        if (data.length === 0) {
          this.toastr.warning("Aucun véhicule libre disponible.");
        }
      },
      error: () => {
        this.toastr.error("Erreur lors du chargement des véhicules libres.");
        this.vehicules = [];
      }
    });
  }

checkAndLoadVehicules(): void {
  const departStr = this.ordreForm.get('dateDepart')?.value;
  const retourStr = this.ordreForm.get('dateRetour')?.value;

  if (!departStr || !retourStr) {
    this.vehicules = [];
    return;
  }

  const depart = new Date(departStr);
  const retour = new Date(retourStr);

  if (retour < depart) {
    this.toastr.warning("La date de retour doit être postérieure à la date de départ.");
    this.vehicules = [];
    return;
  }

  // Récupération des véhicules libres depuis le backend
  this.vehiculeService.getVehiculesLibres().subscribe({
    next: (data) => {
      this.vehicules = data.filter(v => 
        v.etat === 'DISPONIBLE' &&
        !this.missionsEnCours.some(m => {
          if (!m.vehicule) return false;
          if (m.vehicule.idVehicule !== v.idVehicule) return false;

          const missionStart = new Date(m.dateDepart);
          const missionEnd = new Date(m.dateRetour);

          // vrai chevauchement seulement si les intervalles se recouvrent
          return depart < missionEnd && retour > missionStart;
        })
      );

      if (this.vehicules.length === 0) {
        this.toastr.warning("Aucun véhicule disponible pour ces dates.");
      }
    },
    error: () => {
      this.toastr.error("Erreur lors du chargement des véhicules.");
      this.vehicules = [];
    }
  });
}



  loadMissionsEnCours(): void {
    this.ordreService.getMissionsEnCours().subscribe({
      next: (data) => this.missionsEnCours = data,
      error: () => this.toastr.error("Erreur lors du chargement des missions.")
    });
  }

checkConflits(dateDepart: string, dateRetour: string, userId: number): string | null {
    const depart = new Date(dateDepart);
    const retour = new Date(dateRetour);

    if (retour < depart) {
      return "La date de retour ne peut pas être antérieure à la date de départ.";
    }

    for (const mission of this.missionsEnCours) {
      const missionStart = new Date(mission.dateDepart);
      const missionEnd = new Date(mission.dateRetour);
      const chevauchement = retour >= missionStart && depart <= missionEnd;

      if (chevauchement) {
        if (mission.utilisateur?.idUtilisateur === userId) {
          return "Vous avez déjà une mission en cours pendant cette période.";
        }
      }
    }

    return null;
  }

  onSubmit(): void {
    if (this.ordreForm.invalid) {
      this.toastr.warning("Veuillez remplir tous les champs requis.");
      return;
    }

    const formValues = this.ordreForm.value;
    const user = this.authService.getCurrentUser();

    if (!user) {
      this.toastr.error("Utilisateur non connecté.");
      return;
    }

const conflit = this.checkConflits(formValues.dateDepart, formValues.dateRetour, user.idUtilisateur);
    if (conflit) {
      this.toastr.error(conflit);
      return;
    }

    const mission: OrdreMission = {
      objetMission: formValues.objetMission,
      destination: formValues.destination,
      motif: formValues.motif,
      dateDepart: formValues.dateDepart,
      dateRetour: formValues.dateRetour,
      utilisateur: { idUtilisateur: user.idUtilisateur },
    };

    this.ordreService.createOrdreMission(mission).subscribe({
      next: () => {
        this.toastr.success("Demande envoyée avec succès.");
        this.router.navigate(['/home']);
      },
      error: (err) => {
        this.toastr.error(err.error?.message || "Erreur lors de l'envoi.");
      }
    });
  }
}
