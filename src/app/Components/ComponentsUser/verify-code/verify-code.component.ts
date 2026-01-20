import { Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule, FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { CommonModule } from '@angular/common';

@Component({
  standalone: true,
  selector: 'app-verify-code',
  templateUrl: './verify-code.component.html',
  styleUrls: ['./verify-code.component.css'],
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
})
export class VerifyCodeComponent {
  email = '';
  success = '';
  error = '';
  form: FormGroup;

  constructor(
    private fb: FormBuilder,
    private http: HttpClient,
    private router: Router,
    private route: ActivatedRoute
  ) {
    this.form = this.fb.group({
      code: ['', Validators.required]
    });
  }

  ngOnInit() {
    this.email = this.route.snapshot.queryParamMap.get('email') || '';
  }

  onSubmit() {
    if (this.form.invalid) return;

    const payload = {
      email: this.email,
      code: this.form.value.code
    };

    this.http.post('http://localhost:8090/api/auth/verify', payload)
      .subscribe({
        next: () => {
          this.success = '✅ Compte vérifié avec succès.';
          setTimeout(() => this.router.navigate(['/connexion']), 2000);
        },
        error: err => {
          this.error = err.error.message || '❌ Code incorrect ou expiré.';
        }
      });
  }
}
