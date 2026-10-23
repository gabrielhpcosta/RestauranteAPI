package com.restaurante.RestauranteAPI.controllers;

import com.restaurante.RestauranteAPI.dto.request.EstornarPagamentoRequest;
import com.restaurante.RestauranteAPI.dto.request.PagamentoRequest;
import com.restaurante.RestauranteAPI.dto.response.PagamentoResponse;
import com.restaurante.RestauranteAPI.dto.response.PedidoResponse;
import com.restaurante.RestauranteAPI.services.PagamentoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pagamentos")
public class PagamentoController {

    private final PagamentoService pagamentoService;

    public PagamentoController(PagamentoService pagamentoService) {
        this.pagamentoService = pagamentoService;
    }

    @GetMapping("/comanda/{comandaId}")
    public ResponseEntity<List<PagamentoResponse>> buscarPorComanda(@PathVariable Long comandaId) {
        return ResponseEntity.ok(pagamentoService.buscarPorComanda(comandaId));

    }

    @PostMapping
    public ResponseEntity<PagamentoResponse> registrar(@Valid @RequestBody PagamentoRequest request,
                                                       @AuthenticationPrincipal UserDetails usuario){
        PagamentoResponse pagamentoSalvo = pagamentoService.registrar(request, usuario.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(pagamentoSalvo);
    }

    @PutMapping("/{id}/estornar")
    public ResponseEntity<PagamentoResponse> estornar(@PathVariable Long id,
                                                      @Valid @RequestBody EstornarPagamentoRequest request,
                                                      @AuthenticationPrincipal UserDetails usuario) {
        PagamentoResponse pagamentoEstornado = pagamentoService.estornar(id, request, usuario.getUsername());
        return ResponseEntity.ok(pagamentoEstornado);
    }

}
