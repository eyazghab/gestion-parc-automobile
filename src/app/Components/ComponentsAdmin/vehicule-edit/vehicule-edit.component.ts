import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { VehiculeService } from '../../../Services/vehicule.service';

@Component({
  selector: 'app-vehicule-edit',
  templateUrl: './vehicule-edit.component.html',
  styleUrls: ['./vehicule-edit.component.css'],
    imports: [CommonModule, FormsModule, ReactiveFormsModule],

})
export class VehiculeEditComponent implements OnInit {
  vehiculeForm!: FormGroup;
  id!: number;
  isEditMode = false;
  vehicule: any = {};

  constructor(
    private fb: FormBuilder,
    private vehiculeService: VehiculeService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.vehiculeForm = this.fb.group({
      immatriculation: ['', Validators.required],
      numeroChassis: ['', Validators.required],
      dateCircu: ['', Validators.required],
      date_acquisition: ['', Validators.required],
      kilometrage_actuel: ['', Validators.required],
      anneeModel: [null, Validators.required],
      typeCarburant: ['', Validators.required],
      etat: ['En attente', Validators.required]
    });

    const paramId = this.route.snapshot.paramMap.get('id');
    if (paramId) {
      this.id = +paramId;
      this.isEditMode = true;
      this.vehiculeService.getVehiculeById(this.id).subscribe({
        next: (data) => {
          this.vehicule = data;
          this.vehiculeForm.patchValue(data);
        },
        error: (err) => {
          console.error('Erreur de chargement du véhicule', err);
        }
      });
    }
  }

  onSubmit(): void {
    if (this.vehiculeForm.invalid) {
      return;
    }

    if (this.isEditMode) {
      this.vehiculeService.updateVehicule(this.id, this.vehiculeForm.value).subscribe(() => {
        this.router.navigate(['admin/vehicules']);
      });
    } else {
      // Optionnel: logique pour ajouter un véhicule
    }
  }
}
