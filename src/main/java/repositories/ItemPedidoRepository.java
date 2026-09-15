package repositories;

import entities.ItemPedido;
import enums.StatusItemPedido;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemPedidoRepository extends JpaRepository<ItemPedido, Long> {
    List<ItemPedido> findByPedido_Id(Long pedidoId);

    List<ItemPedido> findByPedido_Comanda_Id(Long comandaId);

    Page<ItemPedido> findByStatus(
            StatusItemPedido status,
            Pageable pageable
    );
}
