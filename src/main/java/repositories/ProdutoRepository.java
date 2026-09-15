package repositories;

import entities.Produto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {
    Page<Produto> findByCategoria_Id(Long categoriaId, Pageable pageable);
    Page<Produto> findByDisponivelTrue(Pageable pageable);
}
