import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { SuiviService, AlerteVehiculeDTO } from '../../../Services/suivi.service';
import { ToastrService } from 'ngx-toastr';
import { SuiviDTO } from '../../../models/SuiviDTO';
interface AlerteVehiculeGroup {
  immatriculation: string;
  messages: string[];
  resolue: boolean; // si toutes les alertes sont résolues ou non
  selected?: boolean;
}
@Component({
  selector: 'app-suivis',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './suivis.component.html',
  styleUrls: ['./suivis.component.css']
})

export class SuivisComponent implements OnInit {
  suivis: SuiviDTO[] = [];
  alertesVehicules: (AlerteVehiculeDTO & { selected?: boolean })[] = [];
  filtreImmatriculation: string = '';
  loading: boolean = true;
  errorMessage: string = '';
  selectedSuiviId: number | null = null;
  loadingVehicules: { [id: number]: boolean } = {};
  selectAll: boolean = false;

  // --- CALENDRIER ---
  calendarWeeks: Date[][] = [];
  currentMonth!: number;
  currentYear!: number;
  currentMonthName: string = '';
  currentDate: Date = new Date(); // date du jour
  calendarSuivis: { immatriculation: string, date: Date }[] = [];

  constructor(private suiviService: SuiviService, private toastr: ToastrService) { }

  ngOnInit(): void {
    this.chargerTousLesSuivis();
    this.loadAlertesVehicules();

    const today = new Date();
    this.currentMonth = today.getMonth();
    this.currentYear = today.getFullYear();
    this.generateCalendar(this.currentMonth, this.currentYear);


  }

  // ==========================
  //   CALENDRIER
  // ==========================
  generateCalendar(month: number, year: number) {
    this.currentMonthName = new Date(year, month).toLocaleString('fr-FR', { month: 'long' });
    const firstDay = new Date(year, month, 1);
    const lastDay = new Date(year, month + 1, 0);
    const weeks: Date[][] = [];
    let currentWeek: Date[] = [];
    const startDayOfWeek = firstDay.getDay();

    // Jours vides avant le 1er
    for (let i = 0; i < startDayOfWeek; i++) {
      currentWeek.push(new Date(NaN));
    }

    for (let d = 1; d <= lastDay.getDate(); d++) {
      currentWeek.push(new Date(year, month, d));
      if (currentWeek.length === 7) {
        weeks.push(currentWeek);
        currentWeek = [];
      }
    }

    if (currentWeek.length > 0) {
      while (currentWeek.length < 7) currentWeek.push(new Date(NaN));
      weeks.push(currentWeek);
    }

    this.calendarWeeks = weeks;
  }

  changeMonth(offset: number) {
    this.currentMonth += offset;
    if (this.currentMonth < 0) {
      this.currentMonth = 11;
      this.currentYear--;
    } else if (this.currentMonth > 11) {
      this.currentMonth = 0;
      this.currentYear++;
    }
    this.generateCalendar(this.currentMonth, this.currentYear);
  }

  hasAlert(date: Date): boolean {
    if (!date || isNaN(date.getTime())) return false;
    return this.alertesVehicules.some(a => {
      if (!a.date) return false;
      const alertDate = new Date(a.date);
      return alertDate.getFullYear() === date.getFullYear() &&
        alertDate.getMonth() === date.getMonth() &&
        alertDate.getDate() === date.getDate();
    });
  }

  // Récupère les véhicules pour une date donnée
getVehiculesForDate(date: Date | undefined): string[] {
  if (!date || isNaN(date.getTime())) return [];
  return this.calendarSuivis
    .filter(suivi =>
      suivi.date.getFullYear() === date.getFullYear() &&
      suivi.date.getMonth() === date.getMonth() &&
      suivi.date.getDate() === date.getDate()
    )
    .map(suivi => suivi.immatriculation);
}

  // Vérifie si la date est invalide (pour les cases vides du calendrier)
  isInvalidDate(date: Date | undefined): boolean {
    return !date || isNaN(date.getTime());
  }

  // Vérifie si la date est aujourd'hui
  // Vérifie si la date est aujourd'hui
isToday(date: Date | undefined): boolean {
  if (!date || isNaN(date.getTime())) return false;
  const today = new Date();
  return date.getFullYear() === today.getFullYear() &&
         date.getMonth() === today.getMonth() &&
         date.getDate() === today.getDate();
}

  // ==========================
  //   SUIVI & ALERTES
  // ==========================

