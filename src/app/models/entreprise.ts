import { Departement } from "./departement";


export interface Entreprise {
  idEntreprise: number;
  nomEntreprise: string;
  secteurActivite: string;
  adresse: string;
  email: string;
  telephone: string;
  nbEmployes: number;

  // Relation
  departements?: Departement[];   // Liste des départements associés à cette entreprise (optionnel)
}
