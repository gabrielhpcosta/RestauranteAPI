package repositories;

import entities.ReceitaItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReceitaItemRepository extends JpaRepository<ReceitaItem, Long> {
    List<ReceitaItem> findByProduto_Id(Long produtoId);
    boolean existsByProduto_IdAndIngrediente_Id(
            Long produtoId,
            Long ingredienteId
    );
}
