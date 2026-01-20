import { Routes } from '@angular/router';
import { ConnexionComponent } from './Components/ComponentsUser/connexion/connexion.component';
import { InscriptionComponent } from './Components/ComponentsUser/inscription/inscription.component';
import { VehiculesUComponent } from './Components/ComponentsAdmin/vehicules-u/vehicules-u.component';
import { DemandeOrdreMissionComponent } from './Components/ComponentsUser/demande-ordre-mission/demande-ordre-mission.component';
import { DeclarationSinistreComponent } from './Components/ComponentsUser/declaration-sinistre/declaration-sinistre.component';
import { ProfilComponent } from './Components/ComponentsUser/profil/profil.component';
import { AuthGuard } from './Services/auth-guard.service';
import { VehiculesAComponent } from './Components/ComponentsAdmin/vehicules-a/vehicules-a.component';
import { AdminGuard } from './Services/admin-guard.service';
import { VehiculeEditComponent } from './Components/ComponentsAdmin/vehicule-edit/vehicule-edit.component';
import { TechnicienComponent } from './Components/ComponentsAdmin/technicien/technicien.component';
import { FournisseurComponent } from './Components/ComponentsAdmin/fournisseur/fournisseur.component';
import { StockComponent } from './Components/ComponentsAdmin/stock/stock.component';
import { ArticleComponent } from './Components/ComponentsAdmin/article/article.component';
import { ParkingComponent } from './Components/ComponentsAdmin/parking/parking.component';
import { PlaceComponent } from './Components/ComponentsAdmin/place/place.component';
import { EditPlaceComponent } from './Components/ComponentsAdmin/edit-place/edit-place.component';
import { EntreprisesComponent } from './Components/ComponentsAdmin/entreprise/entreprise.component';
import { UtilisateursComponent } from './Components/ComponentsAdmin/utilisateurs/utilisateurs.component';
import { MaintenancesComponent } from './Components/ComponentsAdmin/maintenances/maintenances.component';
import { AdminHomeComponent } from './Components/ComponentsAdmin/admin-home/admin-home.component';
import { HomeComponent } from './Components/ComponentsUser/home/home.component';
import { MaintenanceEditComponent } from './Components/ComponentsAdmin/maintenance-edit/maintenance-edit.component';
import { MaintenanceDetailComponentComponent } from './Components/ComponentsAdmin/maintenance-detail-component/maintenance-detail-component.component';
import { DepartementsComponent } from './Components/ComponentsAdmin/departements/departements.component';
import { AdminMissionComponent } from './Components/ComponentsAdmin/admin-mission/admin-mission.component';
import { MesMissionsComponent } from './Components/ComponentsUser/mes-missions/mes-missions.component';
import { SinistresComponent } from './Components/ComponentsAdmin/sinistres/sinistres.component';
import { DashboardComponent } from './Components/ComponentsAdmin/dashboard/dashboard.component';
import { AdminLayoutComponent } from './layouts/admin-layout/admin-layout.component';
import { DepotComponent } from './Components/ComponentsAdmin/depot/depot.component';
import { HistoriqueComponent } from './Components/ComponentsAdmin/historique/historique.component';
import { CommandesComponent } from './Components/ComponentsAdmin/commandes/commandes.component';
import { CarburantComponent } from './Components/ComponentsAdmin/carburant/carburant.component';
import { BonCarburantComponent } from './Components/ComponentsUser/bon-carburant/bon-carburant.component';
import { MesCarburantsComponent } from './Components/ComponentsUser/mes-carburants/mes-carburants.component';
import { SuivisComponent } from './Components/ComponentsAdmin/suivis/suivis.component';
import { StatistiquesComponent } from './Components/ComponentsAdmin/statistiques/statistiques.component';
import { SuiviVehiculesComponent } from './Components/ComponentsAdmin/suivi-vehicules/suivi-vehicules.component';

export const routes: Routes = [
  // Public
  { path: 'connexion', component: ConnexionComponent },
  { path: 'inscription', component: InscriptionComponent },
  { path: 'home', component: HomeComponent },
  // Utilisateur connecté
  { path: 'vehicules/:id', component: VehiculesUComponent, canActivate: [AuthGuard] },
  { path: 'demande-mission', component: DemandeOrdreMissionComponent, canActivate: [AuthGuard] },
  { path: 'declaration-sinistre', component: DeclarationSinistreComponent, canActivate: [AuthGuard] },
  { path: 'declaration-bonCarburant', component: BonCarburantComponent, canActivate: [AuthGuard] },
  { path: 'profil', component: ProfilComponent, canActivate: [AuthGuard] },
  { path: 'MesMissions', component: MesMissionsComponent, canActivate: [AuthGuard] },
  { path: 'MesCarburants', component: MesCarburantsComponent, canActivate: [AuthGuard] },



  {
    path: 'admin',
    component: AdminLayoutComponent,
    canActivate: [AdminGuard],
    children: [
      { path: 'vehicules', component: VehiculesAComponent },
      { path: 'vehicules/edit/:id', component: VehiculeEditComponent },
      { path: 'vehicules/:id', component: VehiculesUComponent },
      { path: 'techniciens', component: TechnicienComponent },
      { path: 'fournisseurs', component: FournisseurComponent },
      { path: 'stock', component: StockComponent },
      { path: 'stock/historiques', component: HistoriqueComponent },
      { path: 'depots', component: DepotComponent },
      { path: 'articles', component: ArticleComponent },
      { path: 'parkings', component: ParkingComponent },
      { path: 'places', component: PlaceComponent },
      { path: 'places/edit/:id', component: EditPlaceComponent },
      { path: 'departements', component: DepartementsComponent },
      { path: 'entreprises', component: EntreprisesComponent },
      { path: 'commandes', component: CommandesComponent },
      { path: 'carburants', component: CarburantComponent },
      { path: 'utilisateurs', component: UtilisateursComponent },
      { path: 'maintenances', component: MaintenancesComponent },
      { path: 'suivis', component: SuivisComponent },
      { path: 'maintenances/edit/:id', component: MaintenanceEditComponent },
      { path: 'maintenances/detail/:id', component: MaintenanceDetailComponentComponent },
      { path: 'home', component: HomeComponent },
      { path: 'admin-home', component: AdminHomeComponent },
      { path: 'admin-mission', component: AdminMissionComponent },
      { path: 'admin-sinistres', component: SinistresComponent },
      { path: 'admin-dashboard', component: DashboardComponent },
      { path: 'admin-statistiques', component: StatistiquesComponent },
      { path: 'admin-sante', component: SuiviVehiculesComponent },
      { path: 'maintenance', component: MaintenancesComponent },


      { path: '', redirectTo: 'vehicules', pathMatch: 'full' }
    ]
  },


  // Redirection fallback
  { path: '', redirectTo: 'home', pathMatch: 'full' }
];
