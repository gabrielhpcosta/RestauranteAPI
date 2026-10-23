package com.restaurante.RestauranteAPI.services;

import com.restaurante.RestauranteAPI.config.mappers.HistoricoStatusItemMapper;
import com.restaurante.RestauranteAPI.dto.response.HistoricoStatusItemResponse;
import com.restaurante.RestauranteAPI.exceptions.NotFound;
import com.restaurante.RestauranteAPI.repositories.HistoricoStatusItemRepository;
import com.restaurante.RestauranteAPI.repositories.ItemPedidoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HistoricoStatusItemService {

    private final HistoricoStatusItemRepository historicoStatusItemRepository;
    private final HistoricoStatusItemMapper historicoStatusItemMapper;
    private final ItemPedidoRepository itemPedidoRepository;

    public HistoricoStatusItemService(HistoricoStatusItemRepository historicoStatusItemRepository, HistoricoStatusItemMapper historicoStatusItemMapper, ItemPedidoRepository itemPedidoRepository) {
        this.historicoStatusItemRepository = historicoStatusItemRepository;
        this.historicoStatusItemMapper = historicoStatusItemMapper;
        this.itemPedidoRepository = itemPedidoRepository;
    }

    private static final Logger log = LoggerFactory.getLogger(HistoricoStatusItemService.class);

    public List<HistoricoStatusItemResponse> buscarHistorico(Long itemPedidoId) {
        log.info("Buscando histórico do pedido {}", itemPedidoId);

        if (!itemPedidoRepository.existsById(itemPedidoId)) {
            throw new NotFound("Item do pedido não encontrado");
        }

        return historicoStatusItemRepository.findByItemPedido_IdOrderByAlteradoEmAsc(itemPedidoId).stream().map(historicoStatusItemMapper::toResponse).toList();
    }
}
