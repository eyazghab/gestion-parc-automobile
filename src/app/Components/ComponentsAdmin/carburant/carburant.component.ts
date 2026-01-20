import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { BonCarburantService } from '../../../Services/bon-carburant.service';
import { CarburantService, UtilisateurAvecBonsDTO } from '../../../Services/carburant.service';
import { BonCarburant } from '../../../models/BonCarburant';
import { MissionCarburantDtO } from '../../../models/MissionCarburantDTO';
import { CarburantIAService } from '../../../Services/carburant-ia.service';

@Component({
  selector: 'app-carburant',
  templateUrl: './carburant.component.html',
  styleUrls: ['./carburant.component.css'],
  imports: [CommonModule, FormsModule, ReactiveFormsModule, RouterModule]
})
export class CarburantComponent implements OnInit {

  bons: BonCarburant[] = [];
  bonsGroupes: { immatriculation: string, bons: (BonCarburant & { editQuantite?: boolean, editMontant?: boolean })[] }[] = [];
  formBon!: FormGroup;
  searchImmatriculation: string = '';
// Message à afficher sous le tableau des missions pour chaque bon
messagesParBon: { [bonId: number]: { texte: string, type: 'success' | 'warning' | 'info' | 'error' } } = {};
  missionsParVehicule: { [vehiculeId: number]: MissionCarburantDtO[] } = {};
  missionsParBon: { [key: number]: MissionCarburantDtO[] } = {};
  bonsOuverts: { [key: number]: boolean } = {};
  predictionsCarburant: { [vehiculeId: number]: string } = {};
  utilisateursAvecBons: UtilisateurAvecBonsDTO[] = [];
  searchUtilisateur: string = '';

  constructor(
    private bonService: BonCarburantService,
    private carburantService: CarburantService,
    private fb: FormBuilder,
    private toastr: ToastrService,
    private iaService: CarburantIAService
  ) { }

  ngOnInit(): void {
    this.formBon = this.fb.group({
      dateAchat: ['', Validators.required],
      quantite: ['', Validators.required],
      montant: ['', Validators.required],
      vehiculeId: ['', Validators.required],
      utilisateurId: ['', Validators.required]
    });

    this.iaService.train().subscribe({
      next: res => this.toastr.success(res.message),
      error: () => this.toastr.error('Erreur lors de l\'entraînement du modèle IA')
    });

    this.loadBons();
  }
  private formatLitres(val: number): number {
    return Math.round((val + Number.EPSILON) * 100) / 100;
  }
  // Charger tous les bons et missions associées
  loadBons(): void {
    this.bonService.getAllBons().subscribe({
      next: (data) => {
        this.bons = data;
        this.regrouperBonsParVehicule();
      },
      error: () => this.toastr.error('Erreur lors du chargement des bons')
    });
  }

  // Regrouper bons par véhicule
  regrouperBonsParVehicule(): void {
    const map = new Map<string, (BonCarburant & { editQuantite?: boolean })[]>();

    this.bons.forEach(b => {
      const key = b.vehicule?.immatriculation || 'Véhicule inconnu';
      if (!map.has(key)) map.set(key, []);
      map.get(key)?.push({ ...b, editQuantite: false });

      if (b.vehicule?.idVehicule && !this.missionsParVehicule[b.vehicule.idVehicule]) {
        this.carburantService.getMissionsParVehicule(b.vehicule.idVehicule).subscribe({
          next: missions => this.missionsParVehicule[b.vehicule!.idVehicule!] = missions,
          error: () => this.toastr.error(`Erreur lors du chargement des missions pour le véhicule ${key}`)
        });
      }

      if (b.vehicule?.idVehicule && !this.predictionsCarburant[b.vehicule.idVehicule]) {
        this.verifierCarburantIA(b);
      }
    });

    this.bonsGroupes = Array.from(map.entries()).map(([immatriculation, bons]) => ({
      immatriculation,
      bons
    }));
  }

  // Ajouter un nouveau bon
  ajouter(): void {
    if (!this.formBon.valid) return;

    this.bonService.addBon(this.formBon.value).subscribe({
      next: () => {
        this.toastr.success('Bon ajouté avec succès');
        this.formBon.reset();
        this.loadBons();
      },
      error: () => this.toastr.error('Erreur lors de l\'ajout')
    });
  }

