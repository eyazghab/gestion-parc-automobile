package com.example.demo.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.model.MouvemmentStock;
import com.example.demo.model.Stock;

public interface MouvementStockRepository extends JpaRepository<MouvemmentStock, Long>{
	  // Historique des mouvements pour un stock donné
    List<MouvemmentStock> findByStockOrderByDateDesc(Stock stock);
    List<MouvemmentStock> findAllByOrderByDateDesc();
    @Query("SELECT m FROM MouvemmentStock m WHERE m.article.idArticle = :articleId")
    List<MouvemmentStock> findByArticleId(@Param("articleId") Long articleId);


}
