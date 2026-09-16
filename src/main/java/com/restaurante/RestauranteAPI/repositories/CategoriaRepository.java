package com.restaurante.RestauranteAPI.repositories;

import com.restaurante.RestauranteAPI.entities.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
    boolean existsByNomeIgnoreCase(String nome);
}
