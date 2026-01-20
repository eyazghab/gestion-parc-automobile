import { Component, OnInit } from '@angular/core';
import { MouvementStockService, MouvementStock } from '../../../Services/mouvement-stock.service';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

@Component({
  selector: 'app-historique',
  templateUrl: './historique.component.html',
  styleUrls: ['./historique.component.css'],
  imports: [CommonModule,FormsModule,ReactiveFormsModule],

})
export class HistoriqueComponent implements OnInit {

  mouvements: MouvementStock[] = [];

  constructor(private mouvementService: MouvementStockService) { }

  ngOnInit(): void {
    this.getHistorique();
  }

  getHistorique() {
    this.mouvementService.getAllMouvements().subscribe({
      next: (data) => this.mouvements = data,
      error: (err) => console.error(err)
    });
  }
}
