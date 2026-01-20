package com.example.demo.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Article;
@Repository
public interface ArticleRepository extends JpaRepository<Article, Long>{
	 // rechercher un article par référence
    Article findByReference(String reference);
    List<Article> findByActifTrue();

}
