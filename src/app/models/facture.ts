export interface Facture {
  id: number;
  numeroFacture: string;
  dateFacture: Date;
  montantHT: number;
  montantTVA: number;
  montantTTC: number;
  etat: boolean;
}