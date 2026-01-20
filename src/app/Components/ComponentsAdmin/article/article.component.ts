import { Component, OnInit } from '@angular/core';
import { Article } from '../../../models/Article';
import { ArticleService } from '../../../Services/article.service';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { FournisseurService } from '../../../Services/fournisseur.service';

@Component({
  selector: 'app-article',
  imports: [CommonModule, FormsModule, ReactiveFormsModule, RouterModule],
  templateUrl: './article.component.html',
  styleUrls: ['./article.component.css']
})
export class ArticleComponent implements OnInit {
  articles: Article[] = [];
  fournisseurs: any[] = [];
  filteredArticles: Article[] = [];
  error = '';
  showAddForm = false;
  articleForm: FormGroup;
  searchReference = '';
  selectedFile: File | undefined;
  previewUrl: string | ArrayBuffer | null = null;
  editMode = false;
  currentEditId: number | null = null;

  constructor(
    private articleService: ArticleService,
    private fb: FormBuilder,
    private fournisseurService: FournisseurService
  ) {
    this.articleForm = this.fb.group({
      reference: ['', Validators.required],
      nom: ['', Validators.required],
      description: [''],
      prix: [0, [Validators.required, Validators.min(0)]],
      fournisseurId: [null, Validators.required]
    });
  }

  ngOnInit(): void {
    this.loadArticles();
    this.loadFournisseurs();
  }

  loadFournisseurs(): void {
    this.fournisseurService.getAll().subscribe({
      next: (data) => this.fournisseurs = data,
      error: (err) => console.error('Erreur chargement fournisseurs', err)
    });
  }

  loadArticles(): void {
    this.articleService.getArticles().subscribe({
      next: (data) => {
        this.articles = data;
        this.filteredArticles = [...data];
      },
      error: () => this.error = "Erreur lors du chargement des articles."
    });
  }

  toggleAddForm(): void {
    this.showAddForm = !this.showAddForm;
    if (!this.showAddForm) {
      this.articleForm.reset({ prix: 0 });
      this.error = '';
      this.selectedFile = undefined;
      this.previewUrl = null;
      this.editMode = false;
      this.currentEditId = null;
    }
  }

  onFileSelected(event: Event) {
    const fileInput = event.target as HTMLInputElement;
    if (fileInput.files && fileInput.files.length > 0) {
      this.selectedFile = fileInput.files[0];
      const reader = new FileReader();
      reader.onload = () => this.previewUrl = reader.result;
      reader.readAsDataURL(this.selectedFile);
    }
  }

  onAddSubmit(): void {
    if (this.articleForm.invalid) {
      this.error = 'Veuillez remplir les champs requis.';
      return;
    }

    const fournisseurId = +this.articleForm.get('fournisseurId')?.value;
    const selectedFournisseur = this.fournisseurs.find(f => f.idFournisseur === fournisseurId);

    if (!selectedFournisseur) {
      this.error = 'Fournisseur invalide.';
      return;
    }

    const payload: Partial<Article> = {
      reference: this.articleForm.get('reference')?.value,
      nom: this.articleForm.get('nom')?.value,
      description: this.articleForm.get('description')?.value,
      prix: this.articleForm.get('prix')?.value,
      fournisseur: selectedFournisseur,
      actif: true
    };

    if (this.editMode && this.currentEditId !== null) {
      // Mode édition
      this.articleService.updateArticle(this.currentEditId, payload).subscribe({
        next: (updatedArticle) => this.handleAfterSave(updatedArticle),
        error: () => this.error = "Erreur lors de la mise à jour de l'article."
      });
    } else {
      // Mode ajout
      this.articleService.createArticle(payload).subscribe({
        next: (newArticle) => this.handleAfterSave(newArticle, true),
        error: () => this.error = "Erreur lors de la création de l'article."
      });
    }
  }

  private handleAfterSave(article: Article, isNew: boolean = false) {
    const afterSave = () => {
      this.loadArticles();
      this.toggleAddForm();
    };

    if (this.selectedFile) {
      this.articleService.uploadArticleImage(article.idArticle!, this.selectedFile).subscribe({
        next: () => afterSave(),
        error: () => {
          this.error = isNew ? "Article créé, mais échec de l'upload de l'image." : "Article mis à jour, mais échec de l'upload de l'image.";
          afterSave();
        }
      });
    } else afterSave();
  }

  filterArticles(): void {
    const ref = this.searchReference.trim().toLowerCase();
    this.filteredArticles = ref ? this.articles.filter(a => a.reference.toLowerCase().includes(ref)) : [...this.articles];
  }

toggleActif(article: Article): void {
  if (article.actif) {
    this.articleService.desactiverArticle(article.idArticle!).subscribe({
      next: () => this.loadArticles(),
      error: (err) => console.error('Erreur lors de la désactivation', err)
    });
  } else {
    this.articleService.activerArticle(article.idArticle!).subscribe({
      next: () => this.loadArticles(),
      error: (err) => console.error('Erreur lors de l\'activation', err)
    });
  }
}



  editArticle(article: Article): void {
    this.editMode = true;
    this.currentEditId = article.idArticle;
    this.showAddForm = true;

    this.articleForm.patchValue({
      reference: article.reference,
      nom: article.nom,
      description: article.description,
      prix: article.prix,
      fournisseurId: article.fournisseur?.idFournisseur || null
    });

    this.previewUrl = article.imageUrl || null;
  }
 
}
