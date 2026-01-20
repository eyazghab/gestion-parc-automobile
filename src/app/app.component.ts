import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { NavbarComponent } from './Components/ComponentsUser/navbar/navbar.component';
import { FooterComponent } from './Components/ComponentsUser/footer/footer.component';
import { HomeComponent } from './Components/ComponentsUser/home/home.component';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet,NavbarComponent,FooterComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css',
  template: `
    <app-navbar></app-navbar>
    <router-outlet></router-outlet>
    <app-footer></app-footer>
    `,
})

export class AppComponent {
  title = 'Parc_Auto';
}
