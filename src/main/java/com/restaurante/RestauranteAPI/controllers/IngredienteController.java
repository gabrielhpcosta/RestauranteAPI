package com.restaurante.RestauranteAPI.controllers;

import com.restaurante.RestauranteAPI.dto.request.IngredienteRequest;
import com.restaurante.RestauranteAPI.dto.response.IngredienteResponse;
import com.restaurante.RestauranteAPI.services.IngredienteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ingredientes")
public class IngredienteController {

    private final IngredienteService ingredienteService;

    public IngredienteController(IngredienteService ingredienteService) {
        this.ingredienteService = ingredienteService;
    }

    @GetMapping
    public ResponseEntity<List<IngredienteResponse>> buscarTodas() {
        return ResponseEntity.ok(ingredienteService.buscarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<IngredienteResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ingredienteService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<IngredienteResponse> salvar(@Valid @RequestBody IngredienteRequest request) {
        IngredienteResponse ingredienteSalvo = ingredienteService.salvar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ingredienteSalvo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<IngredienteResponse> atualizar(@PathVariable Long id, @Valid @RequestBody IngredienteRequest request) {
        return ResponseEntity.ok(ingredienteService.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desativar(@PathVariable Long id) {
        ingredienteService.desativar(id);

        return ResponseEntity.noContent().build();
    }
}
