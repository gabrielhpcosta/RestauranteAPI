package com.restaurante.RestauranteAPI.controllers;

import com.restaurante.RestauranteAPI.dto.request.CancelarComandaRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import com.restaurante.RestauranteAPI.dto.request.ComandaRequest;
import com.restaurante.RestauranteAPI.dto.response.ComandaResponse;
import com.restaurante.RestauranteAPI.services.ComandaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comandas")
public class ComandaController {

    private final ComandaService comandaService;

    public ComandaController(ComandaService comandaService) {
        this.comandaService = comandaService;
    }

    @GetMapping
    public ResponseEntity<List<ComandaResponse>> buscarTodos() {
        return ResponseEntity.ok(comandaService.buscarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ComandaResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(comandaService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<ComandaResponse> salvar(@Valid @RequestBody ComandaRequest request, @AuthenticationPrincipal UserDetails usuario) {
        ComandaResponse comandaSalva = comandaService.salvar(request, usuario.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(comandaSalva);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ComandaResponse> atualizar(@PathVariable Long id, @Valid @RequestBody ComandaRequest request) {
        return ResponseEntity.ok(comandaService.atualizar(id, request));
    }

    @PutMapping("/{id}/cancelar")
    public ResponseEntity<ComandaResponse> cancelar(@PathVariable Long id, @Valid @RequestBody CancelarComandaRequest request,
                                                    @AuthenticationPrincipal UserDetails usuario) {
        ComandaResponse comandaCancelada = comandaService.cancelar(id, request, usuario.getUsername());
        return ResponseEntity.ok(comandaCancelada);
    }

    @PutMapping("/{id}/fechar")
    public ResponseEntity<ComandaResponse> fechar(@PathVariable Long id) {
        return ResponseEntity.ok(comandaService.fechar(id));
    }
}
