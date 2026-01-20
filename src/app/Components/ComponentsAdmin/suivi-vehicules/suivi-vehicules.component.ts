import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { SuiviDTO } from '../../../models/SuiviDTO';



@Component({
  selector: 'app-suivi-vehicules',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './suivi-vehicules.component.html',
  styleUrls: ['./suivi-vehicules.component.css']
})
export class SuiviVehiculesComponent implements OnInit {

  suivis: SuiviDTO[] = [];
  loading = true;
  error: string | null = null;
  showAddForm = false;
  maintenanceForm!: FormGroup;
  etatFiltre: string = ''; // '', 'bon', 'accident', 'panne', 'maintenance'

  constructor(private http: HttpClient,private fb: FormBuilder,private router: Router) { }

  ngOnInit(): void {
      this.maintenanceForm = this.fb.group({
    vehiculeId: [null, Validators.required],
    observations: ['']
    // autres champs si besoin
  });
    this.getAllSuivis();
  }

  // --- Récupération des suivis ---
getAllSuivis() {
  this.http.get<SuiviDTO[]>('http://localhost:8090/api/suivis')
    .subscribe({
      next: (data) => {
        this.suivis = data.filter(s =>
          s.etatVehicule !== 'EN_PANNE' &&
          s.etatVehicule !== 'ACCIDENTE' &&
          s.etatVehicule !== 'MAINTENANCE' // ✅ AJOUT ICI
        );
        this.loading = false;
      },
      error: (err) => {
        console.error(err);
        this.error = 'Erreur lors du chargement des suivis.';
        this.loading = false;
      }
    });
}




  // --- Calcul état simplifié ---
  getEtatSimple(suivi: SuiviDTO): string {
    let score = 0;
    const poids = 20;
    if (suivi.kilometresDepuisFreins > 20000) score += poids;
    if (suivi.kilometresDepuisVidange > 15000) score += poids;
    if (suivi.dureeVieBatterie > 24) score += poids;
    if (suivi.nombrePannes > 0) score += poids;
    if (suivi.nombreAccidents > 0) score += poids;

    if (score < 50) return 'bon';
    else if (score >= 50 && score <= 75) return 'maintenance';
    else return 'panne';
  }

  // --- Texte affichage état ---
  getTexteEtat(etat: string | undefined): string {
    switch (etat) {
      case 'bon': return 'En Bonne Santé';
      case 'accident': return 'Accidentés';
      case 'panne': return ' Maintenance Immédiate Recommandée';
      case 'maintenance': return 'Maintenance Préventive Suggérée';
      default: return '';
    }
  }

  // --- Texte + emoji pour tableau ---
  getEtatVehicule(suivi: SuiviDTO): string {
    const etat = this.getEtatSimple(suivi);
    switch (etat) {
      case 'accident': return '🚨 Accidenté';
      case 'panne': return '❌ Risque élevé / maintenance immédiate';
      case 'maintenance': return '⚠️ Risque de panne / maintenance préventive';
      default: return '✅ Pas de risque';
    }
  }

  // --- Liste filtrée ---
  get suivisFiltres(): SuiviDTO[] {
    if (!this.etatFiltre) return this.suivis;
    return this.suivis.filter(s => this.getEtatSimple(s) === this.etatFiltre);
  }

  // --- Compte par état ---
  countEtat(etat: string): number {
    return this.suivis.filter(s => this.getEtatSimple(s) === etat).length;
  }

  // --- Sélection filtre ---
  selectionnerEtat(etat: string) {
    this.etatFiltre = this.etatFiltre === etat ? '' : etat;
  }

  // --- Classe CSS tableau selon état ---
  getClasseLigne(suivi: SuiviDTO): string {
    const etat = this.getEtatSimple(suivi);
    switch (etat) {
      case 'bon': return 'table-success';
      case 'maintenance': return 'table-warning';
      case 'panne': return 'table-danger';
      default: return '';
    }
  }
  @Output() demandeMaintenance = new EventEmitter<SuiviDTO>();

ouvrirMaintenance(v: SuiviDTO) {
  // naviguer vers le composant maintenance avec le paramètre vehiculeId
  this.router.navigate(['/admin/maintenance'], { queryParams: { vehiculeId: v.idVehicule, immatriculation: v.immatriculation } });
}
getSuivisDisponibles(): SuiviDTO[] {
  return this.suivis.filter(s => {
    return  s.etatVehicule !== 'EN_PANNE' && s.etatVehicule !== 'ACCIDENTE';
  });
}

}
