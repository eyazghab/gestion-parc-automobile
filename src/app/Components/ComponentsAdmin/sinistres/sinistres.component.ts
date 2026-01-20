import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { EtatSinistre, EtatSinistreLabels, Sinistre } from '../../../models/sinistre';
import { SinistreService } from '../../../Services/sinistre.service';
import { ToastrService } from 'ngx-toastr';
import { TechnicienService } from '../../../Services/techniciens.service';
import { Technicien } from '../../../models/technicien';
import { MaintenancesService } from '../../../Services/maintenances.service';

@Component({
  selector: 'app-sinistres',
  templateUrl: './sinistres.component.html',
  styleUrls: ['./sinistres.component.css'],
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule]
})
export class SinistresComponent implements OnInit {
  sinistres: (Sinistre & {
    showPrendreEnChargeForm?: boolean;
    selectedTechnicienId?: number;
    selectedCirculation?: 'INTERDITE' | 'POSSIBLE';
    selectedType?: 'INTERNE' | 'EXTERNE';
  })[] = [];

  techniciens: Technicien[] = [];

  // Maintenance form
  showAddForm = false;
  currentSinistreForMaintenance?: Sinistre;
  maintenanceForm!: FormGroup;

  // Gestion modale photos
  modalOpen = false;
  modalImage = '';
  currentPhotoIndex = 0;
  currentPhotos: string[] = [];

  public etats = Object.values(EtatSinistre);
  EtatSinistre = EtatSinistre;
  EtatSinistreLabels = EtatSinistreLabels;

  constructor(
    private sinistreService: SinistreService,
    private technicienService: TechnicienService,
    private maintenanceService: MaintenancesService,
    private toastr: ToastrService,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.chargerTechniciens();
    this.chargerSinistres();
    this.initMaintenanceForm();
  }

  // Init formulaire maintenance
  initMaintenanceForm() {
    this.maintenanceForm = this.fb.group({
      technicienId: [null, Validators.required],
      circulation: [Validators.required],
      typeMaintenance: [Validators.required],
      statut: ['Planifiée', Validators.required],
      observations: ['Maintenance suite à un sinistre'],
      coutPiece: [0],
      coutExterne: [0],
      coutTotal: [0]
    });
  }

  // 🔹 Chargement des sinistres
  chargerSinistres() {
    this.sinistreService.getAll().subscribe({
      next: (data: Sinistre[]) => {
        this.sinistres = data.map(s => ({
          ...s,
          photos: s.photos || [],
          showPrendreEnChargeForm: false,
          selectedTechnicienId: undefined,
          selectedCirculation: 'INTERDITE',
          selectedType: 'INTERNE' // type par défaut
        }));
      },
      error: (err) => {
        console.error('Erreur chargement sinistres :', err);
        this.toastr.error("Erreur lors du chargement des sinistres");
      }
    });
  }

  // 🔹 Chargement des techniciens
  chargerTechniciens() {
    this.technicienService.getAllTechniciens().subscribe({
      next: (data: Technicien[]) => this.techniciens = data,
      error: (err) => console.error('Erreur chargement techniciens :', err)
    });
  }

  // 🔹 Toggle formulaire maintenance
  toggleAddForm(sinistre?: Sinistre) {
    this.showAddForm = !this.showAddForm;
    this.currentSinistreForMaintenance = sinistre || undefined;

    if (sinistre) {
      const filtered = this.getTechniciensFiltered(sinistre);
      this.maintenanceForm.patchValue({
        technicienId: sinistre.selectedTechnicienId || filtered[0]?.idTechnicien || null,
        circulation: sinistre.selectedCirculation || 'INTERDITE',
        typeMaintenance: '',
        statut: 'EN_COURS',
        observations: 'Maintenance suite à un sinistre',
        coutPiece: 0,
        coutExterne: 0,
        coutTotal: 0
      });
    }
  }

  // 🔹 Création maintenance
  onSubmitMaintenance() {
    if (!this.currentSinistreForMaintenance) {
      this.toastr.error("Aucun sinistre sélectionné !");
      return;
    }

    if (!this.maintenanceForm.valid) {
      this.toastr.warning("Veuillez remplir tous les champs obligatoires !");
      return;
    }

    const formData = new FormData();
    formData.append('technicienId', this.maintenanceForm.value.technicienId.toString());
    formData.append('typeMaintenanceId', this.maintenanceForm.value.typeMaintenance.toString());
    formData.append('statut', 'Initialisée');
    formData.append('observations', this.maintenanceForm.value.observations);
    formData.append('coutPiece', (this.maintenanceForm.value.coutPiece || 0).toString());
    formData.append('coutExterne', (this.maintenanceForm.value.coutExterne || 0).toString());
    formData.append('coutTotal', (this.maintenanceForm.value.coutTotal || 0).toString());
    formData.append('sinistreId', this.currentSinistreForMaintenance.id.toString());
    formData.append('vehiculeId', this.currentSinistreForMaintenance.vehicule?.idVehicule.toString() || '');
    formData.append('dateMaintenance', new Date().toISOString().split('T')[0]);

    const files: FileList = this.maintenanceForm.get('files')?.value;
    if (files && files.length > 0) {
      for (let i = 0; i < files.length; i++) {
        formData.append('files', files[i]);
      }
    }

    this.maintenanceService.createFromSinistre(formData).subscribe({
      next: () => {
        this.toastr.success("Maintenance ajoutée avec succès !");
        this.showAddForm = false;
        this.maintenanceForm.reset();

        // Mettre à jour l'état du sinistre
        this.sinistreService.changeEtat(this.currentSinistreForMaintenance!.id, 'A_MAINTENIR').subscribe({
          next: updated => {
            this.currentSinistreForMaintenance!.etat = updated.etat;
            this.toastr.success("État du sinistre mis à jour : A_MAINTENIR");
          },
          error: () => this.toastr.error("Impossible de changer l'état du sinistre")
        });
      },
      error: (err) => {
        console.error(err);
        this.toastr.error("Erreur lors de l'ajout de la maintenance");
      }
    });
  }

