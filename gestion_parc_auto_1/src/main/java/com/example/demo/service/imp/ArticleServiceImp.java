package com.example.demo.service.imp;

import com.example.demo.Repository.ArticleRepository;
import com.example.demo.Repository.MouvementStockRepository;
import com.example.demo.Repository.StockRepository;
import com.example.demo.model.Article;
import com.example.demo.model.MouvemmentStock;
import com.example.demo.model.Stock;
import com.example.demo.service.ArticleService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ArticleServiceImp implements ArticleService {

	 private final ArticleRepository articleRepository;
	    private final StockRepository stockRepository;
	    private final MouvementStockRepository mouvementStockRepository;

	    public ArticleServiceImp(ArticleRepository articleRepository,
	                             StockRepository stockRepository,
	                             MouvementStockRepository mouvementStockRepository) {
	        this.articleRepository = articleRepository;
	        this.stockRepository = stockRepository;
	        this.mouvementStockRepository = mouvementStockRepository;
	    }
    
	        
	    
	    @Override
	    public Article saveArticle(Article article) {
	        // 1️⃣ Sauvegarder l'article
	        Article savedArticle = articleRepository.save(article);
	        
	        // 2️⃣ Créer un mouvement stock "vide" (stock et quantité à zéro ou null)
	        MouvemmentStock mouvement = new MouvemmentStock();
	        mouvement.setStock(null); // pas de stock pour l'instant
	        mouvement.setArticle(savedArticle);
	        mouvement.setQuantite(0); // quantité initiale à 0
	        mouvement.setType("ENTREE");
	        mouvement.setDate(new java.util.Date());

	        // 3️⃣ Sauvegarder le mouvement stock
	        mouvementStockRepository.save(mouvement);

	        return savedArticle;
	    }



    @Override
    public Article getArticleById(Long id) {
        return articleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Article introuvable"));
    }

    @Override
    public List<Article> getAllArticles() {
        return articleRepository.findAll();
    }

    @Override
    public void deleteArticle(Long id) {
        articleRepository.deleteById(id);
    }
    @Override
    public void desactiverArticle(Long id) {
        Article article = articleRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Article non trouvé"));
        article.setActif(false);
        articleRepository.save(article);
    }
	    }
