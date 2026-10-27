package com.restaurante.RestauranteAPI.repositories;

import com.restaurante.RestauranteAPI.entities.Comanda;
import com.restaurante.RestauranteAPI.enums.StatusComanda;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ComandaRepository extends JpaRepository<Comanda, Long> {
    boolean existsByMesa_IdAndStatus(
            Long mesaId,
            StatusComanda status
    );
    Page<Comanda> findByStatus(
            StatusComanda status,
            Pageable pageable
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from Comanda c where c.id = :id")
    Optional<Comanda> findByIdForUpdate(@Param("id") Long id);
}
