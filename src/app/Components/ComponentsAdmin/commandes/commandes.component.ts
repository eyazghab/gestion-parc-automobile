import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, FormArray, Validators, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { CommandeService } from '../../../Services/commande.service';
import { FournisseurService } from '../../../Services/fournisseur.service';
import { ArticleService } from '../../../Services/article.service';
import jsPDF from 'jspdf';
import autoTable from 'jspdf-autotable';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { MAT_DIALOG_DATA, MatDialog, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatButtonModule } from '@angular/material/button';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { FormsModule as NgFormsModule } from '@angular/forms';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-commandes',
  templateUrl: './commandes.component.html',
  styleUrls: ['./commandes.component.css'],
  standalone: true,
  imports: [
    CommonModule,
    NgFormsModule,
    FormsModule,
    ReactiveFormsModule,
    RouterModule,
    MatButtonModule,
    MatInputModule,
    MatSelectModule,
    MatDialogModule,
    MatSnackBarModule
  ],
})
export class CommandesComponent implements OnInit {

  commandes: any[] = [];
  fournisseurs: any[] = [];
  articles: any[] = [];
  articlesFournisseur: any[] = [];
  loading = true;
  showForm = false;
  commandeForm!: FormGroup;

  statutsCommande = ['EN_ATTENTE', 'VALIDEE', 'REJETEE', 'LIVREE'];

  constructor(
    private fb: FormBuilder,
    private commandeService: CommandeService,
    private fournisseurService: FournisseurService,
    private articleService: ArticleService,
    private snackBar: MatSnackBar,
    private dialog: MatDialog,
      private toastr: ToastrService

  ) {}

  ngOnInit(): void {
    this.initForm();
    this.loadFournisseurs();
    this.loadArticles();
    this.loadCommandes();
  }

  initForm() {
    this.commandeForm = this.fb.group({
      id: [0],
      commentaire: [''],
      fournisseur: this.fb.group({
        idFournisseur: [null, Validators.required],
        nomFournisseur: ['']
      }),
      lignes: this.fb.array([]),
    });
  }

  get lignes(): FormArray {
    return this.commandeForm.get('lignes') as FormArray;
  }

  addLigne() {
    this.lignes.push(this.fb.group({
      articleId: [null, Validators.required],
      reference: [''],
      quantite: [1, Validators.required],
      prixUnitaire: [0],
      total: [{ value: 0, disabled: true }]
    }));
  }

  removeLigne(index: number) {
    this.lignes.removeAt(index);
    this.updateTotals();
  }

  showFormForAdd() {
    this.commandeForm.reset();
    this.lignes.clear();
    this.addLigne();
    this.showForm = true;
    this.articlesFournisseur = [];
  }

  cancelForm() {
    this.showForm = false;
  }

  loadFournisseurs() {
    this.fournisseurService.getAll().subscribe(data => this.fournisseurs = data);
  }

  loadArticles() {
    this.articleService.getArticles().subscribe(data => this.articles = data);
  }

  loadCommandes() {
    this.loading = true;
    this.commandeService.getCommandes().subscribe({
      next: data => { this.commandes = data; this.loading = false; },
      error: () => this.loading = false
    });
  }

  onFournisseurChange() {
    const fournisseurId = this.commandeForm.get('fournisseur.idFournisseur')?.value;
    this.articlesFournisseur = fournisseurId
      ? this.articles.filter(a => a.fournisseur?.idFournisseur === fournisseurId)
      : [];
    this.lignes.controls.forEach(l => l.patchValue({ articleId: null, reference: '', prixUnitaire: 0, total: 0 }, { emitEvent: false }));
  }

  onArticleSelect(index: number) {
    const ligne = this.lignes.at(index);
    const articleId = +ligne.get('articleId')?.value;
    const article = this.articlesFournisseur.find(a => a.idArticle === articleId);
    if (article) {
      ligne.patchValue({
        reference: article.reference,
        prixUnitaire: article.prix,
        total: article.prix * (ligne.get('quantite')?.value || 1)
      }, { emitEvent: false });
    } else {
      ligne.patchValue({ reference: '', prixUnitaire: 0, total: 0 }, { emitEvent: false });
    }
    this.updateTotals();
  }

  updateTotals() {
    this.lignes.controls.forEach(l => {
      const quantite = l.get('quantite')?.value || 0;
      const article = this.articlesFournisseur.find(a => a.idArticle === l.get('articleId')?.value);
      const prix = article?.prix || l.get('prixUnitaire')?.value || 0;
      l.patchValue({ total: quantite * prix }, { emitEvent: false });
    });
  }

  getTotalLigne(ligne: FormGroup) {
    return (ligne.get('quantite')?.value || 0) * (ligne.get('prixUnitaire')?.value || 0);
  }

