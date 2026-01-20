package com.example.demo.Controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.example.demo.model.PiecesRechange;
import com.example.demo.service.PieceRechangeService;

import java.util.List;

@RestController
@RequestMapping("/api/pieces")
@CrossOrigin("*")
public class PieceRechangeController {

    @Autowired
    private PieceRechangeService service;

    @PostMapping
    public PiecesRechange create(@RequestBody PiecesRechange piece) {
        return service.save(piece);
    }

    @GetMapping("/{id}")
    public PiecesRechange getById(@PathVariable Long id) {
        return service.findById(id);
    }

    @GetMapping
    public List<PiecesRechange> getAll() {
        return service.findAll();
    }

    @PutMapping("/{id}")
    public PiecesRechange update(@PathVariable Long id, @RequestBody PiecesRechange piece) {
        return service.update(id, piece);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
