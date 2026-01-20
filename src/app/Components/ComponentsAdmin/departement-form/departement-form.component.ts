/* import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Departement } from '../../models/departement';
import { DepartementService } from '../../Services/departement.service';
import { EntrepriseService } from '../../Services/entreprise.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-departement-form',
  templateUrl: './departement-form.component.html',
  imports: [CommonModule],
  standalone: true,
})
export class DepartementFormComponent implements OnInit {
  departement: Departement = {
    nom: '',
    responsable: '',
    entreprise: { idEntreprise: 0 }
  };
  entreprises: any[] = [];
  isEditMode = false;

  constructor(
    private departementService: DepartementService,
    private entrepriseService: EntrepriseService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.entrepriseService.getEntreprises().subscribe(data => this.entreprises = data);

    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.isEditMode = true;
      this.departementService.getDepartementById(+id).subscribe(dep => {
        this.departement = dep;
      });
    }
  }

  onSubmit(): void {
    if (this.isEditMode) {
      this.departementService.updateDepartement(this.departement).subscribe(() => {
        this.router.navigate(['/departements']);
      });
    } else {
      this.departementService.createDepartement(this.departement).subscribe(() => {
        this.router.navigate(['/departements']);
      });
    }
  }

  annuler(): void {
    this.router.navigate(['/departements']);
  }
}
 */