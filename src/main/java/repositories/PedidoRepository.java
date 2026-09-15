package repositories;

import entities.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PedidoRepository extends JpaRepository {
    List<Pedido> findByComanda_IdOrderByCriadoEmAsc(Long comandaId);
}
