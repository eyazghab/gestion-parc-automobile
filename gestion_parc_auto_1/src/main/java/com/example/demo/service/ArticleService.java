package com.example.demo.service;


import com.example.demo.model.Article;
import java.util.List;

public interface ArticleService {
		Article saveArticle(Article article);
	    Article getArticleById(Long id);
	    List<Article> getAllArticles();
	    void deleteArticle(Long id);
	    void desactiverArticle(Long id);

}

