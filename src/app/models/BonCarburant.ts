

export interface BonCarburant {
  id?: number;
  montant?: number;
  quantite?: number;
  dateAchat?: string;   // <-- pas dateBon
  responsable?: string;
  vehicule?: {
    idVehicule: number;
    immatriculation: string;
  };
  carburantNom?: string;
  utilisateurNom?: string;  // <-- nom utilisateur
 etat?: EtatBon;   
  editQuantite?: boolean;
  missionId?: number;
  }

export enum EtatBon {
  EN_ATTENTE = 'EN_ATTENTE',
  ACCEPTE = 'ACCEPTE',
  REFUSE = 'REFUSE'
}