package com.example.demo.service.imp;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.demo.Repository.TypeCarRepository;
import com.example.demo.model.TypeCarrosserie;
import com.example.demo.service.TypeCarrosserieService;

@Service
public class TypeCarrosserieServiceImpl implements TypeCarrosserieService {

    private final TypeCarRepository typeCarrosserieRepository;

    public TypeCarrosserieServiceImpl(TypeCarRepository typeCarrosserieRepository) {
        this.typeCarrosserieRepository = typeCarrosserieRepository;
    }

    @Override
    public List<TypeCarrosserie> getAll() {
        return typeCarrosserieRepository.findAll();
    }

    @Override
    public TypeCarrosserie getById(Long id) {
        Optional<TypeCarrosserie> optional = typeCarrosserieRepository.findById(id);
        return optional.orElse(null); // ou lever une exception si besoin
    }

    @Override
    public TypeCarrosserie save(TypeCarrosserie typeCarrosserie) {
        return typeCarrosserieRepository.save(typeCarrosserie);
    }

    @Override
    public TypeCarrosserie update(Long id, TypeCarrosserie typeCarrosserie) {
        return typeCarrosserieRepository.findById(id).map(existing -> {
            existing.setType(typeCarrosserie.getType());
            return typeCarrosserieRepository.save(existing);
        }).orElse(null);
    }

    @Override
    public void delete(Long id) {
        typeCarrosserieRepository.deleteById(id);
    }
}
