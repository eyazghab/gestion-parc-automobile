import { Vehicule } from "./vehicule";

export interface OrdreMissionDto {
 id: number;
  destination: string;
  motif: string;
  etat: string;
  utilisateurNom: string;        // ✅ doit correspondre au JSON
  vehiculeImmatriculation: string;
  dateDepart: string;
  dateRetour: string;
  // Champs optionnels côté Angular
  vehiculesDisponibles?: Vehicule[];
  selectedVehiculeId?: number;
  vehicule?: {
    idVehicule: number;
    immatriculation: string;
  };
}
