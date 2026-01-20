import { Entreprise } from "./entreprise";
import { Utilisateur } from "./utilisateur";


export interface Departement {
  id?: number;  // <<-- ici le "?" le rend optionnel
  nom: string;
  responsable: string;
nomEntreprise?: string;
  // Relations
  entreprise?: Entreprise;          // Entreprise à laquelle appartient ce département (optionnel)
  utilisateurs?: Utilisateur[];     // Liste des utilisateurs associés à ce département (optionnel)
}
