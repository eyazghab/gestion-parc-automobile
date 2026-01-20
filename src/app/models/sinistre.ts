import { Utilisateur } from './utilisateur';
import { Vehicule } from './vehicule';

export interface Sinistre {
  id: number;
  dateSinistre: string;
  description: string;
  heureSinistre: string;
  lieuSinistre: string;
  typeSinistre: string;
  degats: string;
  numDeclaration: string;
  dateDeclaration: string;
  photos?: string[];
   vehicule: { idVehicule: number; immatriculation: string };
  utilisateur?: { idUtilisateur: number; nom: string; prenom: string };
   etat: EtatSinistre;
//  Champs pour le front (optionnels)
  showPrendreEnChargeForm?: boolean;
  selectedTechnicienId?: number;
  selectedCirculation?: string;
}
export enum EtatSinistre {
  DECLARE = 'DECLARE',
  A_MAINTENIR = 'A_MAINTENIR',
  PAS_DE_TRAITEMENT_NECESSAIRE = 'PAS_DE_TRAITEMENT_NECESSAIRE'
}

// dictionnaire de labels lisibles
export const EtatSinistreLabels: Record<EtatSinistre, string> = {
  [EtatSinistre.DECLARE]: 'Déclaré',
  [EtatSinistre.A_MAINTENIR]: 'À maintenir',
  [EtatSinistre.PAS_DE_TRAITEMENT_NECESSAIRE]: 'Pas de traitement à faire'
};

export enum TypeIncident {
  ACCIDENT = 'ACCIDENT',
  PANNE = 'PANNE'
}
