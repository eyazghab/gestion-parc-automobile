import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { ToastrService } from 'ngx-toastr';
import { Stock } from '../../../models/stock';
import { StockService } from '../../../Services/stock.service';
import { ArticleService } from '../../../Services/article.service';
import { DepotService } from '../../../Services/depot.service';
import { CommonModule } from '@angular/common';
import { interval, Subscription } from 'rxjs';

@Component({
  selector: 'app-stock',
  templateUrl: './stock.component.html',
  styleUrls: ['./stock.component.css'],
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
})
export class StockComponent implements OnInit {
  stockForm: FormGroup;
  sortieForm: FormGroup;
  transfertForm: FormGroup;
  editForm: FormGroup;

  // États d'affichage
  showSortieForm = false;
  showTransfertForm = false;
  showAddForm = false;
  showEditForm = false;

  // Données
  stocks: Stock[] = [];
  filteredStocks: Stock[] = [];
  articles: any[] = [];
  depots: any[] = [];
  alertes: Stock[] = [];

  // Messages
  success = '';
  error = '';

  // Stock sélectionné
  selectedStock: Stock | null = null;
  private alertesSub!: Subscription;
  constructor(
    private fb: FormBuilder,
    private stockService: StockService,
    private articleService: ArticleService,
    private depotService: DepotService,
    private toastr: ToastrService
  ) {
    this.stockForm = this.fb.group({
      articleId: ['', Validators.required],
      depotId: ['', Validators.required],
      quantite: [1, [Validators.required, Validators.min(1)]],
      motif: ['']
    });

    this.sortieForm = this.fb.group({
      quantite: [1, [Validators.required, Validators.min(1)]],
      motif: ['']
    });

    this.transfertForm = this.fb.group({
      depotDestinationId: ['', Validators.required],
      quantite: [1, [Validators.required, Validators.min(1)]],
      motif: ['']
    });

  this.editForm = this.fb.group({
  articleId: ['', Validators.required],
  depotId: ['', Validators.required],
  quantite: [1, [Validators.required, Validators.min(0)]],
  stockMin: [0, [Validators.min(0)]],
  stockAlerte: [0, [Validators.min(0)]],
  dateDernierEntree: [''],
  actif: [true],
  motif: ['']
});
  }
  ngOnInit(): void {
    this.loadStocks();
    this.loadArticles();
    this.loadDepots();

    this.stockService.getAlertes().subscribe({
      next: data => this.alertes = data,
      error: err => console.error(err)
    });
      // 🔄 Rafraîchissement toutes les 10 secondes
    this.alertesSub = interval(10000).subscribe(() => {
      this.loadAlertes();
    });

    // Charge initialement
    this.loadAlertes();
  }

  loadAlertes() {
    this.stockService.getAlertes().subscribe({
      next: data => this.alertes = data,
      error: err => console.error(err)
    });
  }

  ngOnDestroy(): void {
    if (this.alertesSub) this.alertesSub.unsubscribe();
  }
  // ----------- AFFICHAGE FORMULAIRES ----------
  toggleAddForm() {
    this.resetForms();
    this.showAddForm = true;
  }

  openSortieModal(stock: Stock) {
    this.resetForms();
    this.selectedStock = stock;
    this.showSortieForm = true;
    this.sortieForm.reset({ quantite: 1, motif: '' });
  }

  openTransfertModal(stock: Stock) {
    this.resetForms();
    this.selectedStock = stock;
    this.showTransfertForm = true;
    this.transfertForm.reset({ depotDestinationId: '', quantite: 1, motif: '' });
  }

openEditModal(stock: Stock) {
  this.selectedStock = stock;
  this.editForm.patchValue({
    articleId: stock.article.idArticle,
    depotId: stock.depot.idDepot,
    quantite: stock.quantite_disp,
    stockMin: stock.stock_min,
    stockAlerte: stock.stock_alerte,
    dateDernierEntree: stock.date_dernier_entree ? new Date(stock.date_dernier_entree).toISOString().substring(0,10) : '',
    actif: stock.actif,
    motif: stock.motif || ''
  });
  this.showEditForm = true;
}



