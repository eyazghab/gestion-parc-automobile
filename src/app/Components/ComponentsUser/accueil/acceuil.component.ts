import { Component } from '@angular/core';

@Component({
  selector: 'app-acceuil',
  standalone: true,
  templateUrl: './acceuil.component.html',
  styleUrls: ['./acceuil.component.css'],
})

export class AccueilComponent {
  ngOnInit() {
    console.log("Page d'accueil chargée.");
  }
}
