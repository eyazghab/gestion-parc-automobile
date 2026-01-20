import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { PlaceService } from '../../../Services/place.service';
import { ParkingService } from '../../../Services/parkings.service';
import { VehiculeService } from '../../../Services/vehicule.service';


@Component({
  selector: 'app-edit-place',
  templateUrl: './edit-place.component.html',
  styleUrls: ['./edit-place.component.css'],
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule]
})
export class EditPlaceComponent implements OnInit {
  placeForm!: FormGroup;
  parkings: any[] = [];
  vehicules: any[] = [];
  placeId!: number;

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private placeService: PlaceService,
    private parkingService: ParkingService,
    private vehiculeService: VehiculeService
  ) {}

  ngOnInit(): void {
    this.placeForm = this.fb.group({
      nom: ['', Validators.required],
      numeroPlace: [0, Validators.required],
      estOccupee: [false],
      parking: [null, Validators.required],
      vehicule: [null]
    });

    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.placeId = +id;
      this.placeService.getPlaceById(this.placeId).subscribe({
        next: (data) => {
          this.placeForm.patchValue(data);
        },
        error: (err) => {
          console.error('Erreur lors du chargement de la place :', err);
        }
      });
    }

    this.parkingService.getParkings().subscribe({
      next: (data) => (this.parkings = data),
      error: (err) => console.error('Erreur lors du chargement des parkings :', err)
    });

    this.vehiculeService.getVehicules().subscribe({
      next: (data) => (this.vehicules = data),
      error: (err) => console.error('Erreur lors du chargement des véhicules :', err)
    });
  }

  onSubmit(): void {
    if (this.placeForm.valid) {
      const updatedPlace = this.placeForm.value;
      updatedPlace.idPlaceParking = this.placeId;

      this.placeService.updatePlace(this.placeId, updatedPlace).subscribe({
        next: () => {
          alert('Place mise à jour avec succès');
          this.router.navigate(['/places']);
        },
        error: (err) => {
          console.error('Erreur lors de la mise à jour :', err);
        }
      });
    } else {
      console.error('Formulaire invalide');
    }
  }
}
