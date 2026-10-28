package com.restaurante.RestauranteAPI.services;

import com.restaurante.RestauranteAPI.config.mappers.UsuarioMapper;
import com.restaurante.RestauranteAPI.dto.request.UsuarioRequest;
import com.restaurante.RestauranteAPI.entities.Usuario;
import com.restaurante.RestauranteAPI.enums.Users;
import com.restaurante.RestauranteAPI.exceptions.ConflictException;
import com.restaurante.RestauranteAPI.repositories.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    private static final Long ID = 1L;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private UsuarioService usuarioService;

    @BeforeEach
    void setUp() {
        UsuarioMapper usuarioMapper = Mappers.getMapper(UsuarioMapper.class);
        usuarioService = new UsuarioService(passwordEncoder, usuarioRepository, usuarioMapper);
    }

    @Nested
    @DisplayName("deletar (desativar)")
    class Deletar {

        @Test
        @DisplayName("recusa desativar o último ADMIN ativo")
        void recusaUltimoAdmin() {
            when(usuarioRepository.findById(ID)).thenReturn(Optional.of(usuario(Users.ADMIN, true)));
            when(usuarioRepository.countByRoleAndAtivoTrue(Users.ADMIN)).thenReturn(1L);

            assertThatThrownBy(() -> usuarioService.deletar(ID))
                    .isInstanceOf(ConflictException.class)
                    .hasMessageContaining("último administrador");

            verify(usuarioRepository, never()).save(any());
        }

        @Test
        @DisplayName("desativa um ADMIN quando existe outro ADMIN ativo")
        void desativaAdminComOutroAdmin() {
            Usuario admin = usuario(Users.ADMIN, true);
            when(usuarioRepository.findById(ID)).thenReturn(Optional.of(admin));
            when(usuarioRepository.countByRoleAndAtivoTrue(Users.ADMIN)).thenReturn(2L);

            usuarioService.deletar(ID);

            assertThat(admin.getAtivo()).isFalse();
            verify(usuarioRepository).save(admin);
        }

        @Test
        @DisplayName("desativa um GARCOM sem precisar contar os administradores")
        void desativaGarcom() {
            Usuario garcom = usuario(Users.GARCOM, true);
            when(usuarioRepository.findById(ID)).thenReturn(Optional.of(garcom));

            usuarioService.deletar(ID);

            assertThat(garcom.getAtivo()).isFalse();
            verify(usuarioRepository, never()).countByRoleAndAtivoTrue(any());
        }
    }

    @Nested
    @DisplayName("atualizar")
    class Atualizar {

        @Test
        @DisplayName("recusa rebaixar o último ADMIN ativo para GARCOM")
        void recusaRebaixarUltimoAdmin() {
            when(usuarioRepository.findById(ID)).thenReturn(Optional.of(usuario(Users.ADMIN, true)));
            when(usuarioRepository.countByRoleAndAtivoTrue(Users.ADMIN)).thenReturn(1L);

            assertThatThrownBy(() -> usuarioService.atualizar(ID, request(Users.GARCOM, true)))
                    .isInstanceOf(ConflictException.class)
                    .hasMessageContaining("último administrador");

            verify(usuarioRepository, never()).save(any());
        }

        @Test
        @DisplayName("recusa desativar o último ADMIN ativo pelo campo ativo = false")
        void recusaDesativarUltimoAdminPeloAtualizar() {
            when(usuarioRepository.findById(ID)).thenReturn(Optional.of(usuario(Users.ADMIN, true)));
            when(usuarioRepository.countByRoleAndAtivoTrue(Users.ADMIN)).thenReturn(1L);

            assertThatThrownBy(() -> usuarioService.atualizar(ID, request(Users.ADMIN, false)))
                    .isInstanceOf(ConflictException.class);

            verify(usuarioRepository, never()).save(any());
        }

        @Test
        @DisplayName("deixa o último ADMIN atualizar os próprios dados se continuar ADMIN e ativo")
        void atualizaUltimoAdminSemRebaixar() {
            Usuario admin = usuario(Users.ADMIN, true);
            when(usuarioRepository.findById(ID)).thenReturn(Optional.of(admin));
            when(usuarioRepository.findByEmailIgnoreCase("admin@restaurante.com")).thenReturn(Optional.of(admin));
            when(usuarioRepository.save(admin)).thenReturn(admin);

            UsuarioRequest request = request(Users.ADMIN, true);
            request.setNome("Novo nome");
            usuarioService.atualizar(ID, request);

            assertThat(admin.getNome()).isEqualTo("Novo nome");
            verify(usuarioRepository, never()).countByRoleAndAtivoTrue(any());
        }
    }

    // ---------- métodos auxiliares para montar os objetos dos testes ----------

    private Usuario usuario(Users role, boolean ativo) {
        Usuario usuario = new Usuario();
        usuario.setId(ID);
        usuario.setNome("Administrador");
        usuario.setEmail("admin@restaurante.com");
        usuario.setRole(role);
        usuario.setAtivo(ativo);
        return usuario;
    }

    private UsuarioRequest request(Users role, boolean ativo) {
        UsuarioRequest request = new UsuarioRequest();
        request.setNome("Administrador");
        request.setEmail("admin@restaurante.com");
        request.setSenha("senha-de-teste");
        request.setRole(role);
        request.setAtivo(ativo);
        return request;
    }
}
