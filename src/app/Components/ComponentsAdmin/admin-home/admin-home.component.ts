import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-admin-home',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './admin-home.component.html',
  styleUrls: ['./admin-home.component.css']
})
export class AdminHomeComponent {
  features = [
    { icon: '🧾', title: 'Missions', desc: 'Documents et dates', route: '/admin-mission' },
        { icon: '🔧', title: 'Sinistres', desc: 'Historique des maintenances', route: '/admin-sinistres' },
    { icon: '🏟️', title: 'Parking', desc: 'Zones et emplacements', route: '/parkings' },
    { icon: '🅿️', title: 'Places', desc: 'Affectation des places de véhicule', route: '/places' },
    { icon: '🏢', title: 'Départements', desc: 'Organisation par service', route: '/departements' },
    //{ icon: '🏛️', title: 'Entreprises', desc: 'Entités partenaires', route: '/entreprises' },
    { icon: '🚗', title: 'Véhicules', desc: 'Liste et gestion des véhicules', route: '/vehicules' },
    { icon: '👥', title: 'Utilisateurs', desc: 'Gestion des utilisateurs', route: '/utilisateurs' },
    { icon: '⛽', title: 'Carburants', desc: 'Suivi consommation', route: '/carburants' },
    { icon: '📊', title: 'Statistiques', desc: 'Vue d’ensemble du parc', route: '/statistiques' },
    { icon: '📍', title: 'Suivi Véhicules', desc: 'Position et activité', route: '/suivi' },
    { icon: '🛠️', title: 'Techniciens', desc: 'Liste et missions', route: '/techniciens' },
    { icon: '🏢', title: 'Fournisseurs', desc: 'Liste et contrats', route: '/fournisseurs' },
    { icon: '📦', title: 'Stock', desc: 'Suivi des pièces et produits', route: '/stock' },
    { icon: '🛒', title: 'Articles', desc: 'Gestion des articles', route: '/articles' },
    { icon: '📁', title: 'Documents', desc: 'Papiers & certificats des véhicules', route: '/documents' },
    { icon: '🧾', title: 'Commandes', desc: 'Commandes de pièces et services', route: '/commandes' },

  ];
}
