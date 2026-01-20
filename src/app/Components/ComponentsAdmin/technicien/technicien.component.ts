import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Technicien } from '../../../models/technicien';
import { TechnicienService } from '../../../Services/techniciens.service';
import { ToastrService } from 'ngx-toastr';
import { CarburantIAService } from '../../../Services/carburant-ia.service';

@Component({
  selector: 'app-technicien',
  templateUrl: './technicien.component.html',
  styleUrls: ['./technicien.component.css'],
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
})
export class TechnicienComponent implements OnInit {

  technicienForm!: FormGroup;
  techniciens: Technicien[] = [];
  techniciensFiltres: Technicien[] = [];
  showAddForm = false;
  isEditMode = false;
  currentTechnicienId: number | null = null;
  searchTerm = '';
  

  constructor(
    private fb: FormBuilder,
    private technicienService: TechnicienService,
    private toastr: ToastrService,
    private iaService: CarburantIAService
  ) {}

  ngOnInit(): void {
    this.initForm();
    this.getTechniciens();
  }

  // Initialisation du formulaire
  initForm(): void {
    this.technicienForm = this.fb.group({
      nom: ['', Validators.required],
      specialite: ['', Validators.required],
      emailTech: ['', [Validators.required, Validators.email]],
      telephoneTech: ['', Validators.required],
      actif: [true]
    });
  }

  // Afficher / cacher le formulaire
  toggleAddForm(): void {
    this.showAddForm = !this.showAddForm;
    if (!this.showAddForm) this.resetForm();
  }

  // Charger tous les techniciens
  getTechniciens(): void {
    this.technicienService.getAllTechniciens().subscribe({
      next: (data) => {
        this.techniciens = data;
        this.techniciensFiltres = data;
      },
      error: () => {
        this.toastr.error('Erreur lors du chargement des techniciens', 'Erreur');
      }
    });
  }

  // Soumettre le formulaire (ajout ou mise à jour)
  onSubmit(): void {
    if (this.technicienForm.invalid) return;

    const technicien: Technicien = this.technicienForm.value;

    if (this.isEditMode && this.currentTechnicienId !== null) {
      this.technicienService.updateTechnicien(this.currentTechnicienId, technicien).subscribe({
        next: () => {
          this.getTechniciens();
          this.resetForm();
          this.toastr.success('Technicien mis à jour avec succès', 'Succès');
        },
        error: () => {
          this.toastr.error("Erreur lors de la mise à jour du technicien", "Erreur");
        }
      });
    } else {
      this.technicienService.addTechnicien(technicien).subscribe({
        next: () => {
          this.getTechniciens();
          this.resetForm();
          this.toastr.success('Technicien ajouté avec succès', 'Succès');
        },
        error: () => {
          this.toastr.error("Erreur lors de l'ajout du technicien", "Erreur");
        }
      });
    }
  }

  // Préparer l'édition
  editTechnicien(tech: Technicien): void {
    this.technicienForm.patchValue(tech);
    this.currentTechnicienId = tech.idTechnicien;
    this.isEditMode = true;
    this.showAddForm = true;
  }

  // Supprimer un technicien
  deleteTechnicien(idTechnicien: number): void {
    if (confirm('Voulez-vous vraiment supprimer ce technicien ?')) {
      this.technicienService.deleteTechnicien(idTechnicien).subscribe({
        next: () => {
          this.getTechniciens();
          this.toastr.success('Technicien supprimé avec succès', 'Succès');
        },
        error: () => {
          this.toastr.error("Erreur lors de la suppression du technicien", "Erreur");
        }
      });
    }
  }

  // Activer / désactiver un technicien
  toggleActivation(tech: Technicien): void {
    const updatedTech = { ...tech, actif: !tech.actif };
    this.technicienService.updateTechnicien(tech.idTechnicien!, updatedTech).subscribe({
      next: () => {
        this.getTechniciens();
        const status = updatedTech.actif ? 'activé' : 'désactivé';
        this.toastr.success(`Technicien ${status} avec succès`, 'Succès');
      },
      error: () => {
        this.toastr.error("Erreur lors du changement d'état du technicien", "Erreur");
      }
    });
  }

  // Réinitialiser le formulaire
  resetForm(): void {
    this.technicienForm.reset({ actif: true });
    this.showAddForm = false;
    this.isEditMode = false;
    this.currentTechnicienId = null;
  }

  // Filtrer les techniciens
  filterTechniciens(): void {
    const term = this.searchTerm.toLowerCase();
    this.techniciensFiltres = this.techniciens.filter(t =>
      t.nom.toLowerCase().includes(term)
    );
  }
  
}
