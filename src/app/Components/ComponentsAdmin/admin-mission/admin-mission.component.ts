import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { OrdreMissionDto } from '../../../models/ordreMissionDto';
import { Vehicule } from '../../../models/vehicule';
import { OrdreMissionService } from '../../../Services/ordre-mission.service';
import { VehiculeService } from '../../../Services/vehicule.service';
import { AffectationIAService } from '../../../Services/affectation-ia.service';

@Component({
  selector: 'app-admin-mission',
  templateUrl: './admin-mission.component.html',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule, RouterModule],
})
export class AdminMissionComponent implements OnInit {

  missions: OrdreMissionDto[] = [];
  filteredMissions: OrdreMissionDto[] = [];
  vehiculeFilter: string = '';
  historiqueMissions: OrdreMissionDto[] = [];

  constructor(
    private ordreMissionService: OrdreMissionService,
    private toastr: ToastrService,
    private vehiculeService:VehiculeService,
    private iaService: AffectationIAService
  ) {}

  ngOnInit(): void {
    this.getAllMissions();


  }

  // Charger toutes les missions en cours
// Charger toutes les missions en cours
getMissions() {
  this.ordreMissionService.getMissionsEnCours().subscribe({
    next: data => {
      // Mapper correctement les objets du backend vers OrdreMissionDto
      this.missions = data.map(mission => ({
        id: mission.id!,
        destination: mission.destination,
        motif: mission.motif,
        dateDepart: mission.dateDepart,
        dateRetour: mission.dateRetour,
        etat: mission.etat, // obligatoire pour OrdreMissionDto
        utilisateurNom: mission.utilisateurNom || '',
        vehiculeImmatriculation: mission.vehiculeImmatriculation || mission.vehicule?.immatriculation || '', // obligatoire
        vehiculesDisponibles: [], // optionnel
        selectedVehiculeId: undefined, // optionnel
        vehicule: mission.vehicule // optionnel
      }));

      this.filteredMissions = [...this.missions];

      // Charger véhicules disponibles pour chaque mission EN_ATTENTE
      this.missions.forEach(mission => {
        if (mission.etat === 'EN_ATTENTE') {
          this.loadVehiculesDisponibles(mission);
        }
      });
    },
    error: err => {
      console.error("Erreur lors du chargement des missions :", err);
      this.toastr.error("Erreur lors du chargement des missions !");
    }
  });
}





  // Filtrer missions par immatriculation
  applyFilter() {
    const filter = this.vehiculeFilter.trim().toLowerCase();
    this.filteredMissions = this.missions.filter(m =>
      (m.vehiculeImmatriculation || '').toLowerCase().includes(filter)
    );
  }

  // Changer état d'une mission
  changerEtat(id: number, nouvelEtat: string) {
    this.ordreMissionService.updateEtatMission(id, nouvelEtat).subscribe({
      next: response => {
        this.toastr.success(response);
        this.getMissions();
      },
      error: err => {
        console.error(err);
        this.toastr.error("Erreur lors du changement d'état");
      }
    });
  }

  // Charger véhicules disponibles pour une mission
// Charger véhicules disponibles pour une mission EN_ATTENTE
loadVehiculesDisponibles(mission: OrdreMissionDto) {
  // Récupérer tous les véhicules libres depuis le backend
  this.vehiculeService.getVehiculesLibres().subscribe({
    next: (allVehicules) => {
      // Filtrer seulement ceux qui sont vraiment disponibles pour la période de la mission
      const depart = new Date(mission.dateDepart);
      const retour = new Date(mission.dateRetour);

      mission.vehiculesDisponibles = allVehicules.filter(v => 
        v.etat === 'DISPONIBLE' && // Véhicule libre
        !this.missions.some(m => {
          // Exclure les missions déjà assignées à ce véhicule
          if (!m.vehicule) return false;
          if (m.vehicule.idVehicule !== v.idVehicule) return false;

          const missionStart = new Date(m.dateDepart);
          const missionEnd = new Date(m.dateRetour);

          // Retourne vrai si les dates se chevauchent
          return depart < missionEnd && retour > missionStart;
        })
      );

      if (mission.vehiculesDisponibles.length === 0) {
        this.toastr.warning(`Aucun véhicule disponible pour la mission à ${mission.destination}.`);
      }
    },
    error: (err) => {
      console.error("Erreur lors du chargement des véhicules libres", err);
      mission.vehiculesDisponibles = [];
      this.toastr.error("Erreur lors du chargement des véhicules libres.");
    }
  });
}



  // Assignation du véhicule et acceptation de la mission
assignerVehiculeEtAccepter(mission: OrdreMissionDto) {
  if (!mission.selectedVehiculeId) {
    this.toastr.warning("Veuillez sélectionner un véhicule !");
    return;
  }

  this.ordreMissionService.assignVehicule(mission.id, mission.selectedVehiculeId).subscribe({
    next: (updatedMission: OrdreMissionDto) => {
      // Mettre à jour la mission dans le tableau local
      const index = this.missions.findIndex(m => m.id === mission.id);
      if (index !== -1) {
        this.missions[index] = { ...updatedMission };
      }

      // Si tu utilises filteredMissions pour le filtre affiché
      const fIndex = this.filteredMissions.findIndex(m => m.id === mission.id);
      if (fIndex !== -1) {
        this.filteredMissions[fIndex] = { ...updatedMission };
      }

      this.toastr.success('Mission acceptée et véhicule assigné !');
    },
    error: (err) => {
      console.error("Erreur lors de l'assignation du véhicule", err);
      this.toastr.error(err.error?.message || "Impossible d'assigner le véhicule !");
    }
  });
}



getAllMissions() {
  this.ordreMissionService.getAllMissions().subscribe({
    next: (data) => {
      // Filtrer les missions par état
      this.missions = data.filter(m =>
        m.etat === 'EN_ATTENTE' || m.etat === 'ACCEPTEE' || m.etat === 'EN_COURS'
      );

      this.historiqueMissions = data.filter(m =>
        m.etat === 'TERMINEE' || m.etat === 'REFUSEE' || m.etat === 'ANNULEE'
      );

      // Copier les missions pour le filtre affiché
      this.filteredMissions = [...this.missions];

      // Charger les véhicules disponibles pour chaque mission EN_ATTENTE
      this.missions.forEach(mission => {
        if (mission.etat === 'EN_ATTENTE') {
          this.loadVehiculesDisponibles(mission);
        }
      });

      console.log("Missions chargées :", this.missions);
      console.log("Historique :", this.historiqueMissions);
    },
    error: (err) => {
      console.error("Erreur lors du chargement des missions :", err);
      this.toastr.error("Impossible de charger les missions");
    }
  });
}


 // Appeler le backend pour obtenir l'affectation automatique
optimiserMissions() {
  this.iaService.optimiser().subscribe({
    next: res => {
      // res = { vehiculeId: missionId }
      // Parcourir toutes les missions et affecter le véhicule proposé
      for (let mission of this.filteredMissions) {
        const assignedVehiculeIdStr = Object.keys(res).find(
          vehIdStr => res[+vehIdStr] === mission.id
        );
        if (assignedVehiculeIdStr) {
          mission.selectedVehiculeId = +assignedVehiculeIdStr; // convertir en nombre
        }
      }
    },
    error: err => console.error(err)
  });
}

}
