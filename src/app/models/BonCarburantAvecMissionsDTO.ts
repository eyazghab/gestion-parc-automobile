import { MissionCarburantDtO } from "./MissionCarburantDTO";


export interface BonCarburantAvecMissionsDTO  {
  id?: number;
  dateAchat?: string; // ISO string
  montant?: number;
  quantite?: number;
  responsable?: string;
  vehiculeImmatriculation?: string;
  missions?: MissionCarburantDtO[];
}