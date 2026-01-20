import { CommonModule } from '@angular/common';
import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { NotificationService } from '../../Services/notification.service';
import { AuthService, Utilisateur } from '../../Services/auth.service';
import { ToastrService } from 'ngx-toastr';
import { interval, Subscription } from 'rxjs';
import { StockService } from '../../Services/stock.service';
interface Notification {
  id?: number;
  message: string;
  dateNotif: Date;
  lu?: boolean;
}
@Component({
  selector: 'app-admin-layout',
    imports: [CommonModule, FormsModule, ReactiveFormsModule, RouterModule],

  templateUrl: './admin-layout.component.html',
  styleUrl: './admin-layout.component.css'
})
export class AdminLayoutComponent implements OnInit ,OnDestroy{
  user: Utilisateur | null = null;
  notifications: any[] = []; // ✅ toujours un tableau vide par défaut
  notificationsNonLues: number = 0;
 alertesCount: number = 0;
  private refreshSub!: Subscription;
  private lastAlertesCount: number = 0;
  constructor(
    private authService: AuthService,
    private router: Router,
    private notifService: NotificationService,
     private toastr: ToastrService,
     private stockService: StockService
  ) {}

  ngOnInit(): void {
    this.authService.currentUser$.subscribe((user) => {
      this.user = user;
      if (this.user && this.user.idUtilisateur) {
        this.chargerNotifications();
      }
    });
        this.loadAlertes();
         // 🔄 Rafraîchissement toutes les 30 secondes
    this.refreshSub = interval(30000).subscribe(() => {
      this.loadAlertes();
    });

  }

  chargerNotifications() {
    this.notifService.getNotificationsPourUtilisateur(this.user!.idUtilisateur)
      .subscribe(data => {
        this.notifications = data || []; // ✅ assure que ce soit un tableau
        this.notificationsNonLues = this.notifications.filter(n => !n.lue).length;
      }, error => {
        console.error("Erreur lors du chargement des notifications :", error);
        this.notifications = [];
      });
  }

 markAsReadAll() {
  const unreadNotifications = this.notifications.filter(n => !n.lue && n.id != null);
  unreadNotifications.forEach(notif => {
    this.notifService.marquerCommeLue(notif.id).subscribe({
      next: () => {
        notif.lue = true;
        this.notificationsNonLues = this.notifications.filter(n => !n.lue).length;
      },
      error: err => console.error('Erreur marquage notification:', err)
    });
  });
}


ouvrirNotification(notif: any) {
  this.toastr.info(notif.message, 'Notification');

  if (!notif.lue) {
    this.notifService.marquerCommeLue(notif.id).subscribe(() => {
      notif.lue = true;
      this.notificationsNonLues--;
    });
  }

  console.log("Role actuel :", this.user?.role);

  if (this.user?.role === 'ADMIN') {
    this.router.navigate(['admin/admin-dashboard']);
  } else if (this.user?.role === 'USER') {
    this.router.navigate(['/MesMissions']);
  }

}

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/connexion']);
  }
  
   loadAlertes() {
    this.stockService.getAlertes().subscribe({
      next: data => {
        this.alertesCount = data.length;

        // 📢 Si nouvelles alertes => notifier l'admin
        if (this.alertesCount > this.lastAlertesCount) {
          this.showNotification(`⚠ ${this.alertesCount} articles en alerte stock !`);
        }

        this.lastAlertesCount = this.alertesCount;
      },
      error: err => console.error(err)
    });
  }

  showNotification(message: string) {
    // Utilise l'API native du navigateur pour notifier
    if ('Notification' in window) {
      Notification.requestPermission().then(permission => {
        if (permission === 'granted') {
          new Notification('Alerte Stock', {
            body: message,
            icon: 'assets/warning.png' // icône personnalisée
          });
        }
      });
    }
  }

  ngOnDestroy() {
    if (this.refreshSub) {
      this.refreshSub.unsubscribe();
    }
  }
}
