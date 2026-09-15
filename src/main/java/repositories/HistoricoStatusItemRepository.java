package repositories;

import entities.HistoricoStatusItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HistoricoStatusItemRepository extends JpaRepository<HistoricoStatusItem, Long> {
    List<HistoricoStatusItem>findByItemPedido_IdOrderByAlteradoEmAsc(Long itemPedidoId);
}
