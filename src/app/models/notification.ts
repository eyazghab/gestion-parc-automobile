

export interface Notification {
   id?: number;
  titre: string;
  message: string;
  dateNotif: string;
  lue: boolean;
  type?: 'MISSION' | 'BON_CARBURANT';
  destinataire: {
    idUtilisateur: number;
    // autres champs si nécessaires
  };
}
