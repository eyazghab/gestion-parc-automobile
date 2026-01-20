package com.example.demo.service;



import java.util.List;

import com.example.demo.model.PiecesRechange;

public interface PieceRechangeService {

    PiecesRechange save(PiecesRechange piece);
    PiecesRechange findById(Long id);
    List<PiecesRechange> findAll();
    PiecesRechange update(Long id, PiecesRechange piece);
    void delete(Long id);
}
