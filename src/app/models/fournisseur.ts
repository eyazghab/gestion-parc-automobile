export interface Fournisseur {
  idFournisseur?: number; // optionnel pour les créations
  nomFournisseur: string;
  adresse?: string;
  contact?: string;
  typeService?: string;
  actif: boolean;
}