  getMontantHT() {
    return this.lignes.controls.reduce((sum, l) => sum + this.getTotalLigne(l as FormGroup), 0);
  }

  getMontantTVA() {
    return this.getMontantHT() * 0.19;
  }

  getMontantTTC() {
    return this.getMontantHT() + this.getMontantTVA();
  }

edit(commande: any) {
  if (!commande) {
    this.toastr.warning('Commande invalide.', 'Attention');
    return;
  }

  // Patch des champs principaux
  this.commandeForm.patchValue({
    id: commande.id || 0,
    commentaire: commande.commentaire || '',
    fournisseur: {
      idFournisseur: commande.fournisseur?.idFournisseur || null,
      nomFournisseur: commande.fournisseur?.nomFournisseur || ''
    }
  });

  // Filtrer les articles du fournisseur
  const fournisseurId = commande.fournisseur?.idFournisseur;
  this.articlesFournisseur = fournisseurId
    ? this.articles.filter(a => a.fournisseur?.idFournisseur === fournisseurId)
    : [];

  // Vider le FormArray
  this.lignes.clear();

  if (!commande.lignes || commande.lignes.length === 0) {
    this.toastr.info('Cette commande n’a pas de lignes.', 'Info');
    return;
  }

  // Fusionner les lignes identiques par articleId
  const ligneMap = new Map<number, any>();
  commande.lignes.forEach((l: any) => {
    const articleId = l.article?.idArticle;
    if (!articleId) return;

    const prixUnitaire = l.article?.prix ?? l.prixUnitaire ?? 0;
    const quantite = l.quantite ?? 1;

    if (ligneMap.has(articleId)) {
      const exist = ligneMap.get(articleId);
      exist.quantite += quantite;
      exist.total = exist.quantite * exist.prixUnitaire;
    } else {
      ligneMap.set(articleId, {
        articleId,
        reference: l.article?.reference || '',
        quantite,
        prixUnitaire,
        total: quantite * prixUnitaire
      });
    }
  });

  // Remplir le FormArray
  ligneMap.forEach(l => this.lignes.push(this.fb.group(l)));

  // Afficher le formulaire
  this.showForm = true;
  this.toastr.info('Mode édition activé', `Commande #${commande.numeroCommande}`);
}



save() {
  if (this.commandeForm.invalid) {
    this.commandeForm.markAllAsTouched();
    this.toastr.warning('Veuillez compléter les champs obligatoires.', 'Formulaire invalide');
    return;
  }

  const fournisseurId = Number(this.commandeForm.get('fournisseur.idFournisseur')?.value);
  const commentaire = this.commandeForm.get('commentaire')?.value || '';

  // Fusion des lignes identiques par articleId
  const ligneMap = new Map<number, number>();
  this.lignes.controls.forEach(ctrl => {
    const articleId = Number(ctrl.get('articleId')?.value);
    const quantite = Number(ctrl.get('quantite')?.value);
    if (articleId && quantite > 0) {
      ligneMap.set(articleId, (ligneMap.get(articleId) || 0) + quantite);
    }
  });

  const articleIds: number[] = Array.from(ligneMap.keys());
  const quantites: number[] = Array.from(ligneMap.values());

  // Validations
  if (!fournisseurId) {
    this.toastr.info('Veuillez sélectionner un fournisseur.');
    return;
  }
  if (articleIds.length === 0) {
    this.toastr.info('Ajoutez au moins une ligne de commande valide.');
    return;
  }

  const onSuccess = (res: any) => {
    if (res && (res.error || res.success === false)) {
      const msg = res.error?.message || res.message || 'Erreur durant l’enregistrement.';
      this.toastr.error(msg, 'Erreur');
      return;
    }
    this.toastr.success('Commande enregistrée avec succès ✅', 'Succès');
    this.showForm = false;
    this.loadCommandes && this.loadCommandes(); // rafraîchir la liste
  };

  const onError = (err: any) => {
    const msg =
      (typeof err === 'string' && err) ||
      err?.error?.message ||
      err?.message ||
      err?.statusText ||
      'Erreur serveur';
    this.toastr.error(msg, 'Erreur');
  };

  const id = Number(this.commandeForm.value?.id);
  if (id && id !== 0) {
    // Update
    this.commandeService
      .updateCommande(id, fournisseurId, articleIds, quantites, commentaire)
      .subscribe({ next: onSuccess, error: onError });
  } else {
    // Create
    this.commandeService
      .createWithQuantites(fournisseurId, articleIds, quantites, commentaire)
      .subscribe({ next: onSuccess, error: onError });
  }
}



