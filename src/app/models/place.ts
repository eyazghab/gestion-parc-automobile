
import { Parking } from "./parking";
import { Vehicule } from "./vehicule";


export interface place {
  idPlaceParking?: number;
  nom: string;
  numeroPlace: number;
  estOccupee: boolean;

  // Relations
  parking?: Parking | null;   // Parking auquel cette place appartient (optionnel pour éviter erreurs si non chargé)
  vehicule?: Vehicule | null; // Véhicule qui occupe cette place (optionnel si la place est libre)
}
