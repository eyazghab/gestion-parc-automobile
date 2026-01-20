export interface OrdreMission {
  id?:number;
  objetMission: string;
  destination: string;
  motif: string;
  dateDepart: string;
  dateRetour: string;
  utilisateur: {
    idUtilisateur: number;
    nom?: string;
  };
  vehicule?: {
    idVehicule: number;
    immatriculation?: string;
  };
}
