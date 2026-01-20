import { Component, OnInit, ViewChild } from '@angular/core';
import { FormArray, FormBuilder, FormGroup, Validators, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { VehiculeService } from '../../../Services/vehicule.service';
import { TechnicienService } from '../../../Services/techniciens.service';
import { MaintenancesService } from '../../../Services/maintenances.service';
import { ToastrService } from 'ngx-toastr';
import { CommonModule } from '@angular/common';
import Swal from 'sweetalert2';
import { SuiviDTO } from '../../../models/SuiviDTO';
import { ActivatedRoute, Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-maintenances',
  templateUrl: './maintenances.component.html',
  styleUrls: ['./maintenances.component.css'],
  imports: [CommonModule, FormsModule, ReactiveFormsModule]
})
export class MaintenancesComponent implements OnInit {

  maintenances: any[] = [];
  filteredMaintenances: any[] = [];
  groupedMaintenances: { [key: string]: any[] } = {};
  vehiculesDisponibles: any[] = [];
  techniciens: any[] = [];
  filteredTechniciens: any[] = [];
  suivis: SuiviDTO[] = []; 
  maintenanceForm!: FormGroup;
  showAddForm = false;
  isEditMode = false;
  selectedMaintenanceId!: number;
  error: string | null = null;
  searchImmatriculation: string = '';
  selectedType: string = '';
  currentMaintenance: any = null;

  typeIncidents = ['ACCIDENT', 'PANNE', 'PREVENTIVE'];
typeInterventions = ['VIDANGE', 'changement_FREINS', 'changement_ROUES', 'changement_BATTERIE','AUTRE'];

  constructor(
    private fb: FormBuilder,
    private maintenanceService: MaintenancesService,
    private vehiculeService: VehiculeService,
    private technicienService: TechnicienService,
    private toastr: ToastrService,
    private route: ActivatedRoute,
     private http: HttpClient,
     private router:Router
  ) {}

  ngOnInit(): void {
    this.initForm();
    this.loadMaintenances();
    this.loadVehicules();
    this.loadTechniciens();
    this.route.queryParams.subscribe(params => {
  if (params['vehiculeId']) {
    this.showAddForm = true;
    this.isEditMode = false;
    this.maintenanceForm.patchValue({
      vehiculeId: +params['vehiculeId'],
      observations: `Maintenance immédiate pour ${params['immatriculation']}`
    });
  }
});
  }
  /** ---------------------- FORMULAIRE ---------------------- **/
  initForm() {
    this.maintenanceForm = this.fb.group({
      numeroMaintenance: [{ value: '', disabled: true }],
      observations: ['', Validators.required],
      statut: ['Initialisée'],
      technicienId: [null, Validators.required],
      vehiculeId: [null, Validators.required],
      typeIncident: [null, Validators.required],
      interventions: this.fb.array([]),
      dateDepartPlanifiee: [''],
      dateFinPlanifiee: ['']
    });
      this.addIntervention(); // Ajouter une intervention par défaut

  }
  get interventions(): FormArray {
    return this.maintenanceForm.get('interventions') as FormArray;
  }
/** ---------------------- INTERVENTIONS ---------------------- **/
addIntervention(intervention?: any) {
  this.interventions.push(
    this.fb.group({
      typeIntervention: [intervention?.typeIntervention || null, Validators.required],
      autreTypeIntervention: [
        intervention?.autreTypeIntervention || '',
        // Validator conditionnel : requis seulement si type === 'AUTRE'
        []
      ],
      coutTotal: [intervention?.coutTotal || 0, [Validators.required, Validators.min(0)]]
    })
  );
}
/**
 * Affiche ou masque le champ "autreTypeIntervention"
 * selon que l'utilisateur a choisi "AUTRE" ou non
 */
checkOtherType(index: number) {
  const control = this.interventions.at(index);
  const typeValue = control.get('typeIntervention')?.value;

  if (typeValue === 'AUTRE') {
    // Champ libre devient requis
    control.get('autreTypeIntervention')?.setValidators([Validators.required]);
  } else {
    // Sinon on supprime la valeur et le validator
    control.get('autreTypeIntervention')?.setValue('');
    control.get('autreTypeIntervention')?.clearValidators();
  }
  control.get('autreTypeIntervention')?.updateValueAndValidity();
}
  removeIntervention(index: number) {
    this.interventions.removeAt(index);
  }

