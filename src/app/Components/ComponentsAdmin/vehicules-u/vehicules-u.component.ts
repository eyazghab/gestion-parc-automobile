import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { VehiculeService } from '../../../Services/vehicule.service';
import jsPDF from 'jspdf';
import autoTable from 'jspdf-autotable';
import * as QRCode from 'qrcode';

@Component({
  selector: 'app-vehicules-u',
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './vehicules-u.component.html',
  styleUrl: './vehicules-u.component.css'
})
export class VehiculesUComponent implements OnInit {

  vehicule: any;
  vehiculeId!: number;

  constructor(
    private route: ActivatedRoute,
    private vehiculeService: VehiculeService
  ) { }

  ngOnInit(): void {
    this.vehiculeId = Number(this.route.snapshot.paramMap.get('id'));
    this.vehiculeService.getVehiculeById(this.vehiculeId).subscribe({
      next: (data) => this.vehicule = data,
      error: (err) => console.error('Erreur lors du chargement du véhicule :', err)
    });
  }

 async telechargerPDFVehicule(vehicule: any): Promise<void> {
  const doc = new jsPDF();

  // --- TITRE ---
  doc.setFontSize(20);
  doc.setTextColor(40);
  doc.text('Fiche Technique - Véhicule', 105, 15, { align: 'center' });

  let yPos = 25;

  // --- CARACTÉRISTIQUES ---
  doc.setFontSize(14);
  doc.setTextColor(0, 102, 204);
  doc.text('Caractéristiques', 14, yPos);
  yPos += 5;

  autoTable(doc, {
    startY: yPos,
    theme: 'grid',
    head: [['Champ', 'Valeur']],
    body: [
      ['Immatriculation', vehicule.immatriculation || ''],
      ['Châssis', vehicule.numeroChassis || ''],
      ['Date de circulation', vehicule.dateCircu ? new Date(vehicule.dateCircu).toLocaleDateString() : ''],
      ['Date d’acquisition', vehicule.date_acquisition ? new Date(vehicule.date_acquisition).toLocaleDateString() : ''],
      ['Kilométrage', vehicule.kilometrage_actuel ? vehicule.kilometrage_actuel + ' km' : ''],
      ['Année modèle', vehicule.anneeModel || ''],
      ['Carburant', vehicule.typeCarburant || ''],
      ['État', vehicule.etat || ''],
      ['Carrosserie', vehicule.typeCarrosserie?.type || ''],
    ],
  });

  // --- UTILISATEUR ---
  if (vehicule.utilisateur) {
    autoTable(doc, {
      startY: (doc as any).lastAutoTable.finalY + 10,
      theme: 'grid',
      head: [['Utilisateur', 'Valeur']],
      body: [
        ['Nom', `${vehicule.utilisateur.prenom || ''} ${vehicule.utilisateur.nom || ''}`],
        ['Email', vehicule.utilisateur.email || ''],
        ['Téléphone', vehicule.utilisateur.telephone || ''],
      ],
    });
  }

  // --- PARKING ---
  if (vehicule.parking) {
    autoTable(doc, {
      startY: (doc as any).lastAutoTable.finalY + 10,
      theme: 'grid',
      head: [['Parking', 'Valeur']],
      body: [
        ['Nom', vehicule.parking.nomParking || ''],
        ['Adresse', vehicule.parking.adresse || ''],
      ],
    });
  }

  // --- ASSURANCES ---
  if (vehicule.assurances?.length) {
    autoTable(doc, {
      startY: (doc as any).lastAutoTable.finalY + 10,
      theme: 'grid',
      head: [['Nom', 'Type', 'Police', 'Montant (€)']],
      body: vehicule.assurances.map((a: any) => [
        a.nomAssurance || '',
        a.typeAssurance || '',
        a.numeroPolice || '',
        a.montantPrime?.toFixed(2) || '',
      ]),
    });
  }

  // --- MAINTENANCES ---
  if (vehicule.maintenances?.length) {
    autoTable(doc, {
      startY: (doc as any).lastAutoTable.finalY + 10,
      theme: 'grid',
      head: [['Date', 'Description', 'Type', 'Coût (€)']],
      body: vehicule.maintenances.map((m: any) => [
        m.dateMaintenance ? new Date(m.dateMaintenance).toLocaleDateString() : '',
        m.description || '',
        m.typeMaintenance || '',
        m.coutTotal || '',
      ]),
    });
  }

  // --- DOCUMENTS ---
  if (vehicule.documents?.length) {
    autoTable(doc, {
      startY: (doc as any).lastAutoTable.finalY + 10,
      theme: 'grid',
      head: [['Documents']],
      body: vehicule.documents.map((d: any) => [d.type || '']),
    });
  }

  // --- SINISTRES ---
  if (vehicule.sinistres?.length) {
    autoTable(doc, {
      startY: (doc as any).lastAutoTable.finalY + 10,
      theme: 'grid',
      head: [['Type', 'Lieu', 'Dégâts']],
      body: vehicule.sinistres.map((s: any) => [
        s.typeSinistre || '',
        s.lieuSinistre || '',
        s.degats || '',
      ]),
    });
  }

  // --- SUIVIS ---
  if (vehicule.suivis?.length) {
    autoTable(doc, {
      startY: (doc as any).lastAutoTable.finalY + 10,
      theme: 'grid',
      head: [['Date', 'Fréquence (km)', 'Dernier suivi']],
      body: vehicule.suivis.map((s: any) => [
        s.date ? new Date(s.date).toLocaleDateString() : '',
        s.frequence || '',
        s.dateDerniersuivi ? new Date(s.dateDerniersuivi).toLocaleDateString() : '',
      ]),
    });
  }

  // --- AJOUT DU QR CODE ---
  const qrData = `Véhicule: ${vehicule.immatriculation || ''}\nChâssis: ${vehicule.numeroChassis || ''}`;

  try {
    const url = await QRCode.toDataURL(qrData, { width: 120 });
    const finalY = (doc as any).lastAutoTable ? (doc as any).lastAutoTable.finalY + 20 : 250;
    doc.addImage(url, 'PNG', 150, finalY, 40, 40);
  } catch (e) {
    console.error('Erreur génération QRCode', e);
  }

  // --- PIED DE PAGE ---
  const pageHeight = doc.internal.pageSize.height;
  doc.setFontSize(10);
  doc.setTextColor(150);
  doc.text('Document généré automatiquement', 14, pageHeight - 10);

  // --- SAUVEGARDE ---
  doc.save(`Fiche_Technique_${vehicule.immatriculation || 'vehicule'}.pdf`);
}




}