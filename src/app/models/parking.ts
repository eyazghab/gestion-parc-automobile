export interface Parking {
  id?: number;
  nomParking: string;
  adresse: string;
  capacite: number;
  estDisponible: boolean;
  vehicules?: any[];  // Liste des véhicules (tu peux remplacer par un modèle si tu en as un pour 'Vehicule')
  utilisateur?: any;   // Utilisateur assigné (tu peux remplacer par un modèle si tu en as un pour 'Utilisateur')
  placesParking?: any[];  // Liste des places de parking (remplacer par un modèle si nécessaire)
}