  // 🔹 Gestion photos
  openModal(photoUrl: string, photos: string[] = []) {
    this.modalImage = photoUrl;
    this.modalOpen = true;
    this.currentPhotos = photos;
    this.currentPhotoIndex = photos.indexOf(photoUrl);
  }

  closeModal() {
    this.modalOpen = false;
    this.modalImage = '';
    this.currentPhotos = [];
    this.currentPhotoIndex = 0;
  }

  nextPhoto() {
    if (this.currentPhotos.length > 1) {
      this.currentPhotoIndex = (this.currentPhotoIndex + 1) % this.currentPhotos.length;
      this.modalImage = this.currentPhotos[this.currentPhotoIndex];
    }
  }

  prevPhoto() {
    if (this.currentPhotos.length > 1) {
      this.currentPhotoIndex =
        (this.currentPhotoIndex - 1 + this.currentPhotos.length) % this.currentPhotos.length;
      this.modalImage = this.currentPhotos[this.currentPhotoIndex];
    }
  }

  // 🚦 Gestion états
  changerEtat(sinistre: Sinistre, nouvelEtat: EtatSinistre) {
    if (!Object.values(EtatSinistre).includes(nouvelEtat)) {
      this.toastr.error("État invalide");
      return;
    }

    if (nouvelEtat === EtatSinistre.A_MAINTENIR) {
      this.toggleAddForm(sinistre);
      return;
    }

    if (nouvelEtat === EtatSinistre.PAS_DE_TRAITEMENT_NECESSAIRE) {
      this.sinistreService.changeEtat(sinistre.id, nouvelEtat).subscribe({
        next: (updated: Sinistre) => {
          sinistre.etat = updated.etat;
          this.toastr.success("Pas de traitement à faire, véhicule disponible !");
          (sinistre as any).showPrendreEnChargeForm = false;
        },
        error: () => this.toastr.error("Impossible de changer l'état du sinistre")
      });
      return;
    }

    this.sinistreService.changeEtat(sinistre.id, nouvelEtat).subscribe({
      next: (updated: Sinistre) => {
        sinistre.etat = updated.etat;
        (sinistre as any).showPrendreEnChargeForm = false;
        this.toastr.success(`État mis à jour : ${EtatSinistreLabels[updated.etat as EtatSinistre]}`);
      },
      error: () => this.toastr.error("Impossible de changer l'état du sinistre")
    });
  }

  // 🔹 Prise en charge
  prendreEnChargeSinistre(sinistre: Sinistre & { selectedTechnicienId?: number; selectedCirculation?: string }) {
    if (!sinistre.selectedTechnicienId || !sinistre.selectedCirculation) {
      this.toastr.warning('Veuillez sélectionner un technicien et la circulation');
      return;
    }

    this.sinistreService.prendreEnCharge(
      sinistre.id,
      sinistre.selectedTechnicienId,
      sinistre.selectedCirculation
    ).subscribe({
      next: (updated: Sinistre) => {
        (sinistre as any).showPrendreEnChargeForm = false;
        sinistre.etat = updated.etat;
        this.toastr.success('Sinistre pris en charge !');
      },
      error: () => this.toastr.error('Erreur lors de la prise en charge')
    });
  }

  // 🔹 Techniciens filtrés selon le type d’un sinistre
getTechniciensFiltered(sinistre: Sinistre & { selectedType?: 'INTERNE' | 'EXTERNE' }): Technicien[] {
  const type = sinistre.selectedType || 'INTERNE';
  return this.techniciens.filter(t => t.type === type);
}

// 🔹 Mettre à jour le formControl quand on change le technicien
onTechnicienChange(sinistre: any) {
  if (sinistre.selectedTechnicienId) {
    this.maintenanceForm.patchValue({ technicienId: sinistre.selectedTechnicienId });
  }
}

// 🔹 Mettre à jour le technicien automatiquement quand on change le type
onTypeChange(sinistre: any) {
  const filtered = this.getTechniciensFiltered(sinistre);
  if (!filtered.find(t => t.idTechnicien === sinistre.selectedTechnicienId)) {
    sinistre.selectedTechnicienId = filtered.length > 0 ? filtered[0].idTechnicien : undefined;
  }
  this.onTechnicienChange(sinistre);
}

  // 🔹 États possibles
  getEtatsPossibles(sinistre: Sinistre): EtatSinistre[] {
    switch (sinistre.etat) {
      case EtatSinistre.DECLARE:
        return [EtatSinistre.A_MAINTENIR, EtatSinistre.PAS_DE_TRAITEMENT_NECESSAIRE];
      case EtatSinistre.A_MAINTENIR:
        return [EtatSinistre.PAS_DE_TRAITEMENT_NECESSAIRE];
      default:
        return [];
    }
  }
}
