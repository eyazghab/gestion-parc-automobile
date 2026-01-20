import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { VehiculeService } from '../../../Services/vehicule.service';
import { AuthService } from '../../../Services/auth.service';
import { HttpClient } from '@angular/common/http';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-declaration-sinistre',
  templateUrl: './declaration-sinistre.component.html',
  styleUrls: ['./declaration-sinistre.component.css'],
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  
})
export class DeclarationSinistreComponent implements OnInit {
  sinistreForm!: FormGroup;
  vehicules: any[] = [];
  selectedFiles: File[] = [];
  typesIncident = ['ACCIDENT', 'PANNE', 'PREVENTIVE'];

  constructor(
    private vehiculeService: VehiculeService,
    private fb: FormBuilder,
    private authService: AuthService,
    private http: HttpClient,
    private router: Router,
    private toastr: ToastrService
  ) {}

  ngOnInit(): void {
    this.sinistreForm = this.fb.group({
      dateSinistre: ['', Validators.required],
      heureSinistre: ['', Validators.required],
      lieuSinistre: ['', Validators.required],
      degats: ['', Validators.required],
      vehiculeId: [null, Validators.required],
      typeIncident: ['', Validators.required],
    });

    this.loadVehicules();
  }

  loadVehicules(): void {
    this.vehiculeService.getVehiculesDisponiblesOuReserves().subscribe({
      next: data => this.vehicules = data,
      error: () => this.toastr.error("Erreur lors du chargement des véhicules")
    });
  }

  onFileChange(event: any): void {
    this.selectedFiles = Array.from(event.target.files);
  }

 onSubmit(): void {
  if (this.sinistreForm.invalid) {
    this.toastr.warning('⚠️ Veuillez remplir tous les champs obligatoires');
    return;
  }

  const formValue = this.sinistreForm.value;
  const connectedUser = this.authService.getConnectedUser();
  if (!connectedUser) {
    this.toastr.error("Utilisateur non connecté !");
    return;
  }

  // --- Validation de l'heure ---
  let heure = formValue.heureSinistre;
  if (!heure) {
    this.toastr.warning('Veuillez sélectionner l’heure du sinistre');
    return;
  }


  // --- Validation de la date ---
  const date = formValue.dateSinistre;
  if (!date) {
    this.toastr.warning('Veuillez sélectionner la date du sinistre');
    return;
  }

  // --- Validation du type d’incident ---
  const typeIncident = formValue.typeIncident;
  if (!typeIncident) {
    this.toastr.warning('Veuillez sélectionner le type de sinistre');
    return;
  }

  // --- Préparer FormData pour POST ---
  const formData = new FormData();
  formData.append('vehiculeId', formValue.vehiculeId.toString());
  formData.append('utilisateurId', connectedUser.idUtilisateur.toString());
  formData.append('dateSinistre', date); // format YYYY-MM-DD
  formData.append('heureSinistre', heure); // format HH:mm:ss
  formData.append('lieuSinistre', formValue.lieuSinistre);
  formData.append('degats', formValue.degats);
  formData.append('typeIncident', typeIncident); // valeur enum

  // --- Ajouter les fichiers ---
  if (this.selectedFiles && this.selectedFiles.length > 0) {
    this.selectedFiles.forEach(file => formData.append('files', file, file.name));
  }
console.log('Payload envoyé au backend :', this.sinistreForm.value);

  // --- Envoi au backend ---
  this.http.post('http://localhost:8090/api/sinistres/add', formData).subscribe({
    next: () => {
      this.toastr.success('✅ Sinistre déclaré avec succès !');
      this.router.navigate(['/home']);
    },
    error: err => {
      console.error('Erreur backend:', err);
      this.toastr.error('❌ Erreur lors de la déclaration du sinistre');
    }
  });
}

}
