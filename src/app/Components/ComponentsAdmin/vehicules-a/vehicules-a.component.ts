import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Router, RouterModule } from '@angular/router';
import { FormBuilder, FormGroup, Validators, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { Vehicule } from '../../../models/vehicule';
import { VehiculeService, PredictionDTO } from '../../../Services/vehicule.service';
import { AuthService } from '../../../Services/auth.service';
import { ToastrService } from 'ngx-toastr';
import { ModeleService } from '../../../Services/modele.service';
import { TypeCarrosserieService } from '../../../Services/type-carrosserie.service';
import { MaintenanceIAService } from '../../../Services/maintenance-ia.service';
import { MaintenancesService } from '../../../Services/maintenances.service';

@Component({
  selector: 'app-vehicules-a',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule,RouterModule],
  templateUrl: './vehicules-a.component.html',
  styleUrls: ['./vehicules-a.component.css']
})
export class VehiculesAComponent implements OnInit {
  vehicules: Vehicule[] = [];
  vehiculesFiltres: Vehicule[] = [];
  error: string | null = null;
  searchImmatriculation: string = '';
  showAddForm = false;
  vehiculeForm!: FormGroup;
  predictions: { [key: number]: string } = {};
  training = false;
  modelTrained = false;
  message: string | null = null;
  modeles: any[] = [];
  typeCarrosseries: any[] = [];

  sortColumn: string = '';
  sortDirection: 'asc' | 'desc' = 'asc';
loadingIA: boolean = false;

  etatOrdre: string[] = [
    'DISPONIBLE',
    'EN_ATTENTE',
    'EN_COURS',
    'En_TRAITEMENT',
    'TRAITE',
    'EN_PANNE'
  ];

  constructor(
    private vehiculeService: VehiculeService,
    private modeleService: ModeleService,
    private typeCarrosserieService: TypeCarrosserieService,
    private authService: AuthService,
    private router: Router,
    private fb: FormBuilder,
    private toastr: ToastrService,
    private iaService: MaintenanceIAService,
      private maintenanceService: MaintenancesService,


  ) { }

  ngOnInit(): void {
    this.initForm();
    this.loadVehicules();
    this.loadModeles();
    this.loadTypeCarrosseries();
  }

  initForm() {
    this.vehiculeForm = this.fb.group({
      immatriculation: ['', Validators.required],
      numeroChassis: ['', Validators.required],
      dateCircu: ['', Validators.required],
      date_acquisition: ['', Validators.required],
      kilometrageActuel: ['', Validators.required],
      anneeModel: ['', [Validators.required, Validators.min(1900)]],
      typeCarburant: ['', Validators.required],
      etat: ['EN_ATTENTE', Validators.required],
      modele: ['', Validators.required],
      typeCarrosserie: ['', Validators.required]
    });
  }

  loadVehicules() {
    this.vehiculeService.getVehicules().subscribe({
      next: data => {
        this.vehicules = data;
        this.vehiculesFiltres = data;
      },
      error: (err: HttpErrorResponse) => {
        this.toastr.error('Erreur lors du chargement des véhicules');
      }
    });
  }

  loadModeles() {
    this.modeleService.getModelees().subscribe({
      next: data => this.modeles = data,
      error: () => this.toastr.error('Erreur chargement modèles')
    });
  }

  loadTypeCarrosseries() {
    this.typeCarrosserieService.getTypeCarrosseries().subscribe({
      next: data => this.typeCarrosseries = data,
      error: () => this.toastr.error('Erreur chargement types carrosserie')
    });
  }

  toggleAddForm(): void {
    this.showAddForm = !this.showAddForm;
    if (!this.showAddForm) {
      this.vehiculeForm.reset({ etat: 'EN_ATTENTE' });
    }
  }

  onAddSubmit(): void {
    if (this.vehiculeForm.invalid) {
      this.toastr.warning('Veuillez remplir tous les champs obligatoires');
      return;
    }

    const formValue = this.vehiculeForm.value;
    const userJson = localStorage.getItem('user');
    if (!userJson) { this.toastr.error('Utilisateur non connecté'); return; }
    const user = JSON.parse(userJson);
    const userId = user.idUtilisateur || user.id;

    const nouveauVehicule = {
      ...formValue,
      etat: formValue.etat.toUpperCase(),
      modele: { id: Number(formValue.modele) },
      typeCarrosserie: { id: Number(formValue.typeCarrosserie) },
      kilometrageActuel: Number(formValue.kilometrageActuel),
      utilisateur: { idUtilisateur: userId }
    };

    this.vehiculeService.addVehicule(nouveauVehicule).subscribe({
      next: (v: Vehicule) => {
        this.vehicules.push(v);
        this.filtrerVehicules();
        this.toggleAddForm();
        this.toastr.success('Véhicule ajouté avec succès');
      },
      error: (err) => {
        this.toastr.error(err?.error?.message || err.message || 'Erreur lors de l\'ajout du véhicule');
      }
    });
  }

  editVehicule(vehicule: Vehicule): void {
    this.router.navigate(['admin/vehicules/edit', vehicule.idVehicule]);
  }

  changerEtat(v: Vehicule, nouvelEtat: string) {
    this.vehiculeService.changerEtatVehicule(v.idVehicule, nouvelEtat).subscribe({
      next: () => this.toastr.success('État du véhicule mis à jour'),
      error: () => this.toastr.error('Erreur lors de la mise à jour de l\'état')
    });
  }

