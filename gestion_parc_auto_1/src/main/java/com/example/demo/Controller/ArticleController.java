package com.example.demo.Controller;

import com.example.demo.Repository.ArticleRepository;
import com.example.demo.Repository.MouvementStockRepository;
import com.example.demo.model.Article;
import com.example.demo.model.MouvemmentStock;
import com.example.demo.service.ArticleService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/articles")
public class ArticleController {

    private final ArticleService articleService;

    @Autowired
    private ArticleRepository articleRepository;
    
    @Autowired
    private MouvementStockRepository mouvementStockRepository;

    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }

    // GET tous les articles
    @GetMapping
    public List<Article> getAllArticles() {
        return articleService.getAllArticles();
    }

    // GET par ID
    @GetMapping("/{id}")
    public ResponseEntity<Article> getArticleById(@PathVariable Long id) {
        Article article = articleService.getArticleById(id);
        return ResponseEntity.ok(article);
    }

    // Création
   @PostMapping
public ResponseEntity<Article> createArticle(@RequestBody Article article) {
    try {
        // 1️⃣ Sauvegarder l'article
        Article saved = articleService.saveArticle(article);

        // 2️⃣ Créer un mouvement stock pour la création
        MouvemmentStock mouvement = new MouvemmentStock();
        mouvement.setArticle(saved);
        mouvement.setQuantiteAvant(0); // Avant création, il n'y avait rien
        mouvement.setType("CREATION");
        mouvement.setMotif("Nouvel article créé");
        mouvement.setDate(new Date());

        // 3️⃣ Sauvegarder le mouvement
        mouvementStockRepository.save(mouvement);

        // 4️⃣ Retourner l'article créé
        return ResponseEntity.ok(saved);
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                             .body(null);
    }
}

    // Mise à jour
   @PutMapping("/{id}")
   public ResponseEntity<Article> updateArticle(@PathVariable Long id, @RequestBody Article article) {
       // 1️⃣ Récupérer l'article existant
       Article existingArticle = articleService.getArticleById(id);

       // 2️⃣ Vérifier si le prix a changé
    // Vérifier si le prix a changé (prix est un int)
       if (article.getPrix() != existingArticle.getPrix()) {
           int ancienPrix = existingArticle.getPrix();
           int nouveauPrix = article.getPrix();
           existingArticle.setPrix(nouveauPrix);

           // Créer un mouvement stock
           MouvemmentStock mouvement = new MouvemmentStock();
           mouvement.setArticle(existingArticle);
           mouvement.setQuantite(0); 
           mouvement.setType("MODIFICATION");
           mouvement.setMotif("Changement de prix");
           mouvement.setAncienPrix(ancienPrix);   // à condition que ces champs soient int
           mouvement.setNouveauPrix(nouveauPrix);
           mouvement.setDate(new Date());

           mouvementStockRepository.save(mouvement);
       }



       // 4️⃣ Mettre à jour les autres champs si présents
       if (article.getNom() != null) existingArticle.setNom(article.getNom());
       if (article.getDescription() != null) existingArticle.setDescription(article.getDescription());
       // if (article.getActif() != null) existingArticle.setActif(article.getActif());

       // 5️⃣ Sauvegarder l'article
       Article updated = articleService.saveArticle(existingArticle);

       return ResponseEntity.ok(updated);
   }



    // Suppression
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteArticle(@PathVariable Long id) {
        articleService.deleteArticle(id);
        return ResponseEntity.noContent().build();
    }

    // Désactivation
    @PatchMapping("/{id}/desactiver")
    public ResponseEntity<Void> desactiver(@PathVariable Long id) {
        // 1️⃣ Récupérer l'article existant
        Article article = articleService.getArticleById(id);

        // 2️⃣ Désactiver l'article
        article.setActif(false);
        articleService.saveArticle(article);

        // 3️⃣ Créer un mouvement stock pour la désactivation
        MouvemmentStock mouvement = new MouvemmentStock();
        mouvement.setArticle(article);
        mouvement.setQuantite(0); // pas de mouvement physique
        mouvement.setType("DESACTIVATION");
        mouvement.setMotif("Article désactivé");
        mouvement.setDate(new Date());

        mouvementStockRepository.save(mouvement);

        // 4️⃣ Retourner la réponse
        return ResponseEntity.noContent().build();
    }
    @PatchMapping("/activer/{id}")
    public ResponseEntity<Void> activer(@PathVariable Long id) {
        // 1️⃣ Récupérer l'article existant
        Article article = articleService.getArticleById(id);

        // 2️⃣ Activer l'article
        article.setActif(true);
        articleService.saveArticle(article);

        // 3️⃣ Créer un mouvement stock pour l'activation
        MouvemmentStock mouvement = new MouvemmentStock();
        mouvement.setArticle(article);
        mouvement.setQuantite(0); // pas de mouvement physique
        mouvement.setType("ACTIVATION");
        mouvement.setMotif("Article activé");
        mouvement.setDate(new Date());

        mouvementStockRepository.save(mouvement);

        // 4️⃣ Retourner la réponse
        return ResponseEntity.noContent().build();
    }

    // Upload image
    @PostMapping("/upload/{idArticle}")
    public ResponseEntity<String> uploadImage(@PathVariable Long idArticle,
                                              @RequestParam("file") MultipartFile file) {
        Article article = articleRepository.findById(idArticle)
                .orElseThrow(() -> new RuntimeException("Article introuvable"));

        try {
            // Nom du fichier
            String fileName = file.getOriginalFilename();

            // Dossier d'upload (même que dans WebConfig)
            String uploadDir = Paths.get("uploads").toAbsolutePath().toString();
            File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs(); // Crée le dossier si inexistant
            }

            // Enregistrement du fichier
            File uploadFile = new File(dir, fileName);
            file.transferTo(uploadFile);

            // URL publique
            String imageUrl = "http://localhost:8090/images/" + fileName;
            article.setImageUrl(imageUrl);
            articleRepository.save(article);

            return ResponseEntity.ok("Image uploadée avec succès : " + imageUrl);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur: " + e.getMessage());
        }
    }
}
