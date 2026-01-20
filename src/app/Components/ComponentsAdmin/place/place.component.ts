import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule, FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { Router } from '@angular/router';
import { place } from '../../../models/place';
import { PlaceService } from '../../../Services/place.service';
import { Parking } from '../../../models/parking';
import { ParkingService } from '../../../Services/parkings.service'; // ✅ importer le service parking
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-place',
  standalone: true,
  imports: [HttpClientModule, FormsModule, ReactiveFormsModule, CommonModule],
  templateUrl: './place.component.html',
  styleUrls: ['./place.component.css']
})
export class PlaceComponent implements OnInit {
  places: place[] = [];
  parkings: Parking[] = []; // ✅ liste des parkings
  searchTerm = '';
  message = '';
  showAddForm = false;
  placeForm!: FormGroup;

  constructor(
    private placeService: PlaceService,
    private parkingService: ParkingService, // ✅ injection du service parking
    private router: Router,
    private fb: FormBuilder,
     private toastr: ToastrService
  ) {}

  ngOnInit(): void {
    this.loadPlaces();
    this.loadParkings(); // ✅ on charge les parkings dès l'initialisation
    this.initForm();
  }

  initForm() {
    this.placeForm = this.fb.group({
      nom: ['', Validators.required],
      numeroPlace: ['', [Validators.required, Validators.pattern('^[0-9]*$')]],
      estOccupee: [false],
      parking: [null, Validators.required] // ✅ champ obligatoire
      
    });
  }

  loadParkings() {
    this.parkingService.getParkings().subscribe({
      next: (data) => {
        this.parkings = data.filter(p => p.estDisponible === true); // ✅ seulement les parkings dispo
      },
      error: (err) => console.error('Erreur chargement parkings', err)
    });
  }

  toggleAddForm() {
    this.showAddForm = !this.showAddForm;
    if (!this.showAddForm) {
      this.placeForm.reset();
    }
  }

onAddSubmit() {
  if (this.placeForm.invalid) return;

  const parkingId = this.placeForm.value.parking;
  const selectedParking = this.parkings.find(p => p.id === +parkingId);

  if (!selectedParking) {
    this.toastr.error('Parking invalide', 'Erreur');
    return;
  }

  const newPlace: place = {
    ...this.placeForm.value,
    parking: { idParking: selectedParking.id } as any,
    vehicule: undefined
  };

  this.placeService.createPlace(newPlace).subscribe({
    next: () => {
      this.toastr.success('Place ajoutée avec succès', 'Succès');
      this.loadPlaces();
      this.toggleAddForm();
    },
    error: (err: any) => {
      this.toastr.error('Erreur lors de l\'ajout de la place', 'Erreur');
      console.error(err);
    }
  });
}



  loadPlaces() {
    this.placeService.getAllPlaces().subscribe({
      next: (data) => this.places = data,
      error: (err) => console.error('Erreur chargement places', err)
    });
  }

  filteredPlaces(): place[] {
    const term = this.searchTerm.toLowerCase();
    return this.places.filter(p =>
      p.nom?.toLowerCase().includes(term) ||
      p.numeroPlace?.toString().includes(term) ||
      p.parking?.nomParking?.toLowerCase().includes(term) ||
      p.vehicule?.immatriculation?.toLowerCase().includes(term)
    );
  }

  modifier(place: place) {
    if (!place.idPlaceParking) {
      console.error('ID de la place manquant pour la navigation');
      return;
    }
    this.router.navigate(['/places/edit', place.idPlaceParking]);
  }

  supprimer(place: place) {
    if (confirm(`Supprimer la place "${place.nom}" ?`)) {
      this.placeService.deletePlace(place.idPlaceParking!).subscribe({
        next: () => this.loadPlaces(),
        error: (err) => console.error('Erreur suppression', err)
      });
    }
  }

  basculerEtat(place: place) {
    if (!place.idPlaceParking) return;
    place.estOccupee = !place.estOccupee;
    this.placeService.updatePlace(place.idPlaceParking, place).subscribe({
      next: () => this.loadPlaces(),
      error: (err) => console.error('Erreur lors de la mise à jour de la place :', err)
    });
  }
}
