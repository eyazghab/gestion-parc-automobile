import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { OrdreMissionService } from '../../../Services/ordre-mission.service';
import { ToastrService } from 'ngx-toastr';
import jsPDF from 'jspdf';
import autoTable from 'jspdf-autotable';
import QRCode from 'qrcode';
@Component({
  selector: 'app-mes-missions',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './mes-missions.component.html',
})
export class MesMissionsComponent implements OnInit {
  missions: any[] = [];
  missionData: any = null;
  erreur: string = '';


  constructor(
    private ordreMissionService: OrdreMissionService,
    private toastr: ToastrService
  ) {}

  ngOnInit(): void {
    this.chargerMissions();
  }

  // Charger toutes les missions de l'utilisateur connecté
chargerMissions(): void {
  const utilisateurConnecte = JSON.parse(localStorage.getItem('user') || '{}');
  const id = utilisateurConnecte?.idUtilisateur;

  if (!id) {
    this.toastr.warning("Utilisateur non connecté !");
    return;
  }

  this.ordreMissionService.getMesMissions(id).subscribe({
    next: (data) => {
      // Normalisation : filtrer uniquement les objets de mission valides
      this.missions = data
  .filter(m => typeof m === 'object')
  .map(m => ({
    ...m,
    id: m.id,
    showFormFin: false,
    dateDepart: m.dateDepart ? new Date(m.dateDepart) : null,
    dateRetour: m.dateRetour ? new Date(m.dateRetour) : null,
    vehicule: {
      idVehicule: m.vehicule?.idVehicule || null,
      immatriculation: m.vehicule?.immatriculation || 'Non défini'
    },
    utilisateur: m.utilisateur || {}
  }));

    },
    error: (err) => {
      console.error('Erreur chargement missions', err);
      this.toastr.error('Échec du chargement des missions');
    }
  });
}


  // Annuler une mission
  annulerMission(idMission: number): void {
    if (!confirm('Êtes-vous sûr de vouloir annuler cette mission ?')) {
      this.toastr.info("Annulation de la demande d'annulation", 'Info');
      return;
    }

    this.ordreMissionService.annulerOrdreMission(idMission).subscribe({
      next: () => {
        this.toastr.success('Mission annulée avec succès', 'Succès');
        this.chargerMissions(); // recharge après annulation
      },
      error: (err) => {
        console.error('Erreur annulation mission', err);
        this.toastr.error("Erreur lors de l'annulation", 'Erreur');
      }
    });
  }

  // Démarrer une mission
// Démarrer une mission
demarrerMission(id: number) {
  this.ordreMissionService.demarrerMission(id).subscribe({
    next: (res) => {
      this.chargerMissions(); // recharger la liste
      this.toastr.success('Mission démarrée avec succès !', 'Succès');
    },
    error: (err) => {
      console.error(err);
      this.toastr.error('Impossible de démarrer la mission.', 'Erreur');
    }
  });
}

// Terminer une mission
terminerMission(id: number) {
  this.ordreMissionService.terminerMission(id).subscribe({
    next: (res) => {
      this.chargerMissions(); // recharger la liste
      this.toastr.success('Mission terminée avec succès !', 'Succès');
    },
    error: (err) => {
      console.error(err);
      this.toastr.error('Impossible de terminer la mission.', 'Erreur');
    }
  });
}
  // Ouvrir formulaire pour finir une mission
  ouvrirFormulaireFinMission(mission: any): void {
    mission.showFormFin = true;
    mission.dateDepartReelle = mission.dateDepart ? this.formatDateTimeLocal(mission.dateDepart) : '';
    mission.dateRetourReelle = mission.dateRetour ? this.formatDateTimeLocal(mission.dateRetour) : '';
  }

  // Convertir Date en format compatible <input type="datetime-local">
  formatDateTimeLocal(date: any): string {
    const d = new Date(date);
    const pad = (n: number) => n.toString().padStart(2, '0');
    return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}T${pad(d.getHours())}:${pad(d.getMinutes())}`;
  }



  // Télécharger un PDF de la mission
async telechargerPDF(mission: any): Promise<void> {
  const doc = new jsPDF();

  // --- Titre ---
  doc.setFontSize(22);
  doc.setTextColor(40);
  doc.text('Ordre de Mission', doc.internal.pageSize.getWidth() / 2, 20, { align: 'center' });

  // --- Infos utilisateur et véhicule ---
  doc.setFontSize(12);
  doc.setTextColor(80);
  doc.text(`Nom et Prénom : ${mission.utilisateurNom || 'Non défini'}`, 14, 35);
  doc.text(`Véhicule : ${mission.vehiculeImmatriculation || 'Non défini'}`, 14, 42);

  // --- Tableau des détails ---
  autoTable(doc, {
    startY: 50,
    theme: 'grid',
    headStyles: { fillColor: [22, 160, 133], halign: 'center' },
    bodyStyles: { halign: 'left' },
    head: [['Champ', 'Valeur']],
    body: [
      ['Destination', mission.destination || ''],
      ['Motif', mission.motif || ''],
      ['Date départ', mission.dateDepart ? new Date(mission.dateDepart).toLocaleDateString() : ''],
      ['Date retour', mission.dateRetour ? new Date(mission.dateRetour).toLocaleDateString() : ''],
      ['État', mission.etat || ''],
    ],
  });

  // --- Pied de page ---
  const pageHeight = doc.internal.pageSize.height;
  doc.setFontSize(10);
  doc.setTextColor(150);
  doc.text('Document généré automatiquement', 14, pageHeight - 20);

  // --- Préparer les données JSON pour le QR code ---
  const missionData = {
    id: mission.id,
    nom: mission.utilisateurNom || 'Non défini',
    vehicule: mission.vehiculeImmatriculation || 'Non défini',
    destination: mission.destination || '',
    motif: mission.motif || '',
    dateDepart: mission.dateDepart ? new Date(mission.dateDepart).toLocaleDateString() : '',
    dateRetour: mission.dateRetour ? new Date(mission.dateRetour).toLocaleDateString() : '',
    etat: mission.etat || ''
  };

  const qrData = JSON.stringify(missionData);

  // --- Générer le QR code ---
  const qrCodeDataUrl = await QRCode.toDataURL(qrData, { margin: 1 });

  // --- Ajouter le QR code dans le PDF ---
  const qrSize = 40; 
  const qrX = doc.internal.pageSize.width - qrSize - 14; 
  const qrY = pageHeight - qrSize - 14; 
  doc.addImage(qrCodeDataUrl, 'PNG', qrX, qrY, qrSize, qrSize);

  // --- Télécharger le PDF ---
  doc.save(`Ordre_Mission_${mission.id}.pdf`);
}




}
