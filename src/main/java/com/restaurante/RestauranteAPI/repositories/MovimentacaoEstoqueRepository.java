package com.restaurante.RestauranteAPI.repositories;

import com.restaurante.RestauranteAPI.entities.MovimentacaoEstoque;
import com.restaurante.RestauranteAPI.enums.TipoMovimentacao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MovimentacaoEstoqueRepository extends JpaRepository<MovimentacaoEstoque, Long> {
    Page<MovimentacaoEstoque> findByIngrediente_Id(
            Long ingredienteId,
            Pageable pageable
    );

    List<MovimentacaoEstoque> findByItemPedido_IdAndTipo(
            Long itemPedidoId,
            TipoMovimentacao tipo
    );
}
