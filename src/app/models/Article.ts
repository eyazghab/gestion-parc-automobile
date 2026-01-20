import { Fournisseur } from "./fournisseur";


export interface Article {
  idArticle: number;
  reference: string;
  nom: string;
  description: string;
  prix: number;
  actif: boolean; 
  imageUrl:string;
fournisseur: Fournisseur;
}

