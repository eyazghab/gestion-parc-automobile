import { Component, OnInit } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { AuthService, Utilisateur } from '../../../Services/auth.service';
import { NotificationService } from '../../../Services/notification.service';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css'],
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule, RouterModule]
})
export class NavbarComponent implements OnInit {
  user: Utilisateur | null = null;
  notifications: any[] = [];
  notificationsNonLues: number = 0;

  constructor(
    private authService: AuthService,
    private router: Router,
    private notifService: NotificationService,
    private toastr: ToastrService
  ) {}

  ngOnInit(): void {
    this.authService.currentUser$.subscribe((user) => {
      this.user = user;
      if (this.user?.idUtilisateur) {
        this.chargerNotifications();
      }
    });
  }

  // Charger toutes les notifications
  chargerNotifications() {
    this.notifService.getNotificationsPourUtilisateur(this.user!.idUtilisateur)
      .subscribe({
        next: (data) => {
          this.notifications = data || [];
          this.notificationsNonLues = this.notifications.filter(n => !n.lue).length;
        },
        error: (err) => {
          console.error("Erreur lors du chargement des notifications :", err);
          this.notifications = [];
        }
      });
  }

  // Marquer toutes les notifications comme lues
  markAsReadAll() {
    const nonLues = this.notifications.filter(n => !n.lue);
    nonLues.forEach(notif => {
      this.notifService.marquerCommeLue(notif.id).subscribe(() => {
        notif.lue = true;
        this.notificationsNonLues = Math.max(this.notificationsNonLues - 1, 0);
      });
    });
  }

  // Ouvrir une notification et rediriger selon le type
 ouvrirNotification(notif: any) {
  if (!notif.lue) {
    this.notifService.marquerCommeLue(notif.id).subscribe(() => notif.lue = true);
  }

  if (notif.message.toLowerCase().includes("carburant")) {
    this.router.navigate(['/MesCarburants']);
    return;
  }

  // fallback pour les missions ou autre
  if (notif.message.toLowerCase().includes("mission")) {
    this.router.navigate(['/MesMissions']);
  }
}


  logout(): void {
    this.authService.logout();
    this.router.navigate(['/connexion']);
  }
}
