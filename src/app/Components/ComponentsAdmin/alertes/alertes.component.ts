import { Component, OnInit } from '@angular/core';
import { ToastrService } from 'ngx-toastr';
import { SuiviService } from '../../../Services/suivi.service';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';

@Component({
  selector: 'app-alertes',
  templateUrl: './alertes.component.html',
  styleUrls: ['./alertes.component.css'],
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule, RouterModule],
})
export class AlertesComponent implements OnInit {
  suivis: any[] = [];

  constructor(private suiviService: SuiviService,
              private toastr: ToastrService,
              private router: Router) {}

  ngOnInit(): void {
    this.chargerSuivis();
  }

  chargerSuivis(): void {
    this.suiviService.getAllSuivis().subscribe({
      next: (data) => this.suivis = data,
      error: (err) => this.toastr.error('Impossible de charger les suivis')
    });
  }

  voirDetails(vehiculeId: number): void {
    this.router.navigate(['/admin/suivi-vehicule', vehiculeId]);

  }
}