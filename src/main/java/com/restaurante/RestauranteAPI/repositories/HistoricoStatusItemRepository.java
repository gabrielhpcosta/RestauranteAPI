package com.restaurante.RestauranteAPI.repositories;

import com.restaurante.RestauranteAPI.entities.HistoricoStatusItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HistoricoStatusItemRepository extends JpaRepository<HistoricoStatusItem, Long> {
    List<HistoricoStatusItem>findByItemPedido_IdOrderByAlteradoEmAsc(Long itemPedidoId);
}
