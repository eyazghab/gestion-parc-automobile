


export interface Suivi {
  id: number;
  description: string;
  dateSuivi: string;
  kilometreActuel: number;
  dureeEstimee?: number;
  frequence?: number;
  vehicule?: { id: number, immatriculation: string };
  dateCircu?: Date;
  prochainSuivi?: Date; //  nouveau champ

}
