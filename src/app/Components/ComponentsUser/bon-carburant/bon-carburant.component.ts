import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule, FormsModule } from '@angular/forms';
import { Vehicule } from '../../../models/vehicule';
import { Carburant } from '../../../models/Carburant';
import { BonCarburantService } from '../../../Services/bon-carburant.service';
import { VehiculeService } from '../../../Services/vehicule.service';
import { CarburantService } from '../../../Services/carburant.service';
import { ToastrService } from 'ngx-toastr';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../../Services/auth.service';
import { OrdreMissionService } from '../../../Services/ordre-mission.service';

@Component({
  selector: 'app-bon-carburant',
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './bon-carburant.component.html',
  styleUrls: ['./bon-carburant.component.css']
})
export class BonCarburantComponent implements OnInit {

  bonForm!: FormGroup;
  vehicules: Vehicule[] = [];
  carburants: Carburant[] = [];
  vehiculesUtilisateur: any[] = []; // véhicules liés aux missions
  currentUser: any;
  ordresMission: any[] = [];

  constructor(
    private fb: FormBuilder,
    private bonService: BonCarburantService,
    private vehiculeService: VehiculeService,
    private carburantService: CarburantService,
    private ordreMissionService: OrdreMissionService,
    private toastr: ToastrService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.currentUser = this.authService.getCurrentUser();

    // Initialisation du formulaire
    this.bonForm = this.fb.group({
      vehiculeId: [null, Validators.required],
      carburantId: [null, Validators.required],
      quantite: [0, [Validators.required, Validators.min(1)]],
      montant: [0, [Validators.min(0)]], 
      dateAchat: [this.formatDate(new Date()), Validators.required],
       missionId: [null]  // mission liée au véhicule sélectionné
    });

    this.loadCarburants();
    this.loadVehiculesAvecOrdres();
  }

  // Charger tous les carburants
  loadCarburants(): void {
    this.carburantService.getCarburants().subscribe({
      next: data => this.carburants = data,
      error: () => this.toastr.error("Erreur lors du chargement des carburants")
    });
  }

  // Charger les véhicules liés aux ordres de mission de l'utilisateur connecté
 loadVehiculesAvecOrdres(): void {
  this.vehiculeService.getVehicules().subscribe({
    next: (vehicules: Vehicule[]) => {
      this.vehicules = vehicules;

      this.ordreMissionService.getOrdresByUtilisateur(this.currentUser.idUtilisateur)
        .subscribe({
          next: (ordres: any[]) => {
            this.ordresMission = ordres;

            // Liste unique de véhicules liés à des ordres
            this.vehiculesUtilisateur = ordres
              .filter(o => o.vehiculeId != null)
              .map(o => this.vehicules.find(v => v.idVehicule === o.vehiculeId))
              .filter((v, index, self) =>
                v != null && index === self.findIndex(obj => obj!.idVehicule === v!.idVehicule)
              ) as Vehicule[];
          },
          error: () => this.toastr.error("Erreur lors du chargement des ordres de mission")
        });
    },
    error: () => this.toastr.error("Erreur lors du chargement des véhicules")
  });
}


  // Quand l'utilisateur choisit un véhicule, récupérer la mission liée
missionsPourVehicule: any[] = [];

onVehiculeChange(event: any): void {
  const vehiculeId = +event.target.value;

  // Missions acceptées pour ce véhicule
  this.missionsPourVehicule = this.ordresMission.filter(
    m => m.vehiculeId === vehiculeId && m.etat === 'ACCEPTEE'
  );

  // Pré-sélectionner si une seule mission
  if (this.missionsPourVehicule.length === 1) {
    this.bonForm.patchValue({ missionId: this.missionsPourVehicule[0].id });
  } else {
    this.bonForm.patchValue({ missionId: null });
  }
}







  // Ajouter un bon carburant
  ajouterBon(): void {
    if (this.bonForm.invalid) return;

    const bonDTO = {
      ...this.bonForm.value,
      utilisateurId: this.currentUser?.idUtilisateur
    };

    this.bonService.addBon(bonDTO).subscribe({
      next: () => {
        this.toastr.success('Bon carburant ajouté avec succès !');
        this.bonForm.reset({ dateAchat: this.formatDate(new Date()) });
        this.loadVehiculesAvecOrdres(); // rafraîchir la liste
      },
      error: err => {
        console.error(err);
        this.toastr.error('Erreur lors de l\'ajout du bon carburant.');
      }
    });
  }

  // Formater la date pour input type="date"
  formatDate(date: Date): string {
    const yyyy = date.getFullYear();
    const mm = String(date.getMonth() + 1).padStart(2, '0');
    const dd = String(date.getDate()).padStart(2, '0');
    return `${yyyy}-${mm}-${dd}`;
  }
}
