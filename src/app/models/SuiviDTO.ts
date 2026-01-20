import { Fournisseur } from "./fournisseur";


export interface SuiviDTO {
  idVehicule: number;
  immatriculation: string;
  kmActuel: number;
  nombreAccidents: number;
  nombrePannes: number;
  kilometresDepuisFreins: number;
  kilometresDepuisVidange: number;
  dureeVieBatterie: number;
  alerteMaintenance: boolean;
  description: string;
  frequence: number;
  dureeEstimé: number;
  echeances?: any[]; // ou type précis si défini
  alertes?: string[];
  etatVehicule: 'DISPONIBLE' | 'RESERVE' | 'EN_PANNE' | 'MAINTENANCE' | 'ACCIDENTE' | 'EN_ATTENTE';
  prochainSuivi?: Date;
 consommationMoyenne?: number;
}
export type EtatSimple = 'bon' | 'maintenance' | 'panne' | 'accident';

export interface SuiviAvecEtat extends SuiviDTO {
  etatSimple: EtatSimple;
}