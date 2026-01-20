import { CommonModule } from '@angular/common';
import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { AuthService } from '../../../Services/auth.service';
import { NotificationService } from '../../../Services/notification.service';
import { interval, Subscription } from 'rxjs';
import { StockService } from '../../../Services/stock.service';
import { AlerteVehiculeDTO, SuiviService } from '../../../Services/suivi.service';
import { SinistreService } from '../../../Services/sinistre.service';
import { OrdreMissionService } from '../../../Services/ordre-mission.service';
import { BonCarburantService } from '../../../Services/bon-carburant.service';

interface Notification {
  id?: number;
  message: string;
  dateNotif: Date;
  lu?: boolean;
}

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css'],
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule, RouterModule],
})
export class DashboardComponent implements OnInit, OnDestroy {
  user: { idUtilisateur: number; prenom: string; nom: string; role: string } | null = null;
  notifications: Notification[] = [];
  notificationsNonLues = 0;

  private refreshSub!: Subscription;

  features = [
    { icon: '🧾', title: 'Missions', desc: 'Documents et dates', route: '/admin/admin-mission', badge: 0 },
    { icon: '🔧', title: 'Sinistres', desc: 'Historique des maintenances', route: '/admin/admin-sinistres', badge: 0 },
    { icon: '🏟️', title: 'Parking', desc: 'Zones et emplacements', route: '/admin/parkings' },
    { icon: '🅿️', title: 'Places', desc: 'Affectation des places de véhicule', route: '/admin/places' },
    { icon: '🏢', title: 'Départements', desc: 'Organisation par service', route: '/admin/departements' },
    { icon: '🚗', title: 'Véhicules', desc: 'Liste et gestion des véhicules', route: '/admin/vehicules' },
    { icon: '🔄', title: 'État des véhicules', desc: 'Suivi en temps réel  etat du sante de véhicule', route: '/admin/admin-sante' },
    { icon: '👥', title: 'Utilisateurs', desc: 'Gestion des utilisateurs', route: '/admin/utilisateurs' },
    { icon: '⛽', title: 'Carburants', desc: 'Suivi consommation', route: '/admin/carburants', badge: 0 },
    { icon: '📊', title: 'Statistiques', desc: 'Vue d’ensemble du parc', route: '/admin/admin-statistiques' },
    { icon: '📍', title: 'Suivi  maintenances des Véhicules', desc: 'Position et activité', route: '/admin/suivis', badge: 0 },
    { icon: '🛠️', title: 'Techniciens', desc: 'Liste et missions', route: '/admin/techniciens' },
    { icon: '🏢', title: 'Fournisseurs', desc: 'Liste et contrats', route: '/admin/fournisseurs' },
    { icon: '📦', title: 'Stock', desc: 'Suivi des pièces et produits', route: '/admin/stock', badge: 0 },
    { icon: '🛒', title: 'Articles', desc: 'Gestion des articles', route: '/admin/articles' },
/*     { icon: '🧾', title: 'Commandes', desc: 'Commandes de pièces et services', route: '/admin/commandes' },
 */  ];

  constructor(
    private authService: AuthService,
    private notificationService: NotificationService,
    private suiviService: SuiviService,
    private stockService: StockService,
    private sinistreService: SinistreService,
    private missionService: OrdreMissionService,
    private BoncarburantService: BonCarburantService,
    private router: Router,
    private toastr: ToastrService
  ) {}

  ngOnInit(): void {
    this.loadUser();
    this.refreshAllAlertes();

    // Rafraîchissement toutes les 30 secondes
    this.refreshSub = interval(30000).subscribe(() => this.refreshAllAlertes());
  }

  ngOnDestroy(): void {
    if (this.refreshSub) this.refreshSub.unsubscribe();
  }

  // --- UTILISATEUR ---
  loadUser() {
    const user = this.authService.getConnectedUser();
    if (user) {
      this.user = user;
      this.loadNotifications(user.idUtilisateur);
    } else {
      this.toastr.error('Utilisateur non connecté', 'Erreur');
    }
  }

  loadNotifications(userId: number) {
    this.notificationService.getNotificationsPourUtilisateur(userId).subscribe({
      next: (data: Notification[]) => {
        this.notifications = data;
        this.compterNotificationsNonLues();
      },
      error: () => this.toastr.error('Erreur lors du chargement des notifications', 'Erreur')
    });
  }

  compterNotificationsNonLues() {
    this.notificationsNonLues = this.notifications.filter(n => !n.lu).length;
  }

  markAsReadAll() {
    this.notifications.forEach(n => n.lu = true);
    this.compterNotificationsNonLues();
    this.toastr.info('Toutes les notifications ont été marquées comme lues', 'Info');
  }

  ouvrirNotification(notif: Notification) {
    notif.lu = true;
    this.compterNotificationsNonLues();
    this.toastr.success(notif.message, 'Notification');
  }

  logout() {
    this.authService.logout();
    this.toastr.success('Vous avez été déconnecté', 'Déconnexion');
    this.router.navigate(['/home']);
  }

  // --- ALERTES ---
  private refreshAllAlertes() {
    this.loadAlertesStock();
    this.loadAlertesSinistres();
    this.loadAlertesMissions();
    this.loadAlertesCarburants();
    this.loadAlertesSuiviVehicules();
  }

  private loadAlertesStock() {
    this.stockService.getAlertes().subscribe({
      next: data => this.setBadge('Stock', data.length),
      error: err => console.error(err)
    });
  }

  private loadAlertesSinistres() {
    this.sinistreService.getAlertes().subscribe({
      next: data => this.setBadge('Sinistres', data.length),
      error: err => console.error(err)
    });
  }

  private loadAlertesMissions() {
    this.missionService.getMissionsEnAttente().subscribe({
      next: data => this.setBadge('Missions', data.length),
      error: err => console.error(err)
    });
  }

  private loadAlertesCarburants() {
    this.BoncarburantService.getBonsEnAttente().subscribe({
      next: data => this.setBadge('Carburants', data.length),
      error: err => console.error(err)
    });
  }

  private loadAlertesSuiviVehicules() {
    this.suiviService.getAlertesTousVehicules().subscribe({
      next: (data: AlerteVehiculeDTO[]) => {
        const alertesNonResolues = data.filter(a => !a.resolue);
        this.setBadge('Suivi Véhicules', alertesNonResolues.length);
      },
      error: err => console.error(err)
    });
  }

  private setBadge(title: string, count: number) {
    const feature = this.features.find(f => f.title === title);
    if (feature) feature.badge = count;
  }
}
