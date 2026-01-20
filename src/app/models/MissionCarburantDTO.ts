export interface MissionCarburantDtO {
  id?: number;
  destination?: string;
  dateDepart?: string; // ISO string
  dateRetour?: string; // ISO string
  distanceEstimee?: number;
  carburantNecessaire?: number;
   bonCarburantId: number;
}