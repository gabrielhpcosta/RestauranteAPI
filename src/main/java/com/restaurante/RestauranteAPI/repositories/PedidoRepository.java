package com.restaurante.RestauranteAPI.repositories;

import com.restaurante.RestauranteAPI.entities.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    List<Pedido> findByComanda_IdOrderByCriadoEmAsc(Long comandaId);
}
