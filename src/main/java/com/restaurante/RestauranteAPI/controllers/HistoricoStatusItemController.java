package com.restaurante.RestauranteAPI.controllers;

import com.restaurante.RestauranteAPI.dto.response.HistoricoStatusItemResponse;
import com.restaurante.RestauranteAPI.services.HistoricoStatusItemService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/historicos-status-itens")
public class HistoricoStatusItemController {

    private final HistoricoStatusItemService historicoStatusItemService;

    public HistoricoStatusItemController(HistoricoStatusItemService historicoStatusItemService) {
        this.historicoStatusItemService = historicoStatusItemService;
    }

    @GetMapping("/item/{itemPedidoId}")
    public ResponseEntity<List<HistoricoStatusItemResponse>> buscarHistorico(@PathVariable Long itemPedidoId) {
        return ResponseEntity.ok(historicoStatusItemService.buscarHistorico(itemPedidoId));
    }

}
