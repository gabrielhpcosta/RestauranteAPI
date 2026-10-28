package com.restaurante.RestauranteAPI.repositories;

import com.restaurante.RestauranteAPI.entities.Usuario;
import com.restaurante.RestauranteAPI.enums.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository <Usuario, Long> {
    Optional<Usuario> findByEmailIgnoreCase(String email);
    Optional<Usuario> findByEmail(String email);
    long countByRoleAndAtivoTrue(Users role);
}
