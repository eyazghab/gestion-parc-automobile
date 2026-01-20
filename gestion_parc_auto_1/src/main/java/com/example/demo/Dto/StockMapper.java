package com.example.demo.Dto;

import com.example.demo.model.Stock;

public class StockMapper {


 public static StockDto toDTO(Stock stock) {
     StockDto dto = new StockDto();
     dto.setId(stock.getId());
     dto.setIot(stock.getIot());
     dto.setQuantite_disp(stock.getQuantite_disp());
     dto.setQuantite_reservee(stock.getQuantite_reservee());
     dto.setDate_dernier_entree(stock.getDate_dernier_entree());
     dto.setDate_dernier_sortie(stock.getDate_dernier_sortie());
     dto.setDate_dernier_inventaire(stock.getDate_dernier_inventaire());
     dto.setActif(dto.getActif());
     return dto;
 }

 public static Stock toEntity(StockDto dto) {
     Stock stock = new Stock();
     stock.setId(dto.getId());
     stock.setIot(dto.getIot());
     stock.setQuantite_disp(dto.getQuantite_disp());
     stock.setQuantite_reservee(dto.getQuantite_reservee());
     stock.setDate_dernier_entree(dto.getDate_dernier_entree());
     stock.setDate_dernier_sortie(dto.getDate_dernier_sortie());
     stock.setDate_dernier_inventaire(dto.getDate_dernier_inventaire());
     stock.setActif(dto.getActif());
     return stock;
 }
}
