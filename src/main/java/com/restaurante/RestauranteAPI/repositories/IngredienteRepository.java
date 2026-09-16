package com.restaurante.RestauranteAPI.repositories;

import com.restaurante.RestauranteAPI.entities.Ingrediente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IngredienteRepository extends JpaRepository<Ingrediente, Long> {

}