  filtrerVehicules(): void {
    const immat = this.searchImmatriculation.trim().toLowerCase();
    this.vehiculesFiltres = this.vehicules.filter(v =>
      !immat || (v.immatriculation && v.immatriculation.toLowerCase().includes(immat))
    );
  }

  trierVehicules(colonne: keyof Vehicule) {
    if (this.sortColumn === colonne) {
      this.sortDirection = this.sortDirection === 'asc' ? 'desc' : 'asc';
    } else {
      this.sortColumn = colonne;
      this.sortDirection = 'asc';
    }

    this.vehiculesFiltres.sort((a, b) => {
      let valA: any = a[colonne];
      let valB: any = b[colonne];

      if (colonne === 'etat') {
        valA = this.etatOrdre.indexOf(valA);
        valB = this.etatOrdre.indexOf(valB);
      }

      if (valA instanceof Date) valA = valA.getTime();
      if (valB instanceof Date) valB = valB.getTime();
      if (typeof valA === 'string') valA = valA.toLowerCase();
      if (typeof valB === 'string') valB = valB.toLowerCase();

      return (valA < valB ? -1 : valA > valB ? 1 : 0) * (this.sortDirection === 'asc' ? 1 : -1);
    });
  }

  /** Entraîner le modèle IA */
/** Entraîner le modèle IA */
/* entrainerModelIA() {
  this.training = true;
  this.error = null;
  this.message = null;

  this.iaService.trainModel().subscribe({
    next: (res: any) => {
      this.training = false;
      this.modelTrained = true;
      this.message = res.message || '✅ Modèle IA entraîné avec succès';
      this.toastr.success(this.message || '', 'IA');
      console.log('Résultat entraînement:', res);
    },
    error: (err) => {
      this.training = false;
      this.error = err.error?.message || '❌ Erreur lors de l\'entraînement';
      this.toastr.error(this.error || '', 'IA');
      console.error('Erreur backend IA:', err);
    }
  });
} */



  /** Prédire la maintenance d’un véhicule */
predireMaintenance(vehiculeId: number, dernierCout: number = 0) {
  this.predictions[vehiculeId] = '⏳ En cours...';

  this.iaService.predict(vehiculeId, dernierCout).subscribe({
    next: res => {
      let texte = res.prediction ?? res.message ?? '❌ Pas de prédiction';

      // Si le texte contient ": ", on prend uniquement la partie après
      if (texte.includes(':')) {
        texte = texte.split(':')[1].trim();
      }

      this.predictions[vehiculeId] = texte;
    },
    error: err => {
      this.predictions[vehiculeId] = '❌ Erreur lors de la prédiction';
      console.error('Erreur backend IA:', err);
    }
  });
}
formatPrediction(pred: string): string {
  // exemple : transforme la prédiction brute en message simple
  if (pred === 'urgent') return '❌ Maintenance immédiate';
  if (pred === 'nonUrgent') return '✅ Pas de maintenance urgente';
  return pred; // au cas où il y a d'autres valeurs
}




  getBadgeClass(pred: string) {
    if (pred.includes('⚠️')) return 'bg-warning text-dark';
    if (pred.includes('✅')) return 'bg-success text-white';
    if (pred.includes('❌')) return 'bg-danger text-white';
    return 'bg-secondary text-dark';
  }

  voirDetails(vehicule: Vehicule): void {
    this.router.navigate(['admin/vehicules', vehicule.idVehicule]);
  }

  logout() {
    this.authService.logout();
    this.toastr.info('Vous êtes déconnecté');
    this.router.navigate(['/home']);
  }
/* forceTrainModel() {
  this.iaService.trainModel().subscribe({
    next: (res) => {
      this.toastr.success('✅ Modèle entraîné avec succès');
      console.log('Résultat entraînement:', res);
    },
    error: (err) => {
      this.toastr.error('❌ Erreur lors de l\'entraînement');
      console.error('Erreur backend IA:', err);
    }
  });
} */

entrainerModelIA() {
  this.training = true;
  this.error = null;
  this.message = null;

  this.iaService.trainModel().subscribe({
    next: (res: any) => {
      this.training = false;
      this.modelTrained = true;
      this.message = res.message ?? '✅ Modèle IA entraîné avec succès';
this.toastr.success(this.message ?? '', 'IA'); // ou ''
      this.refreshPredictions();
    },
    error: (err: HttpErrorResponse) => {
      this.training = false;
      this.error = err.error?.message ?? '❌ Erreur lors de l\'entraînement';
this.toastr.error(this.error ?? '', 'IA');
      console.error('Erreur backend IA:', err);
    }
  });
}

refreshPredictions(): void {
  this.vehiculesFiltres.forEach(v => {
    this.predictions[v.idVehicule] = '⏳ En cours...';
    this.iaService.predict(v.idVehicule, (v as any).coutTotal ?? 0).subscribe({
      next: res => {
        this.predictions[v.idVehicule] = res.prediction
          ? `🚗 ${res.vehicule}: ${res.prediction}`
          : res.message ?? '❌ Pas de prédiction';
      },
      error: err => {
        this.predictions[v.idVehicule] = '❌ Erreur lors de la prédiction';
        console.error('Erreur backend IA:', err);
      }
    });
  });
}




}
