package com.example.demo.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.model.Article;
import com.example.demo.model.Depot;
import com.example.demo.model.Stock;

public interface StockRepository extends JpaRepository<Stock, Long>{

	  // Trouver le stock d’un article dans un dépôt
    Optional<Stock> findByDepotAndArticle(Depot depot, Article article);
    // Tous les stocks d’un dépôt
    List<Stock> findByDepot(Depot depot);
    // Tous les stocks d’un article
    List<Stock> findByArticle(Article article);
    Optional<Stock> findByArticle_IdArticleAndDepot_IdDepot(Long articleId, Long depotId);
    List<Stock> findByActifTrue();
    @Query("SELECT s.quantite_disp FROM Stock s WHERE s.depot = :depot AND s.article = :article")
    int getQuantiteByDepotAndArticle(@Param("depot") Depot depot, @Param("article") Article article);
    @Query("SELECT s FROM Stock s WHERE s.quantite_disp <= s.stock_alerte AND s.actif = true")
    List<Stock> findStocksEnAlerte();
    
}
