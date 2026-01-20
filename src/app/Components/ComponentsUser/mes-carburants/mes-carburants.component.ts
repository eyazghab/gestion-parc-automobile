import { Component, OnInit } from '@angular/core';
import { BonCarburant } from '../../../models/BonCarburant';
import { BonCarburantService } from '../../../Services/bon-carburant.service';
import { ToastrService } from 'ngx-toastr';
import { AuthService } from '../../../Services/auth.service';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { BonCarburantDto, EtatBon } from '../../../models/BonCarburantDto';
import jsPDF from 'jspdf';
import autoTable from 'jspdf-autotable';
import QRCode from 'qrcode';

@Component({
  selector: 'app-mes-carburants',
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './mes-carburants.component.html',
  styleUrl: './mes-carburants.component.css'
})
export class MesCarburantsComponent implements OnInit {
bons: BonCarburantDto[] = [];
  EtatBon = EtatBon; // ✅ expose l'enum pour le template

  constructor(
    private bonService: BonCarburantService,
    private authService: AuthService,
    private toastr: ToastrService
  ) {}

  ngOnInit(): void {
    const currentUser = this.authService.getCurrentUser();
    if (currentUser) {
      this.loadBons(currentUser.idUtilisateur);
    } else {
      this.toastr.error('Utilisateur non connecté');
    }
  }

  loadBons(utilisateurId: number) {
    this.bonService.getBonsByUtilisateur(utilisateurId).subscribe({
      next: (data) => this.bons = data,
      error: () => this.toastr.error('Erreur lors du chargement des bons')
    });
  }


async telechargerPDF(bon: BonCarburant): Promise<void> {
  const doc = new jsPDF();

  // 1️⃣ Titre principal
  doc.setFontSize(22);
  doc.setTextColor(40);
  doc.text('Bon de Carburant', 105, 20, { align: 'center' });

  // 2️⃣ Informations générales (utilisateur et véhicule)
  doc.setFontSize(12);
  doc.setTextColor(80);
  doc.text(`Utilisateur : ${bon.utilisateurNom || 'Non défini'}`, 14, 35);
  doc.text(`Véhicule : ${bon.vehicule?.immatriculation || 'Non défini'}`, 14, 42);

  // 3️⃣ Tableau des détails du bon carburant
  autoTable(doc, {
    startY: 55,
    theme: 'grid',
    headStyles: { fillColor: [22, 160, 133], halign: 'center' },
    bodyStyles: { halign: 'left' },
    head: [['Champ', 'Valeur']],
    body: [
      ['Date', bon.dateAchat ? new Date(bon.dateAchat).toLocaleDateString() : ''],
      ['Quantité (L)', bon.quantite?.toString() || '0'],
      ['Montant (DT)', bon.montant?.toString() || '0'],
      ['Carburant', bon.carburantNom || ''],
      ['État', bon.etat || 'EN_ATTENTE'],
    ],
  });

  // 4️⃣ Pied de page
  const pageHeight = doc.internal.pageSize.height;
  doc.setFontSize(10);
  doc.setTextColor(150);
  doc.text('Document généré automatiquement', 14, pageHeight - 20);

  // 5️⃣ Générer QR code avec JSON contenant toutes les infos
  const qrDataObj = {
    id: bon.id,
    utilisateur: bon.utilisateurNom || 'Non défini',
    vehicule: bon.vehicule?.immatriculation || 'Non défini',
    date: bon.dateAchat ? new Date(bon.dateAchat).toLocaleDateString() : '',
    quantite: bon.quantite || 0,
    montant: bon.montant || 0,
    carburant: bon.carburantNom || '',
    etat: bon.etat || 'EN_ATTENTE'
  };

  const qrData = JSON.stringify(qrDataObj);

  const qrCodeDataUrl = await QRCode.toDataURL(qrData, { margin: 1 });

  // Ajouter le QR code en bas à droite
  const qrSize = 40; // largeur et hauteur du QR
  const qrX = doc.internal.pageSize.width - qrSize - 14; // marge droite
  const qrY = pageHeight - qrSize - 14; // marge bas
  doc.addImage(qrCodeDataUrl, 'PNG', qrX, qrY, qrSize, qrSize);

  // 6️⃣ Téléchargement du PDF
  doc.save(`Bon_Carburant_${bon.id || 'inconnu'}.pdf`);
}



}