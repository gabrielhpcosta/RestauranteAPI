package com.restaurante.RestauranteAPI.controllers;

import com.restaurante.RestauranteAPI.dto.request.AtualizarReceitaItemRequest;
import com.restaurante.RestauranteAPI.dto.request.ReceitaItemRequest;
import com.restaurante.RestauranteAPI.dto.response.ReceitaItemResponse;
import com.restaurante.RestauranteAPI.services.ReceitaItemService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/receita-itens")
public class ReceitaItemController {

    private final ReceitaItemService receitaItemService;

    public ReceitaItemController(ReceitaItemService receitaItemService) {
        this.receitaItemService = receitaItemService;
    }

    @GetMapping("/produto/{produtoId}")
    public ResponseEntity<List<ReceitaItemResponse>> buscarPorProduto(@PathVariable Long produtoId) {
        return ResponseEntity.ok(receitaItemService.buscarPorProduto(produtoId));
    }

    @PostMapping
    public ResponseEntity<ReceitaItemResponse> salvar(@Valid @RequestBody ReceitaItemRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(receitaItemService.salvar(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReceitaItemResponse> atualizar(@PathVariable Long id,
                                                         @Valid @RequestBody AtualizarReceitaItemRequest request) {
        return ResponseEntity.ok(receitaItemService.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        receitaItemService.deletar(id);
        return ResponseEntity.noContent().build();
    }

}
