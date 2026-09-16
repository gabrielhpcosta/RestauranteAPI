package com.restaurante.RestauranteAPI.repositories;

import com.restaurante.RestauranteAPI.entities.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    Optional<Cliente> findByCpf(String cpf);
    Optional<Cliente> findByEmailIgnoreCase(String email);

}
