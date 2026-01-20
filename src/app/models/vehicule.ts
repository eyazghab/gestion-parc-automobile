import { Utilisateur } from "./utilisateur";

export interface Vehicule {
  idVehicule: number;
  immatriculation: string;
  numeroChassis: string;
  dateCircu: string;
  date_acquisition : Date;
  kilometrageActuel: string;
  anneeModel: number;
  typeCarburant: string;
    etat: 'EN_ATTENTE' | 'EN_COURS' | 'En_TRAITEMENT' | 'TRAITE' |'EN_PANNE' |'DISPONIBLE'| 'MAINTENANCE'|'RESERVE'		
;
 parking?: { nom: string };
  utilisateur?: { nom: string };
    photoUrl?: string;
  modelee?: { libelle: string };
  typeCarrosserie?: { libelle: string }; 
}
export type EtatVehicule = 'DISPONIBLE' | 'RESERVE' | 'EN_PANNE' | 'MAINTENANCE' | 'ACCIDENTE' | 'EN_ATTENTE';
