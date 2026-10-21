package com.restaurante.RestauranteAPI.controllers;

import com.restaurante.RestauranteAPI.dto.request.AlterarStatusItemRequest;
import com.restaurante.RestauranteAPI.dto.response.ItemPedidoResponse;
import com.restaurante.RestauranteAPI.services.ItemPedidoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/itens-pedido")
public class ItemPedidoController {

    private final ItemPedidoService itemPedidoService;

    public ItemPedidoController(ItemPedidoService itemPedidoService) {
        this.itemPedidoService = itemPedidoService;
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ItemPedidoResponse> alterarStatus(@PathVariable Long id,
                                                            @Valid @RequestBody AlterarStatusItemRequest request,
                                                            @AuthenticationPrincipal UserDetails usuario) {
        return ResponseEntity.ok(itemPedidoService.alterarStatus(id, request, usuario.getUsername()));
    }

}
