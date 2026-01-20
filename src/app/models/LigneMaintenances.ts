import { Maintenance } from "./maintenances";

export interface LigneMaintenance {
  id: number;
  quantite: number;
  montant: number;
  dateCreation: string; // ou Date selon ta gestion du parsing
  document: string;
  cout: number;

  // relations
  maintenance: Maintenance | number; // souvent c'est l'id ou l'objet selon le backend
  //typeMaintenance: TypeMaintenance;
}
