import { Component, OnInit } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  Validators,
  ReactiveFormsModule,
  FormsModule,
} from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { ToastrService } from 'ngx-toastr';
import { Departement } from '../../../models/departement';
import { DepartementService } from '../../../Services/departement.service';

@Component({
  selector: 'app-inscription',
  standalone: true,
  templateUrl: './inscription.component.html',
  styleUrls: ['./inscription.component.css'],
  imports: [CommonModule, ReactiveFormsModule, FormsModule],
})
export class InscriptionComponent implements OnInit {
  form!: FormGroup;
  codeForm!: FormGroup;
  showVerifyCode = false;
  email = '';
  departements: Departement[] = [];
showCodeCnam: boolean = false;


  constructor(
    private fb: FormBuilder,
    private http: HttpClient,
    private router: Router,
    private departementService: DepartementService,
    private toastr: ToastrService
  ) {}

  ngOnInit(): void {
    this.initForms();
    this.loadDepartements();
  }

  initForms(): void {
    this.form = this.fb.group({
      nom: ['', Validators.required],
      prenom: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      motDePasse: ['', Validators.required],
      telephone: ['', Validators.required],
 cin: ['', [Validators.required,Validators.pattern(/^\d{8}$/)]],   // exactement 8 chiffres
     dateEmbauche: ['', Validators.required],
      departementId: [null, Validators.required],
codeCnam: ['', [Validators.required, Validators.pattern(/^[A-Za-z0-9]{10}$/)]]
    });

    this.codeForm = this.fb.group({
      code: ['', Validators.required],
    });
  }

  loadDepartements(): void {
    this.departementService.getDepartements().subscribe({
      next: (data) => {
        this.departements = data.filter(
          (dep, index, self) =>
            index === self.findIndex((d) => d.nom === dep.nom)
        );
      },
      error: (err) => {
        this.toastr.error('Erreur de chargement des départements ❌');
        console.error('Erreur départements :', err);
      },
    });
  }

  onSubmit(): void {
    if (this.form.invalid) return;

    const utilisateur = {
      nom: this.form.value.nom,
      prenom: this.form.value.prenom,
      email: this.form.value.email,
      motDePasse: this.form.value.motDePasse,
      telephone: this.form.value.telephone,
      cin: this.form.value.cin,
      dateEmbauche: this.form.value.dateEmbauche,
       codeCnam: this.form.value.codeCnam,
      departement: {
        id: Number(this.form.value.departementId),
      },
    };

    this.http
      .post('http://localhost:8090/api/auth/register', utilisateur)
      .subscribe({
        next: (res: any) => {
          this.toastr.success(res.message || 'Code envoyé par email ✅');
          this.email = this.form.value.email;
          this.showVerifyCode = true;
          this.form.disable();
        },
        error: (err) => {
          const rawMessage =
            typeof err.error === 'string' ? err.error : err.error?.message;

          const message = (rawMessage || '').toLowerCase();

          if (message.includes('email')) {
            this.toastr.error('❌ Cet email est déjà utilisé');
          } else if (message.includes('cin')) {
            this.toastr.error('❌ Ce CIN est déjà utilisé');
          } else {
            this.toastr.error(rawMessage || 'Erreur d’inscription ❌');
          }

          console.error('Erreur inscription :', err);
        },
      });
  }

  verifyCode(): void {
    if (this.codeForm.invalid) {
      this.toastr.warning('⚠️ Le code est requis !');
      return;
    }

    const code = this.codeForm.get('code')?.value;

    this.http
      .post('http://localhost:8090/api/auth/verify', {
        email: this.email,
        code: code,
      })
      .subscribe({
        next: () => {
          this.toastr.success('✅ Compte vérifié avec succès');
          this.router.navigate(['/connexion']);
        },
        error: (err) => {
          console.error('Erreur vérification :', err);

          const message =
            typeof err.error === 'string'
              ? err.error
              : err.error?.message || 'Erreur de vérification ❌';

          this.toastr.error(message);
          this.codeForm.reset(); // nettoyage champ code après erreur
        },
      });
  }
}
