package com.restaurante.RestauranteAPI.controllers;

import com.restaurante.RestauranteAPI.dto.request.CategoriaRequest;
import com.restaurante.RestauranteAPI.dto.response.CategoriaResponse;
import com.restaurante.RestauranteAPI.services.CategoriaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categorias")
public class CategoriaController {

    private final CategoriaService categoriaService;

    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    @GetMapping
    public ResponseEntity<List<CategoriaResponse>> buscarTodas() {
        return ResponseEntity.ok(categoriaService.buscarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoriaResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(categoriaService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<CategoriaResponse> salvar(@Valid @RequestBody CategoriaRequest request) {
        CategoriaResponse categoriaSalva = categoriaService.salvar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(categoriaSalva);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoriaResponse> atualizar(@PathVariable Long id, @Valid @RequestBody CategoriaRequest request) {
        return ResponseEntity.ok(categoriaService.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desativar(@PathVariable Long id) {
        categoriaService.desativar(id);

        return ResponseEntity.noContent().build();
    }
}