  // Valider un bon (ACCEPTE ou REFUSE)
  // Valider un bon
  valider(id: number, action: string): void {
    this.bonService.valider(id, action).subscribe({
      next: () => {
        this.toastr.success(`Bon ${action.toLowerCase()} avec succès ✅`);
        this.loadBons();
      },
      error: err => {
        const msg = err.error?.message?.replace(/([0-9]+\.[0-9]{10,})/g, (m: string) =>
          this.formatLitres(parseFloat(m)).toString()
        ) || 'Erreur lors de la validation ❌';
        this.toastr.error(msg);
      }
    });
  }

  // Vérifier la quantité disponible pour un bon selon ses missions
verifierCarburant(bon: BonCarburant & { editQuantite?: boolean }): void {
  if (bon.id == null) {
    // Si le bon n'a pas d'id, impossible d'afficher le message lié
    return;
  }

  const bonId = bon.id;

  this.bonService.getMissionsParBon(bonId).subscribe({
    next: (missions: MissionCarburantDtO[]) => {
      if (!missions || missions.length === 0) {
        this.messagesParBon[bonId] = { texte: 'Aucune mission liée à ce bon', type: 'info' };
        return;
      }

      // Calcul de la quantité totale nécessaire
      const totalNecessaire = missions.reduce((sum, m) => sum + (m.carburantNecessaire ?? 0), 0);
      const totalArrondi = this.formatLitres(totalNecessaire);

      const quantiteBon = bon.quantite ?? 0;

      if (quantiteBon < totalArrondi) {
        this.messagesParBon[bonId] = { 
          texte: `Quantité insuffisante ! (${quantiteBon} L saisi, ${totalArrondi} L nécessaire)`, 
          type: 'warning' 
        };
        bon.editQuantite = true;
      } else {
        this.messagesParBon[bonId] = { 
          texte: `Quantité suffisante (${quantiteBon} L ≥ ${totalArrondi} L nécessaire)`, 
          type: 'success' 
        };
        bon.editQuantite = false;
      }
    },
    error: () => {
      this.messagesParBon[bonId] = { texte: 'Erreur lors de la récupération des missions', type: 'error' };
    }
  });
}






  // Mettre à jour la quantité d’un bon
  mettreAJourQuantite(bon: BonCarburant & { editQuantite?: boolean }): void {
    if (!bon.id || bon.quantite == null) return;

    this.bonService.mettreAJourQuantite(bon.id, bon.quantite).subscribe({
      next: (updatedBon) => {
        // Mettre à jour montant et quantité
        bon.quantite = updatedBon.quantite;
        bon.montant = updatedBon.montant; // montant calculé automatiquement
        bon.editQuantite = false;
        this.toastr.success('Quantité et montant mis à jour avec succès');
      },
      error: () => this.toastr.error('Erreur lors de la mise à jour')
    });
  }



  // Filtrer les bons par immatriculation
  get bonsGroupesFiltres() {
    if (!this.searchImmatriculation) return this.bonsGroupes;
    const search = this.searchImmatriculation.toLowerCase();
    return this.bonsGroupes.filter(g =>
      g.immatriculation.toLowerCase().includes(search)
    );
  }

  // IA prédictive
async verifierCarburantIA(bon: BonCarburant & { editQuantite?: boolean }): Promise<void> {
  if (!bon.vehicule?.idVehicule || bon.quantite == null) return;

  const vehiculeId = bon.vehicule.idVehicule;
  const quantite = bon.quantite;

  try {
    const res = await this.iaService.predict(vehiculeId, quantite).toPromise();
    // Si res est undefined, on met un message par défaut
    this.predictionsCarburant[vehiculeId] = res?.message ?? '❌ Pas de prédiction';
  } catch (err) {
    this.predictionsCarburant[vehiculeId] = '❌ Erreur lors de la prédiction';
  }
}



  trainModelIA(): Promise<boolean> {
    return new Promise((resolve, reject) => {
      this.iaService.train().subscribe({
        next: (res: any) => {
          this.toastr.success(res.message);
          resolve(true);
        },
        error: () => {
          this.toastr.error('Erreur lors de l\'entraînement du modèle IA');
          reject(false);
        }
      });
    });
  }

  // Afficher / cacher les missions liées à un bon
  toggleMissions(bon: BonCarburant): void {
    if (!bon.id) return;
    const bonId = bon.id;

    if (this.bonsOuverts[bonId]) {
      this.bonsOuverts[bonId] = false;
      return;
    }

    this.bonService.getMissionsParBon(bonId).subscribe({
      next: missions => {
        this.missionsParBon[bonId] = missions;
        this.bonsOuverts[bonId] = true;
      },
      error: () => this.toastr.error(`Erreur lors du chargement des missions pour le bon ${bonId}`)
    });
  }
}
