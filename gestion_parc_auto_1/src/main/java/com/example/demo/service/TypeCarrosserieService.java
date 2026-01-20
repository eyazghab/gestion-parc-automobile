package com.example.demo.service;

import java.util.List;

import com.example.demo.model.TypeCarrosserie;

public interface TypeCarrosserieService {
	List<TypeCarrosserie> getAll();
    TypeCarrosserie getById(Long id);
    TypeCarrosserie save(TypeCarrosserie typeCarrosserie);
    TypeCarrosserie update(Long id, TypeCarrosserie typeCarrosserie);
    void delete(Long id);
}