  resetForms() {
    this.showAddForm = false;
    this.showSortieForm = false;
    this.showTransfertForm = false;
    this.showEditForm = false;
    this.selectedStock = null;
  }

  // ----------- CRUD STOCKS ----------
  loadStocks() {
    this.stockService.getStocks().subscribe({
      next: res => {
        this.stocks = res;
        this.filteredStocks = res;
      },
      error: () => this.toastr.error('Erreur lors du chargement des stocks')
    });
  }

  loadArticles() {
    this.articleService.getArticles().subscribe({
      next: res => this.articles = res,
      error: () => this.toastr.error('Erreur lors du chargement des articles')
    });
  }

  loadDepots() {
    this.depotService.getAllDepots().subscribe({
      next: res => this.depots = res,
      error: () => this.toastr.error('Erreur lors du chargement des dépôts')
    });
  }

  // ----------- AJOUT ----------
  onAddSubmit() {
    const { depotId, articleId, quantite, motif } = this.stockForm.value;
    this.stockService.entreeStock(depotId, articleId, quantite, motif).subscribe({
      next: () => {
        this.toastr.success('Stock ajouté avec succès');
        this.loadStocks();
        this.resetForms();
        this.stockForm.reset({ quantite: 1 });
      },
      error: err => this.toastr.error(err.error?.message || 'Erreur ajout stock')
    });
  }

  // ----------- SORTIE ----------
  onSortieSubmit() {
    if (!this.selectedStock) return;
    const { quantite, motif } = this.sortieForm.value;
    this.stockService.sortieStock(
      this.selectedStock.depot.idDepot!,
      this.selectedStock.article.idArticle,
      quantite,
      motif
    ).subscribe({
      next: () => {
        this.toastr.success('Sortie effectuée');
        this.resetForms();
        this.loadStocks();
      },
      error: err => this.toastr.error(err.error?.message || 'Erreur sortie stock')
    });
  }

  // ----------- TRANSFERT ----------
  onTransfertSubmit() {
    if (!this.selectedStock) return;
    const { depotDestinationId, quantite, motif } = this.transfertForm.value;
    this.stockService.transfererStock(
      this.selectedStock.depot.idDepot!,
      depotDestinationId,
      this.selectedStock.article.idArticle,
      quantite,
      motif
    ).subscribe({
      next: () => {
        this.toastr.success('Transfert effectué');
        this.resetForms();
        this.loadStocks();
      },
      error: err => this.toastr.error(err.error?.message || 'Erreur transfert stock')
    });
  }

  // ----------- EDITION ----------

 onEditSubmit() {
  if (!this.selectedStock) return;

  const { articleId, depotId, quantite, stockMin, stockAlerte, dateDernierEntree, actif, motif } = this.editForm.value;

  this.stockService.updateStock(
    this.selectedStock.id,
    articleId,
    depotId,
    quantite,
    stockMin,
    stockAlerte,
    dateDernierEntree,
    actif,
    motif
  ).subscribe({
    next: () => {
      this.toastr.success('Stock modifié avec succès');
      this.showEditForm = false;
      this.loadStocks();
    },
    error: (err) => this.toastr.error(err.error?.message || 'Erreur modification stock')
  });
}


  // ----------- ACTIVATION ----------
  toggleActif(stock: Stock) {
    this.stockService.toggleActif(stock.id, !stock.actif).subscribe({
      next: () => this.loadStocks(),
      error: err => this.toastr.error(err.error?.message || 'Erreur modification')
    });
  }

  desactiverStock(id: number) {
    this.stockService.desactiverStock(id).subscribe({
      next: () => {
        this.toastr.success("Stock désactivé");
        this.loadStocks();
      },
      error: err => this.toastr.error(err.error?.message || 'Erreur suppression')
    });
  }


}
