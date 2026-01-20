package com.example.demo.service.imp;

import com.example.demo.Repository.ArticleRepository;
import com.example.demo.Repository.DepotRepository;
import com.example.demo.Repository.MouvementStockRepository;
import com.example.demo.Repository.StockRepository;
import com.example.demo.model.Article;
import com.example.demo.model.Depot;
import com.example.demo.model.MouvemmentStock;
import com.example.demo.model.Stock;
import com.example.demo.service.StockService;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class StockServiceImp implements StockService {

    private final StockRepository stockRepository;
    private final DepotRepository depotRepository;
    private final ArticleRepository articleRepository;
    private final MouvementStockRepository mouvementStockRepository;

    public StockServiceImp(StockRepository stockRepository, DepotRepository depotRepository,
                           ArticleRepository articleRepository, MouvementStockRepository mouvementStockRepository) {
        this.stockRepository = stockRepository;
        this.depotRepository = depotRepository;
        this.articleRepository = articleRepository;
        this.mouvementStockRepository = mouvementStockRepository;
    }

    @Override
    public List<Stock> getAllStocks() {
        return stockRepository.findAll();
    }

    @Transactional
    @Override
    public Stock entreeStock(Long depotId, Long articleId, int quantite, String motif) {

        if (quantite <= 0) {
            throw new IllegalArgumentException("La quantité doit être supérieure à zéro");
        }

        Depot depot = depotRepository.findById(depotId)
                .orElseThrow(() -> new RuntimeException("Dépôt introuvable"));
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new RuntimeException("Article introuvable"));

        Stock stock = stockRepository.findByDepotAndArticle(depot, article)
                .orElseGet(() -> {
                    Stock newStock = new Stock();
                    newStock.setDepot(depot);
                    newStock.setArticle(article);
                    newStock.setQuantite_disp(0);
                    newStock.setQuantite_reservee(0); // sécurisation
                    return stockRepository.save(newStock);
                });

        Date now = new Date();

        MouvemmentStock mouvement = new MouvemmentStock();
        mouvement.setDate(now);
        mouvement.setQuantite(quantite);
        mouvement.setType("ENTREE");
        mouvement.setQuantiteAvant(stock.getQuantite_disp());
        mouvement.setQuantiteApres(stock.getQuantite_disp() + quantite);
        mouvement.setMotif((motif != null && !motif.trim().isEmpty()) ? motif : "Non spécifié");
        mouvement.setCommantaire("Ajout automatique"); // correction orthographe
        mouvement.setStock(stock);
        mouvement.setArticle(article);

        System.out.println("Insertion mouvement : " + mouvement);
        mouvementStockRepository.save(mouvement);

        stock.setQuantite_disp(stock.getQuantite_disp() + quantite);
        stock.setDate_dernier_entree(now);

        System.out.println("Mise à jour stock : " + stock);
        return stockRepository.save(stock);
    }

    @Transactional
    @Override
    public Stock sortieStock(Long depotId, Long articleId, int quantite, String motif) {
        Depot depot = depotRepository.findById(depotId).orElseThrow(() -> new RuntimeException("Depot introuvable"));
        Article article = articleRepository.findById(articleId).orElseThrow(() -> new RuntimeException("Article introuvable"));

        Stock stock = stockRepository.findByDepotAndArticle(depot, article)
                .orElseThrow(() -> new RuntimeException("Stock introuvable"));

        int disponible = stock.getQuantite_disp() - stock.getQuantite_reservee();
        if (quantite > disponible) {
            throw new RuntimeException("Quantité insuffisante");
        }

        int avant = stock.getQuantite_disp();
        stock.setQuantite_disp(stock.getQuantite_disp() - quantite);
        stock.setDate_dernier_sortie(new Date());
        stockRepository.save(stock);

        MouvemmentStock mouvement = new MouvemmentStock();
        mouvement.setStock(stock);
        mouvement.setType("SORTIE");
        mouvement.setQuantite(quantite);
        mouvement.setQuantiteAvant(avant);
        mouvement.setQuantiteApres(stock.getQuantite_disp());
        mouvement.setMotif((motif != null && !motif.trim().isEmpty()) ? motif : "Non spécifié");
        mouvement.setArticle(article);
        mouvementStockRepository.save(mouvement);

        return stock;
    }

    @Transactional
    @Override
    public void transfererStock(Long depotSourceId, Long depotDestinationId, Long articleId, int quantite, String motif) {
        sortieStock(depotSourceId, articleId, quantite, "Transfert vers dépôt " + depotDestinationId + " - " + motif);
        entreeStock(depotDestinationId, articleId, quantite, "Transfert depuis dépôt " + depotSourceId + " - " + motif);
    }

    @Override
    public List<Stock> getStocksByDepot(Long depotId) {
        Depot depot = depotRepository.findById(depotId).orElseThrow(() -> new RuntimeException("Depot introuvable"));
        return stockRepository.findByDepot(depot);
    }
    @Transactional
    @Override
    public Stock desactiverStock(Long id) {
        Stock stock = stockRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Stock introuvable"));
        stock.setActif(false);
        return stockRepository.save(stock);
    }
    @Override
    public List<Stock> getStocksEnAlerte() {
        return stockRepository.findStocksEnAlerte();
    }
    @Override
    public Stock updateStock(Long id, Long articleId, Long depotId, int quantite, String motif, Integer stockMin, Integer stockAlerte) {
    Stock stock = stockRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Stock non trouvé"));

    Article article = articleRepository.findById(articleId)
            .orElseThrow(() -> new RuntimeException("Article non trouvé"));

    Depot depot = depotRepository.findById(depotId)
            .orElseThrow(() -> new RuntimeException("Depot non trouvé"));

    stock.setArticle(article);
    stock.setDepot(depot);
    stock.setQuantite_disp(quantite);
    stock.setMotif(motif);

    if (stockMin != null) stock.setStock_min(stockMin);
    if (stockAlerte != null) stock.setStock_alerte(stockAlerte);

    return stockRepository.save(stock);
}
}

