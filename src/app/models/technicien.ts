export interface Technicien {
  idTechnicien: number;
  nom: string;
  prenom: string;
  emailTech: string;
  telephoneTech: string;
  type: TypeTechnicien; 
  specialite?: string;
  maintenances?: any[];// Liste des maintenances effectuées par ce technicien
  actif: boolean; 
}
export enum TypeTechnicien {
  INTERNE = 'INTERNE',
  EXTERNE = 'EXTERNE'
}