  toggleAddForm() {
    this.showAddForm = !this.showAddForm;
    if (!this.showAddForm) this.resetForm();
  }

  /** ---------------------- CHARGEMENT ---------------------- **/
  loadMaintenances() {
  this.maintenanceService.getAll().subscribe({
    next: data => {
      // Ajouter une propriété temporaire pour gérer les changements de statut
      this.maintenances = data.map(m => ({
        ...m,
        nouveauStatut: m.statut,
        showPlanificationDates: false, // initialisation
        tempDateDepart: m.dateDepartPlanifiee || '',
        tempDateFin: m.dateFinPlanifiee || ''
      }));
      this.filteredMaintenances = [...this.maintenances];
      this.groupByStatut();
    },
    error: () => this.error = 'Erreur de chargement des maintenances'
  });
}
  loadVehicules() {
    this.vehiculeService.getVehicules().subscribe({
next: data => this.vehiculesDisponibles = data.filter(v => 
  v.etat === 'EN_PANNE' || v.etat === 'DISPONIBLE'|| 
    v.etat === 'EN_ATTENTE' || 
    v.etat === 'RESERVE'),
      error: () => this.error = 'Erreur de chargement des véhicules'
    });
  }
  loadTechniciens() {
    this.technicienService.getAllTechniciens().subscribe({
      next: data => this.techniciens = data,
      error: () => this.error = 'Erreur de chargement des techniciens'
    });
  }
  /** ---------------------- FILTRAGE ---------------------- **/
  filterTechniciensByType() {
    if (!this.selectedType) {
      this.filteredTechniciens = [];
      return;
    }
    this.filteredTechniciens = this.techniciens.filter(t => t.type === this.selectedType);
    if (!this.isEditMode) this.maintenanceForm.patchValue({ technicienId: null });
  }
  filterMaintenances() {
    const query = this.searchImmatriculation.toLowerCase().trim();
    this.filteredMaintenances = query
      ? this.maintenances.filter(m =>
          (m.vehiculeImmatriculation || '').toLowerCase().includes(query)
        )
      : this.maintenances;
    this.groupByStatut();
  }

  groupByStatut() {
    this.groupedMaintenances = this.filteredMaintenances.reduce((acc, m) => {
      if (!acc[m.statut]) acc[m.statut] = [];
      acc[m.statut].push(m);
      return acc;
    }, {} as { [key: string]: any[] });
  }

