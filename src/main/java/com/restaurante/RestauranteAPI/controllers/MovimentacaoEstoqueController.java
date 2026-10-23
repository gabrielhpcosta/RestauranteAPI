package com.restaurante.RestauranteAPI.controllers;

import com.restaurante.RestauranteAPI.dto.request.MovimentacaoEstoqueRequest;
import com.restaurante.RestauranteAPI.dto.response.MovimentacaoEstoqueResponse;
import com.restaurante.RestauranteAPI.services.MovimentacaoEstoqueService;
import jakarta.validation.Valid;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/movimentacoes-estoque")
public class MovimentacaoEstoqueController {

    private final MovimentacaoEstoqueService movimentacaoEstoqueService;

    public MovimentacaoEstoqueController(MovimentacaoEstoqueService movimentacaoEstoqueService) {
        this.movimentacaoEstoqueService = movimentacaoEstoqueService;
    }

    @PostMapping
    public ResponseEntity<MovimentacaoEstoqueResponse> registrar(@Valid @RequestBody
                                                                     MovimentacaoEstoqueRequest request,
                                                                 @AuthenticationPrincipal
                                                                 UserDetails usuario) {
        MovimentacaoEstoqueResponse movimentacao = movimentacaoEstoqueService.registrar(request, usuario.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(movimentacao);
    }

    @GetMapping("/ingrediente/{ingredienteId}")
    public ResponseEntity<List<MovimentacaoEstoqueResponse>> buscarPorIngrediente(@PathVariable
                                                                                      Long ingredienteId) {
        return ResponseEntity.ok(movimentacaoEstoqueService.buscarPorIngrediente(ingredienteId));
    }
}
