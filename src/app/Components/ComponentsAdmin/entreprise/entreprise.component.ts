import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Entreprise } from '../../../models/entreprise';
import { EntrepriseService } from '../../../Services/entreprise.service';


@Component({
  selector: 'app-entreprises',
  templateUrl: './entreprise.component.html',
  imports: [CommonModule],
  standalone: true,
})
export class EntreprisesComponent implements OnInit {
  entreprises: Entreprise[] = [];

  constructor(private entrepriseService: EntrepriseService) {}

  ngOnInit(): void {
    this.entrepriseService.getEntreprises().subscribe(data => {
      this.entreprises = data;
    });
  }
}
