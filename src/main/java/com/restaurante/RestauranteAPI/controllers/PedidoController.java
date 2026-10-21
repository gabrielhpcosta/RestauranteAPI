package com.restaurante.RestauranteAPI.controllers;

import com.restaurante.RestauranteAPI.dto.request.PedidoRequest;
import com.restaurante.RestauranteAPI.dto.response.PedidoResponse;
import com.restaurante.RestauranteAPI.services.PedidoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @GetMapping
    public ResponseEntity<List<PedidoResponse>> buscarTodos() {
        return ResponseEntity.ok(pedidoService.buscarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PedidoResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(pedidoService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<PedidoResponse> salvar(@Valid @RequestBody PedidoRequest request,
                                                 @AuthenticationPrincipal UserDetails usuario) {
        PedidoResponse pedidoSalvo = pedidoService.salvar(request, usuario.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(pedidoSalvo);
    }
}
