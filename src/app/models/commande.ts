import { Facture } from "./facture";
import { Fournisseur } from "./fournisseur";
import { LigneCommande } from "./LigneCommande ";


  export interface Commande {
  id?: number;
  numeroCommande: number;
  date: Date;
  montant_ht: number;
  montant_tva: number;
  montant_ttc: number;
  commentaire: string;
  fournisseur: Fournisseur;
 lignes: LigneCommande[];
  facture: Facture | null;
  articleId?: number;
  article?: { idArticle: number; nom: string; reference: string; prix: number };
  statut: 'EN_ATTENTE' | 'VALIDEE' | 'REJETEE' | 'LIVREE'| 'EN_COURS'|'ANNULEE'; // <- ajouter ceci

  }


