package com.example.demo.service.imp;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.Repository.PiecesRechangeRepository;
import com.example.demo.model.PiecesRechange;
import com.example.demo.service.PieceRechangeService;

import java.util.List;

@Service
public class PieceRechangeServiceImp implements PieceRechangeService {

    @Autowired
    private PiecesRechangeRepository repository;

    @Override
    public PiecesRechange save(PiecesRechange piece) {
        return repository.save(piece);
    }

    @Override
    public PiecesRechange findById(Long id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public List<PiecesRechange> findAll() {
        return repository.findAll();
    }

    @Override
    public PiecesRechange update(Long id, PiecesRechange piece) {
        if (repository.existsById(id)) {
            piece.setId(id);
            return repository.save(piece);
        }
        return null;
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }
}