 chargerTousLesSuivis(): void {
  this.suiviService.getAllSuivis().subscribe({
    next: (data: SuiviDTO[]) => {
      this.suivis = data; // assignation des suivis pour le tableau

      // --- Préparer les données pour le calendrier ---
      this.calendarSuivis = this.suivis
        .filter(s => s.prochainSuivi !== undefined && s.prochainSuivi !== null) // seulement ceux qui ont une date
        .map(s => {
          let date: Date;

          // Si c'est déjà un objet Date, on le garde, sinon on convertit depuis string
          if (s.prochainSuivi instanceof Date) {
            date = s.prochainSuivi;
          } else {
            date = new Date(s.prochainSuivi!);
          }

          return {
            immatriculation: s.immatriculation,
            date
          };
        });

      this.loading = false;
    },
    error: (err) => {
      console.error(err);
      this.errorMessage = 'Erreur lors du chargement des suivis';
      this.loading = false;
    }
  });
}


  get alertesFiltres(): (AlerteVehiculeDTO & { selected?: boolean })[] {
    if (!this.filtreImmatriculation.trim()) return this.alertesVehicules;
    const filtre = this.filtreImmatriculation.toLowerCase();
    return this.alertesVehicules.filter(a => a.vehicule.toLowerCase().includes(filtre));
  }

  loadAlertesVehicules() {
    this.suiviService.getAlertesTousVehicules().subscribe({
      next: (data: AlerteVehiculeDTO[]) => {
        this.alertesVehicules = data.map(a => ({ ...a, selected: false }));
      },
      error: err => console.error('Erreur lors du chargement des alertes véhicules :', err)
    });
  }
// Vérifie si une date a un suivi programmé
hasSuivi(date: Date | undefined): boolean {
  if (!date || isNaN(date.getTime())) return false;
  return this.calendarSuivis.some(suivi => 
    suivi.date.getFullYear() === date.getFullYear() &&
    suivi.date.getMonth() === date.getMonth() &&
    suivi.date.getDate() === date.getDate()
  );
}
  marquerCommeResolue(alerte: AlerteVehiculeDTO) {
    this.suiviService.marquerAlerteCommeResolue(alerte.id).subscribe({
      next: () => {
        this.alertesVehicules = this.alertesVehicules.filter(a => a.id !== alerte.id);
        this.toastr.success(`Alerte pour ${alerte.vehicule} marquée comme Lu`, 'marquée comme Lu');
      },
      error: () => this.toastr.error('Impossible de marquer l’alerte comme résolue', 'Erreur')
    });
  }

  marquerToutesCommeResolues() {
    const selectionnees = this.alertesVehicules.filter(a => a.selected && !a.resolue);
    selectionnees.forEach(a => this.marquerCommeResolue(a));
  }

  toggleSelectAll() {
    this.alertesVehicules.forEach(a => {
      if (!a.resolue) a.selected = this.selectAll;
    });
  }

  openDetails(suivi: SuiviDTO) {
    this.selectedSuiviId = this.selectedSuiviId === suivi.idVehicule ? null : suivi.idVehicule;
  }
  openAlertDetails(date: Date) {
    if (!date || isNaN(date.getTime())) return;
    const vehicules = this.getVehiculesForDate(date);
    if (vehicules.length === 0) {
      this.toastr.info('Aucune alerte pour ce jour.', 'Info');
    } else {
      const liste = vehicules.join(', ');
      this.toastr.warning(`Alertes pour : ${liste}`, date.toLocaleDateString('fr-FR'));
    }
  }
  get alertesGroupees(): AlerteVehiculeGroup[] {
    const map = new Map<string, AlerteVehiculeGroup>();

    this.alertesFiltres.forEach(a => {
      if (!map.has(a.vehicule)) {
        map.set(a.vehicule, {
          immatriculation: a.vehicule,
          messages: [a.message],
          resolue: !!a.resolue,
          selected: a.selected || false
        });
      } else {
        const group = map.get(a.vehicule)!;
        group.messages.push(a.message);
        group.resolue = group.resolue && !!a.resolue; // toutes doivent être résolues pour true
      }
    });

    return Array.from(map.values());
  }
  marquerToutesCommeResoluesPourVehicule(group: AlerteVehiculeGroup) {
    const alertes = this.alertesVehicules.filter(a => a.vehicule === group.immatriculation && !a.resolue);
    alertes.forEach(a => this.marquerCommeResolue(a));
  }
  getSuiviClass(date: any): string {
    if (!date) return 'text-muted';
    const now = new Date();
    const suiviDate = new Date(date);
    const diff = (suiviDate.getTime() - now.getTime()) / (1000 * 60 * 60 * 24); // jours

    if (diff < 0) return 'text-danger fw-bold';      // suivi en retard
    if (diff <= 7) return 'text-warning fw-semibold'; // bientôt
    return 'text-success';                            // à jour
  }
}
