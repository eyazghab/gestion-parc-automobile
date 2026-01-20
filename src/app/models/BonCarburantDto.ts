// src/app/models/BonCarburantDTO.ts
export enum EtatBon {
  EN_ATTENTE = 'EN_ATTENTE',
  ACCEPTE = 'ACCEPTE',
  REFUSE = 'REFUSE'
}

export interface BonCarburantDto {
  id?: number;
  montant?: number;
  quantite?: number;
  dateAchat?: string;
  responsable?: string;
  vehicule?: {
    idVehicule: number;
    immatriculation: string;
  };
  carburantNom?: string;
  utilisateurNom?: string;
  statut?: EtatBon;
    missionId?: number;

}
