package com.restaurante.RestauranteAPI.repositories;

import com.restaurante.RestauranteAPI.entities.Pagamento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PagamentoRepository extends JpaRepository<Pagamento, Long> {
    List<Pagamento> findByComanda_Id(Long comandaId);
    Optional<Pagamento> findByChaveIdempotencia(String chaveIdempotencia);
}