  /** ---------------------- STATUT ---------------------- **/
 changerStatut(m: any) {
  if (m.statut === 'Initialisée' ) {
    // Si l'utilisateur choisit "Planifiée", afficher les champs de date
    m.showPlanificationDates = true;
  } else if (m.statut === 'Planifiée') {
    // Si c'était déjà planifiée et qu'on change, on peut directement mettre à jour
    this.sauvegarderPlanification(m);
  } else {
    // Pour les autres statuts
    this.updateMaintenance(m);
  }
}
 // Variable temporaire pour stocker les dates avant l'envoi
initPlanification(m: any) {
  m.tempDateDepart = m.dateDepartPlanifiee || '';
  m.tempDateFin = m.dateFinPlanifiee || '';
  m.showPlanificationDates = true;
}
sauvegarderPlanification(m: any) {
  if (!m.tempDateDepart || !m.tempDateFin) {
    this.toastr.error('Veuillez renseigner les deux dates.', 'Erreur');
    return;
  }

  const dateDepart = new Date(m.tempDateDepart);
  const dateFin = new Date(m.tempDateFin);

  if (dateFin < dateDepart) {
    this.toastr.warning('La date de fin doit être postérieure à la date de début.', 'Validation');
    return;
  }

  // Formatage en yyyy-MM-dd pour Spring Boot
  const formatDate = (d: Date) => d.toISOString().split('T')[0];

  // ⚡ Payload complet incluant tous les champs existants
  const payload = {
    idMaintenance: m.idMaintenance,
    numeroMaintenance: m.numeroMaintenance,
    observations: m.observations,
    statut: 'Planifiée',
    technicienId: m.technicienId,
    vehiculeId: m.vehiculeId,
    typeIncident: m.typeIncident,
    lignesMaintenance: m.lignesMaintenance || [],  // si tu as des lignes
    typeIntervention: m.typeIntervention || [],
    coutTotal: m.coutTotal || 0,
    dateDepartPlanifiee: formatDate(dateDepart),
    dateFinPlanifiee: formatDate(dateFin)
  };

  this.maintenanceService.update(m.idMaintenance, payload).subscribe({
    next: () => {
      this.toastr.success('Dates de planification enregistrées !');
      m.dateDepartPlanifiee = payload.dateDepartPlanifiee;
      m.dateFinPlanifiee = payload.dateFinPlanifiee;
      m.statut = payload.statut;
      m.showPlanificationDates = false;
      this.loadMaintenances();
    },
    error: (err) => {
      console.error('Erreur backend :', err);
      this.toastr.error('Erreur lors de l’enregistrement des dates');
    }
  });
}
canDemarrer(m: any): boolean {
  return m.statut === 'En cours' && !m.dateMaintenance;
}
demarrerMaintenance(m: any) {
/*   if (!m.dateDepartPlanifiee) {
    this.toastr.warning('Veuillez renseigner la date de début planifiée avant de démarrer.');
    return;
  } */

  const dateDebut = new Date().toISOString().split('T')[0]; // date du jour
  const payload = {
    idMaintenance: m.idMaintenance,
    statut: 'En cours',
    dateMaintenance: dateDebut,
    dateDepartPlanifiee: m.dateDepartPlanifiee,
    dateFinPlanifiee: m.dateFinPlanifiee,
    numeroMaintenance: m.numeroMaintenance,
    observations: m.observations,
    technicienId: m.technicienId,
    vehiculeId: m.vehiculeId,
    typeIncident: m.typeIncident,
    lignesMaintenance: m.lignesMaintenance || [],
    typeIntervention: m.typeIntervention || [],
    coutTotal: m.coutTotal || 0
  };

  this.maintenanceService.update(m.idMaintenance, payload).subscribe({
    next: () => {
      m.statut = 'En cours';
      m.dateMaintenance = dateDebut;
      this.toastr.success('Maintenance démarrée et statut mis à jour en "En cours" ✅');
      this.groupByStatut();
    },
    error: (err) => {
      console.error('Erreur backend :', err);
      this.toastr.error('Impossible de démarrer la maintenance');
    }
  });
}


