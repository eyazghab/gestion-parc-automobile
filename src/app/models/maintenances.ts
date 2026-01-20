

export interface Maintenance {
 idMaintenance?: number;
  numeroMaintenance: string;
  datePlanifiee: string;
  dateEffectuee?: string;
  dateDepartPlanifiee?: string;   // ✅ champ virtuel
  dateFinPlanifiee?: string;      // ✅ champ virtuel

  kilometrage?: number;
  prestataire?: string;
  coutPiece?: number;
  coutExterne?: number;
  coutTotal?: number;
  typeMaintenance?: string;
  numeroFa?: string;
  statut?: string;
  observations?: string;
  dateFacture?: string;
  dateDemande?: string;
  vehicule?: any;
  technicien?: any;
  prediction?: string;
  typeIncident?: 'ACCIDENT' | 'PANNE' | 'PREVENTIVE';
  typeIntervention?: 'VIDANGE' | 'Changement_FREINS' | 'Changement_ROUES' | 'Changement_BATTERIE';
}

