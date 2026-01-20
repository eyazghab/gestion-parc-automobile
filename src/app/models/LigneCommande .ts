import { Article } from "./Article";


export interface LigneCommande {
   id: number;
  produit?: string;  // facultatif si tu utilises article
  quantite: number;  // obligatoire
  prixUnitaire: number; // obligatoire
  commandeId?: number;  // facultatif
  article: Article;


}


