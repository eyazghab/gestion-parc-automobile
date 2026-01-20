import { Article } from "./Article";
import { Depot } from "./Depot";

export interface Stock {
   id: number;
  quantite_disp: number;
  quantite_reservee: number;
  stock_min: number;
  stock_alerte: number;
  date_dernier_entree: string;    // ISO date string
  date_dernier_sortie: string;
  date_dernier_inventaire: string;
  Iot?: string;
  actif: boolean;
  depot: Depot;
  article: Article;
  motif:string;
}