  updateMaintenance(m: any) {
    this.maintenanceService.update(m.idMaintenance, m).subscribe({
      next: () => {
        this.toastr.success('Maintenance mise à jour avec succès !');
        this.loadMaintenances();
      },
      error: () => this.toastr.error('Erreur lors de la mise à jour.')
    });
  }

editMaintenance(m: any) {
  this.isEditMode = true;
  this.selectedMaintenanceId = m.idMaintenance;
  this.showAddForm = true;
  this.currentMaintenance = m;

  // Pré-remplir le formulaire
  this.maintenanceForm.patchValue({
    numeroMaintenance: m.numeroMaintenance,
    observations: m.observations,
    statut: m.statut,
    technicienId: m.technicienId,
    vehiculeId: m.vehiculeId,
    typeIncident: m.typeIncident,
    dateDepartPlanifiee: m.dateDepartPlanifiee,
    dateFinPlanifiee: m.dateFinPlanifiee
  });

  // Désactiver les champs spécifiques en mode édition
  this.maintenanceForm.get('technicienId')?.disable();
  this.maintenanceForm.get('vehiculeId')?.disable();
  this.maintenanceForm.get('typeIncident')?.disable();
  this.maintenanceForm.get('numeroMaintenance')?.disable();

  // Supprimer d'abord les interventions existantes pour les réafficher
  this.interventions.clear();

  // Ajouter les interventions existantes dans le FormArray
  if (m.lignesMaintenance && m.lignesMaintenance.length > 0) {
    m.lignesMaintenance.forEach((l: any) => {
      this.addIntervention({
        typeIntervention: l.description,
        coutTotal: l.montant
      });
    });
  }
}

onSubmit() {
  if (this.maintenanceForm.invalid) {
    this.toastr.warning('Veuillez remplir tous les champs obligatoires !');
    return;
  }

  const formValue = this.maintenanceForm.getRawValue();

  // 🗓️ Formatage des dates
  const formatDate = (d: string | Date) =>
    d ? new Date(d).toISOString().split('T')[0] : null;

  const dateDepartPlanifiee = formatDate(formValue.dateDepartPlanifiee);
  const dateFinPlanifiee = formatDate(formValue.dateFinPlanifiee);

  // 🧩 Normalisation des types d'intervention
  const normalizeType = (type: string): string => {
    if (!type) return 'AUTRE';
    const t = type.trim().toUpperCase();
    const map: any = {
      'VIDANGE': 'VIDANGE',
      'CHANGEMENT FREINS': 'changement_FREINS',
      'CHANGEMENT_FREINS': 'changement_FREINS',
      'CHANGEMENT ROUES': 'changement_ROUES',
      'CHANGEMENT_ROUES': 'changement_ROUES',
      'CHANGEMENT BATTERIE': 'changement_BATTERIE',
      'CHANGEMENT_BATTERIE': 'changement_BATTERIE',
      'AUTRE': 'AUTRE'
    };
    return map[t] || 'AUTRE';
  };

  // 🔧 Construction des lignes de maintenance
  const lignesMaintenance = this.interventions.controls.map(ctrl => {
    let type = ctrl.get('typeIntervention')?.value;
    let autreDescription: string | null = null;

    if (type === 'AUTRE') {
      autreDescription = ctrl.get('autreTypeIntervention')?.value?.trim() || 'AUTRE';
    }

    return {
      description: normalizeType(type),
      quantite: 1,
      montant: ctrl.get('coutTotal')?.value || 0,
      typeMaintenance: null,
      autreDescription: autreDescription
    };
  });

  // 📦 Liste des types d'intervention normalisés
  const typeIntervention = lignesMaintenance.map(l => l.description);

  // 💰 Calcul du coût total
  const coutTotal = lignesMaintenance.reduce(
    (sum, l) => sum + (l.montant || 0),
    0
  );

  // 💌 Payload final envoyé au backend
  const payload: any = {
    numeroMaintenance: formValue.numeroMaintenance,
    observations: formValue.observations,
    statut: formValue.statut,
    technicienId: Number(formValue.technicienId),
    vehiculeId: Number(formValue.vehiculeId),
    typeIncident: formValue.typeIncident,
    lignesMaintenance: lignesMaintenance,
    typeIntervention: typeIntervention,
    coutTotal: coutTotal,
    dateDepartPlanifiee: dateDepartPlanifiee,
    dateFinPlanifiee: dateFinPlanifiee
  };

  console.log('📤 Payload envoyé au backend :', payload);

  const handleSuccess = (res: any) => {
    this.toastr.success(this.isEditMode ? 'Maintenance modifiée !' : 'Maintenance ajoutée !');

    // 🔹 Mettre à jour l’état du véhicule localement
    const vehicule = this.vehiculesDisponibles.find(v => v.idVehicule === payload.vehiculeId);
    if (vehicule) vehicule.etat = 'MAINTENANCE';

    // 🔹 Re-filtrer les véhicules disponibles pour le formulaire
    this.vehiculesDisponibles = this.vehiculesDisponibles.filter(v => v.etat === 'DISPONIBLE');

    this.loadMaintenances();
    this.resetForm();
  };

  // 🚀 Appel backend
  if (this.isEditMode) {
    this.maintenanceService.update(this.selectedMaintenanceId!, payload).subscribe({
      next: handleSuccess,
      error: err => {
        console.error('Erreur backend :', err);
        this.toastr.error('Erreur lors de la mise à jour.');
      }
    });
  } else {
    this.maintenanceService.createSimple(payload).subscribe({
      next: handleSuccess,
      error: err => {
        console.error('Erreur backend :', err);
        this.toastr.error('Erreur lors de l’ajout.');
      }
    });
  }
}


resetForm() {
  this.maintenanceForm.reset({
    numeroMaintenance: '',
    observations: '',
    statut: 'Initialisée',
    technicienId: null,
    vehiculeId: null,
    typeIncident: null,
    dateDepartPlanifiee: '',
    dateFinPlanifiee: ''
  });
  this.interventions.clear();
  this.isEditMode = false;
  this.showAddForm = false;
  this.currentMaintenance = null;

  // Réactiver les champs au cas où
  this.maintenanceForm.get('technicienId')?.enable();
  this.maintenanceForm.get('vehiculeId')?.enable();
  this.maintenanceForm.get('typeIncident')?.enable();
  this.maintenanceForm.get('numeroMaintenance')?.enable();
}
  supprimerMaintenance(id: number) {
    if (confirm('Voulez-vous vraiment supprimer cette maintenance ?')) {
      this.maintenanceService.delete(id).subscribe({
        next: () => this.loadMaintenances(),
        error: () => this.toastr.error('Erreur lors de la suppression.')
      });
    }
  }
terminerMaintenance(m: any) {
  const payload = {
    idMaintenance: m.idMaintenance,
    statut: 'Terminée',
    dateFinMaintenance: new Date().toISOString().split('T')[0], // yyyy-MM-dd
    numeroMaintenance: m.numeroMaintenance,
    observations: m.observations,
    technicienId: m.technicienId,
    vehiculeId: m.vehiculeId,
    typeIncident: m.typeIncident,
    lignesMaintenance: m.lignesMaintenance || [],
    typeIntervention: m.typeIntervention || [],
    coutTotal: m.coutTotal || 0,
    dateMaintenance: m.dateMaintenance || null // conserver la date de début si existante
  };

  this.maintenanceService.update(m.idMaintenance, payload).subscribe({
    next: () => {
      this.toastr.success('Maintenance terminée ! ✅');

      // ✅ Mettre à jour localement
      m.statut = 'Terminée';
      m.dateFinMaintenance = payload.dateFinMaintenance;

      // ✅ Rafraîchir les groupes (pour déplacer la maintenance dans “Terminée”)
      this.groupByStatut();
    },
    error: (err) => {
      console.error('Erreur backend :', err);
      this.toastr.error('Impossible de terminer la maintenance');
    }
  });
}
onStatutChange(m: any) {
  // Si le statut devient "Planifiée", afficher les champs de date
  if (m.statut === 'Planifiée') {
    m.showPlanificationDates = true;
  } 
  // Si le statut devient "En cours", masquer la planification
  else if (m.statut === 'En cours') {
    m.showPlanificationDates = false;
  } 
    //  Si l'utilisateur choisit "Terminée", on appelle la méthode correspondante
  if (m.statut === 'Terminée') {
    this.preparerFinMaintenance(m);
  }
  //  Regroupe de nouveau
  this.groupByStatut();
}
terminerEtEditerMaintenance(m: any) {
  this.showAddForm = true;
  this.isEditMode = true;
  this.selectedMaintenanceId = m.idMaintenance;
  this.currentMaintenance = m;

  // 🔹 Assurer une date de fin
  const dateFin = m.dateFinMaintenance || new Date().toISOString().split('T')[0];
  m.dateFinMaintenance = dateFin;

  // 🔹 Réinitialiser le formulaire avec les valeurs de la maintenance
  this.maintenanceForm.reset({
    numeroMaintenance: m.numeroMaintenance,
    observations: m.observations,
    statut: 'Terminée',
    technicienId: m.technicienId,
    vehiculeId: m.vehiculeId,
    typeIncident: m.typeIncident,
    dateDepartPlanifiee: m.dateDepartPlanifiee,
    dateFinPlanifiee: m.dateFinPlanifiee,
    dateFinMaintenance: dateFin
  });

  // 🔹 Réinitialiser les interventions
  this.interventions.clear();
  if (m.lignesMaintenance && m.lignesMaintenance.length > 0) {
    m.lignesMaintenance.forEach((l: any) => {
      this.addIntervention({
        typeIntervention: l.description,
        coutTotal: l.montant
      });
    });
  }

  // 🔹 Désactiver tous les champs sauf interventions
  [
    'numeroMaintenance',
    'observations',
    'statut',
    'technicienId',
    'vehiculeId',
    'typeIncident',
    'dateDepartPlanifiee',
    'dateFinPlanifiee'
  ].forEach(f => this.maintenanceForm.get(f)?.disable());

  // 🔹 Mettre à jour le véhicule et le suivi après la maintenance
  const vehicule = this.vehiculesDisponibles.find(v => v.idVehicule === m.vehiculeId);
  if (vehicule) {
    // Changer l'état local immédiatement
    vehicule.etat = 'DISPONIBLE';

    // Appel backend pour réinitialiser le suivi
    this.updateSuiviVehicule(m, vehicule);
  }

  // 🔹 Scroll vers le haut pour afficher le formulaire
  setTimeout(() => window.scrollTo({ top: 0, behavior: 'smooth' }), 100);
}



preparerFinMaintenance(m: any) {
  // Charger techniciens et véhicules avant affichage
  this.loadTechniciens();
  this.loadVehicules();

  this.showAddForm = true;
  this.isEditMode = true;
  this.selectedMaintenanceId = m.idMaintenance;
  this.currentMaintenance = m;

  const dateFin = m.dateFinMaintenance || new Date().toISOString().split('T')[0];
  m.dateFinMaintenance = dateFin;

  this.maintenanceForm.reset({
    numeroMaintenance: m.numeroMaintenance,
    observations: m.observations,
    statut: 'Terminée',
    technicienId: m.technicienId,
    vehiculeId: m.vehiculeId,
    typeIncident: m.typeIncident,
    dateDepartPlanifiee: m.dateDepartPlanifiee,
    dateFinPlanifiee: m.dateFinPlanifiee,
    dateFinMaintenance: dateFin
  });

  this.interventions.clear();
  if (m.lignesMaintenance?.length > 0) {
    m.lignesMaintenance.forEach((l: any) => {
      this.addIntervention({ typeIntervention: l.description, coutTotal: l.montant });
    });
  }
}

onStatutTempChange(m: any, event: any) {
  m.nouveauStatut = event.target.value; // Changement temporaire
}
confirmerChaStatut(m: any) {
  if (!m.nouveauStatut) {
    this.toastr.warning('Veuillez sélectionner un nouveau statut avant de confirmer.');
    return;
  }
  if (m.nouveauStatut === m.statut) {
    this.toastr.info('Le statut sélectionné est identique au statut actuel.');
    return;
  }

  if (m.nouveauStatut === 'Planifiée') {
    m.showPlanificationDates = true;
    this.toastr.info('Veuillez définir les dates de planification avant confirmation.');
    return;
  }

  Swal.fire({
    title: 'Confirmation',
    text: `Voulez-vous vraiment passer cette maintenance du statut "${m.statut}" à "${m.nouveauStatut}" ?`,
    icon: 'question',
    showCancelButton: true,
    confirmButtonText: 'Oui, confirmer',
    cancelButtonText: 'Annuler'
  }).then((result) => {
    if (result.isConfirmed) {

      // 🔹 Cas spécial : si on passe de "Planifiée" → "En cours"
      if (m.nouveauStatut === 'En cours' && m.statut === 'Planifiée') {
        // On fait update + démarrage automatique
        const dateDebut = new Date().toISOString().split('T')[0];

        const payload = {
          statut: 'En cours',
          dateMaintenance: dateDebut,
          dateDepartPlanifiee: m.dateDepartPlanifiee,
          dateFinPlanifiee: m.dateFinPlanifiee
        };

        this.maintenanceService.update(m.idMaintenance, payload).subscribe({
          next: () => {
            this.toastr.success('Statut mis à jour et maintenance démarrée ✅');
            // on met à jour localement
            m.statut = 'En cours';
            m.dateMaintenance = dateDebut;

            // on appelle la méthode de démarrage pour logique additionnelle
            this.demarrerMaintenance(m);

            // regroupement par statut
            this.groupByStatut();
          },
          error: (err) => {
            console.error('Erreur lors du démarrage :', err);
            this.toastr.error('Erreur lors du démarrage de la maintenance');
          }
        });
        return; // stop ici pour éviter de retomber dans le bloc standard
      }

      // 🔹 Cas standard : simple update
      const payload = {
        statut: m.nouveauStatut,
        dateDepartPlanifiee: m.dateDepartPlanifiee || null,
        dateFinPlanifiee: m.dateFinPlanifiee || null
      };

      this.maintenanceService.update(m.idMaintenance, payload).subscribe({
        next: () => {
          m.statut = m.nouveauStatut;
          m.nouveauStatut = m.statut;
          m.showPlanificationDates = false;
          this.toastr.success(`Statut mis à jour avec succès (${m.statut}).`);
          this.groupByStatut();
        },
        error: err => {
          console.error('Erreur lors de la mise à jour du statut :', err);
          this.toastr.error('Erreur lors de la mise à jour du statut.');
        }
      });
    }
  });
}

annulerStatut(m: any) {
  // Réinitialise le statut temporaire au statut actuel
  m.nouveauStatut = m.statut;

  // Masquer le formulaire de planification si nécessaire
  m.showPlanificationDates = false;

  // Optionnel : réinitialiser les dates temporaires
  m.tempDateDepart = m.dateDepartPlanifiee || '';
  m.tempDateFin = m.dateFinPlanifiee || '';

  this.toastr.info('Changement de statut annulé.');
}
onStatutChangeTemp(m: any, newStatut: string) {
  m.nouveauStatut = newStatut;

  // Si on choisit "Planifiée", afficher le formulaire de planification
  if (newStatut === 'Planifiée') {
    m.showPlanificationDates = true;
    return;
  }

  // Si on choisit "En cours" → confirmation + démarrage automatique
  if (newStatut === 'En cours') {
    Swal.fire({
      title: 'Démarrer la maintenance ?',
      text: 'Voulez-vous vraiment démarrer cette maintenance maintenant ?',
      icon: 'question',
      showCancelButton: true,
      confirmButtonText: 'Oui, démarrer',
      cancelButtonText: 'Annuler'
    }).then(result => {
      if (result.isConfirmed) {
        this.demarrerMaintenance(m);
      } else {
        m.nouveauStatut = m.statut; // Annule le changement
      }
    });
    return;
  }

  // ✅ Si on choisit "Terminée" → ouvrir le formulaire de fin de maintenance
  if (newStatut === 'Terminée') {
    Swal.fire({
      title: 'Confirmer la fin de la maintenance ?',
      text: 'Une fois terminée, la maintenance ne pourra plus être modifiée.',
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText: 'Oui, terminer',
      cancelButtonText: 'Annuler'
    }).then(result => {
      if (result.isConfirmed) {
        // Appel à ta méthode qui prépare la fin de maintenance
        this.preparerFinMaintenance(m);

        // Afficher un message d’information
        this.toastr.info('Veuillez vérifier et valider les détails de fin de maintenance.');
      } else {
        m.nouveauStatut = m.statut; // Annule le changement
      }
    });
    return;
  }

  // Autres statuts : simple mise à jour directe
  const payload = { statut: newStatut };
  this.maintenanceService.update(m.idMaintenance, payload).subscribe({
    next: () => {
      m.statut = newStatut;
      this.toastr.success(`Statut changé en "${newStatut}"`);
      this.groupByStatut();
    },
    error: () => this.toastr.error('Erreur lors de la mise à jour du statut')
  });
}


@ViewChild('maintenanceComp') maintenanceComp!: MaintenancesComponent;

ouvrirFormulaireMaintenance(vehicule: SuiviDTO) {
  this.maintenanceComp.showAddForm = true;
  this.maintenanceComp.maintenanceForm.patchValue({
    vehiculeId: vehicule.idVehicule,
    observations: `Maintenance immédiate pour ${vehicule.immatriculation}`
  });
}
updateSuiviVehicule(m: any, vehicule: any) {
  if (!vehicule?.idVehicule) {
    this.toastr.error("Véhicule invalide ❌");
    return;
  }

  const data = {
    idVehicule: vehicule.idVehicule,
    suiviId: m.suiviId,
    typeIncident: m.typeIncident?.toUpperCase() || 'PANNE',
    lignesMaintenance: Array.isArray(m.lignesMaintenance)
      ? m.lignesMaintenance.map((l: any) => ({ description: l.description }))
      : []
  };

  console.log('📤 Payload envoyé au backend :', data);

  this.http.post<any>('http://localhost:8090/api/suivis/updateAfterMaintenance', data).subscribe({
    next: (res) => {
      console.log('📥 Réponse backend :', res);

      // 🔹 Trouver le véhicule correspondant dans le tableau
      const v = this.vehiculesDisponibles.find(v => v.idVehicule === vehicule.idVehicule);
      if (v) {
        // 🔹 Mettre à jour le suivi
        v.suivi = { ...v.suivi, ...res };

        // 🔹 Mettre à jour l’état du véhicule si présent dans la réponse
        if (res.vehicule && res.vehicule.etat) {
          v.etat = res.vehicule.etat;
        }
      }

      this.toastr.success('Le suivi du véhicule a été mis à jour après maintenance ✅');
    },
    error: (err) => {
      console.error('❌ Erreur complète :', err);
      this.toastr.error('Erreur lors de la mise à jour du suivi du véhicule ❌');
    }
  });
}

voirCalendrierSuivi() {
  this.router.navigate(['/admin/suivis']); // 👈 c’est ta route du composant suivi
}

}
