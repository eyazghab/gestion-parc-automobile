import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { Departement } from '../../../models/departement';
import { DepartementService } from '../../../Services/departement.service';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-departements',
  templateUrl: './departements.component.html',
  styleUrls: ['./departements.component.css'],
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
})
export class DepartementsComponent implements OnInit {

  departements: Departement[] = [];
  nomRecherche: string = '';
  departementForm!: FormGroup;
  showAddForm = false;
  editingId: number | null = null;
  departementErrors: { [id: number]: string } = {};

  constructor(
    private fb: FormBuilder,
    private departementService: DepartementService,
    private toastr: ToastrService
  ) {}

  ngOnInit(): void {
    this.initForm();
    this.loadDepartements();
  }

  // Initialisation du formulaire
  initForm() {
    this.departementForm = this.fb.group({
      nom: ['', Validators.required],
      responsable: ['', Validators.required],
    });
  }

  // Chargement des départements
  loadDepartements() {
    this.departementService.getDepartements().subscribe({
      next: (data) => {
        this.departements = data;
      },
      error: (err) => {
        this.toastr.error('Erreur lors du chargement des départements', 'Erreur');
      }
    });
  }

  // Fonction de filtrage réactif
  filteredDepartements(): Departement[] {
    if (!this.nomRecherche) return this.departements;
    const term = this.nomRecherche.toLowerCase();
    return this.departements.filter(d =>
      d.nom.toLowerCase().includes(term) ||
      d.responsable.toLowerCase().includes(term)
    );
  }

  // Réinitialiser la recherche
  reinitialiser(): void {
    this.nomRecherche = '';
  }

  // Affichage / masquage du formulaire
  toggleAddForm(): void {
    this.showAddForm = !this.showAddForm;
    if (!this.showAddForm) this.resetForm();
  }

  // Reset du formulaire
  resetForm(): void {
    this.departementForm.reset();
    this.editingId = null;
  }

  // Soumission du formulaire (ajout ou modification)
  onSubmit(): void {
    if (this.editingId) {
      const depToUpdate: Departement = { id: this.editingId, ...this.departementForm.value };
      this.departementService.updateDepartement(depToUpdate).subscribe({
        next: () => {
          this.toastr.success('Département mis à jour avec succès', 'Succès');
          this.resetForm();
          this.loadDepartements();
        },
        error: (err) => {
          const msg = err.error?.message || 'Erreur lors de la mise à jour';
          this.toastr.error(msg, 'Erreur');
        }
      });
    } else {
      this.departementService.createDepartement(this.departementForm.value).subscribe({
        next: () => {
          this.toastr.success('Département créé avec succès', 'Succès');
          this.resetForm();
          this.loadDepartements();
        },
        error: (err) => {
          const msg = err.error?.message || 'Erreur lors de la création';
          this.toastr.error(msg, 'Erreur');
        }
      });
    }
  }

  // Préparation de l'édition
  editDepartement(id: number | undefined): void {
    if (id === undefined) return;
    const dep = this.departements.find(d => d.id === id);
    if (!dep) return;

    this.departementForm.patchValue({
      nom: dep.nom,
      responsable: dep.responsable
    });

    this.editingId = id;
    this.showAddForm = true;
  }

// Suppression d'un département avec Toastr
deleteDepartement(id: number | undefined): void {
  if (id === undefined) return;

  this.departementService.deleteDepartement(id).subscribe({
    next: () => {
      this.departements = this.departements.filter(d => d.id !== id);
      delete this.departementErrors[id];
      // Succès avec Toastr
      this.toastr.success('Département supprimé avec succès', 'Succès');
    },
    error: (err) => {
      const msg = err.error?.message || 'Erreur lors de la suppression';
      this.departementErrors[id] = msg;
      // Erreur avec Toastr
      this.toastr.error(msg, 'Erreur');
    }
  });
}
// Retourne les départements regroupés par nom
getGroupedDepartements(): { nom: string, departements: Departement[] }[] {
  const map: { [nom: string]: Departement[] } = {};

  // Filtrer d'abord selon la recherche
  const filtered = this.filteredDepartements();

  // Grouper par nom
  filtered.forEach(d => {
    if (!map[d.nom]) map[d.nom] = [];
    map[d.nom].push(d);
  });

  // Transformer en tableau pour itération dans le template
  return Object.keys(map).map(nom => ({
    nom,
    departements: map[nom]
  }));
}


}
