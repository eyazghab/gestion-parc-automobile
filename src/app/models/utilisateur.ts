import { Departement } from "./departement";

export interface Utilisateur {
  idUtilisateur: number;
  nom: string;
  prenom: string;
  email: string;
  motDePasse: string;
  role: string;
  actif: boolean; 
  telephone: string;
  cin: string;
  codeCnam:string;
  dateEmbauche: string; // ou Date selon le cas
  vehicules: any[]; // Liste des véhicules (si nécessaire)
  departement: Departement; // Département de l'utilisateur (si nécessaire)
}