  annulerCommande(commande: any) {
    if (commande.statut !== 'EN_COURS') {
      this.snackBar.open('Seules les commandes en attente peuvent être annulées.', 'Fermer', { duration: 3000 });
      return;
    }

    const dialogRef = this.dialog.open(AnnulerCommandeDialog, {
      width: '400px',
      data: { raison: '' }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.commandeService.annulerCommande(commande.id, result).subscribe({
          next: (updatedCommande) => {
            commande.statut = updatedCommande.statut;
            commande.actif = updatedCommande.actif;
            commande.justificationAnnulation = updatedCommande.justificationAnnulation;
            this.snackBar.open('Commande annulée avec succès', 'Fermer', { duration: 3000 });
          },
          error: () => this.snackBar.open('Erreur lors de l’annulation', 'Fermer', { duration: 3000 })
        });
      }
    });
  }
passerEnCours(commande: any) {
  if (!commande || !commande.id) {
    this.toastr.warning("Commande invalide.", "Attention");
    return;
  }

  this.commandeService.passerEnCours(commande.id).subscribe({
    next: (updatedCommande) => {
      // mettre à jour le statut dans la liste locale
      commande.statut = updatedCommande.statut;
      this.toastr.success(`Commande #${commande.numeroCommande} passée en cours ✅`, "Succès");
    },
    error: (err) => {
      const msg = err?.error?.message || "Erreur lors du passage en cours";
      this.toastr.error(msg, "Erreur");
    }
  });
}
livrerCommande(commande: any) {
  if (!commande?.id) {
    this.toastr.warning('Commande invalide', 'Attention');
    return;
  }

  this.commandeService.livrerCommande(commande.id).subscribe({
    next: (res) => {
      this.toastr.success(`Commande #${commande.numeroCommande} livrée ✅`, 'Succès');
      this.loadCommandes && this.loadCommandes(); // rafraîchir la liste
    },
    error: (err) => {
      const msg =
        (typeof err === 'string' && err) ||
        err?.error?.message ||
        err?.message ||
        err?.statusText ||
        'Erreur serveur';
      this.toastr.error(msg, 'Erreur');
    }
  });
}
telechargerPDF(commande: any) {
  if (!commande?.id) {
    this.toastr.warning('Commande invalide', 'Attention');
    return;
  }

  this.commandeService.telechargerPDF(commande.id).subscribe({
    next: (blob: Blob) => {
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = `Facture_Commande_${commande.numeroCommande}.pdf`;
      a.click();
      window.URL.revokeObjectURL(url);
      this.toastr.success('PDF téléchargé avec succès', 'Succès');
    },
    error: (err) => {
      console.error('Erreur téléchargement PDF:', err);
      const msg =
        (err?.error?.message) || 'Erreur lors du téléchargement du PDF';
      this.toastr.error(msg, 'Erreur');
    }
  });
}
deleteCommande(commande: any) {
  // Affiche une notification Toastr demandant confirmation
  const toast = this.toastr.info(
    `Voulez-vous supprimer la commande #${commande.id} ? Cliquez pour confirmer.`,
    'Confirmation',
    {
      timeOut: 0, // Ne disparaît pas automatiquement
      extendedTimeOut: 0,
      tapToDismiss: false,
      closeButton: true,
      enableHtml: true
    }
  );

  // Sur clic du toast, on supprime la commande
  toast.onTap?.subscribe(() => {
    this.commandeService.delete(commande.id).subscribe({
      next: () => {
        this.commandes = this.commandes.filter(c => c.id !== commande.id);
        this.toastr.success('Commande supprimée avec succès ✅');
      },
      error: (err) => {
        console.error(err);
        this.toastr.error('Erreur lors de la suppression ❌');
      },
    });
    // Fermer le toast de confirmation après action
    this.toastr.clear(toast.toastId);
  });
}

  private handleSuccess(res: any) { /*...*/ }
}

/*-----------------------------------------------
  DIALOG INLINE
-------------------------------------------------*/
@Component({
  selector: 'annuler-commande-dialog',
  template: `
    <h2 mat-dialog-title>Annuler la commande</h2>
    <mat-dialog-content>
      <mat-form-field class="w-100">
        <mat-label>Raison de l'annulation</mat-label>
        <textarea matInput [(ngModel)]="data.raison" placeholder="Entrez la raison..." rows="3"></textarea>
      </mat-form-field>
    </mat-dialog-content>
    <mat-dialog-actions align="end">
      <button mat-button (click)="onCancel()">Annuler</button>
      <button mat-raised-button color="warn" [disabled]="!data.raison" (click)="onConfirm()">Confirmer</button>
    </mat-dialog-actions>
  `,
  standalone: true,
  imports: [NgFormsModule, MatInputModule, MatButtonModule]
})
export class AnnulerCommandeDialog {
  constructor(
    public dialogRef: MatDialogRef<AnnulerCommandeDialog>,
    @Inject(MAT_DIALOG_DATA) public data: { raison: string }
  ) {}

  onCancel() {
    this.dialogRef.close();
  }

  onConfirm() {
    this.dialogRef.close(this.data.raison);
  }
}
