package com.example.demo.Controller;


import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.model.TypeCarrosserie;
import com.example.demo.service.TypeCarrosserieService;

@RestController
@RequestMapping("/api/type-carrosseries")
@CrossOrigin(origins = "*") // ajuste selon besoin
public class TypeCarrosserieController {

    private final TypeCarrosserieService service;

    public TypeCarrosserieController(TypeCarrosserieService service) {
        this.service = service;
    }

    @GetMapping
    public List<TypeCarrosserie> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<TypeCarrosserie> getById(@PathVariable Long id) {
        TypeCarrosserie typeCarrosserie = service.getById(id);
        if (typeCarrosserie == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(typeCarrosserie);
    }

    @PostMapping
    public TypeCarrosserie create(@RequestBody TypeCarrosserie typeCarrosserie) {
        return service.save(typeCarrosserie);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TypeCarrosserie> update(@PathVariable Long id, @RequestBody TypeCarrosserie typeCarrosserie) {
        TypeCarrosserie updated = service.update(id, typeCarrosserie);
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
