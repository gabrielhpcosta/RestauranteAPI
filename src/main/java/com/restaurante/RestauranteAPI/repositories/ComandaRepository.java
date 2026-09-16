package com.restaurante.RestauranteAPI.repositories;

import com.restaurante.RestauranteAPI.entities.Comanda;
import com.restaurante.RestauranteAPI.enums.StatusComanda;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ComandaRepository extends JpaRepository<Comanda, Long> {
    boolean existsByMesa_IdAndStatus(
            Long mesaId,
            StatusComanda status
    );
    Page<Comanda> findByStatus(
            StatusComanda status,
            Pageable pageable
    );
}
