package com.restaurante.RestauranteAPI.repositories;

import com.restaurante.RestauranteAPI.entities.Mesa;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MesaRepository extends JpaRepository<Mesa, Long> {
    boolean existsByNumero(Integer numero);
}
