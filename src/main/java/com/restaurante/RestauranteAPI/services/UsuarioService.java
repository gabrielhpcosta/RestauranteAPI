package com.restaurante.RestauranteAPI.services;

import com.restaurante.RestauranteAPI.config.mappers.UsuarioMapper;
import com.restaurante.RestauranteAPI.dto.request.UsuarioRequest;
import com.restaurante.RestauranteAPI.dto.response.UsuarioResponse;
import com.restaurante.RestauranteAPI.entities.Usuario;
import com.restaurante.RestauranteAPI.enums.Users;
import com.restaurante.RestauranteAPI.exceptions.ConflictException;
import com.restaurante.RestauranteAPI.exceptions.NotFound;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.restaurante.RestauranteAPI.repositories.UsuarioRepository;

import java.util.List;
import java.util.Locale;

@Service
public class UsuarioService {

    private final PasswordEncoder passwordEncoder;
    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;

    public UsuarioService(PasswordEncoder passwordEncoder, UsuarioRepository usuarioRepository, UsuarioMapper usuarioMapper) {
        this.passwordEncoder = passwordEncoder;
        this.usuarioRepository = usuarioRepository;
        this.usuarioMapper = usuarioMapper;
    }

    private static final Logger log = LoggerFactory.getLogger(UsuarioService.class);

    @Cacheable("usuarios")
    public List<UsuarioResponse> buscarTodos(){
        log.info("Buscando todos os usuarios");
        return usuarioRepository.findAll().stream().map(usuarioMapper::toResponse).toList();
    }

    @Cacheable(value = "usuario", key = "#id")
    public UsuarioResponse buscarPorId(Long id) {
        log.info("Buscando usuario com ID {}", id);
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new NotFound("Usuário não encontrada"));

        return usuarioMapper.toResponse(usuario);
    }

    @CacheEvict(value = {"usuarios", "usuario"}, allEntries = true)
    public UsuarioResponse salvar(UsuarioRequest request) {
        log.info("Salvando usuario com email {}", request.getEmail());

        if (usuarioRepository.findByEmailIgnoreCase(request.getEmail().trim()).isPresent()) {
            throw new ConflictException("Esse e-mail já está cadastrado");
        }

        Usuario usuario = usuarioMapper.toEntity(request);
        usuario.setEmail(request.getEmail().trim().toLowerCase(Locale.ROOT));

        // Criptografa a senha antes de salvar
        usuario.setSenha(passwordEncoder.encode(request.getSenha()));

        Usuario usuarioSalvo = usuarioRepository.save(usuario);

        return usuarioMapper.toResponse(usuarioSalvo);
    }

    @CacheEvict(value = {"usuarios", "usuario"}, allEntries = true)
    public UsuarioResponse atualizar(Long id, UsuarioRequest request){
        log.info("Atualizando usuario com ID {}", id);
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new NotFound("Usuario não encontrado"));

        // confere antes de mexer no usuário: a regra olha o estado atual (ADMIN e ativo)
        boolean deixaDeSerAdminAtivo = request.getRole() != Users.ADMIN
                || !Boolean.TRUE.equals(request.getAtivo());
        if (deixaDeSerAdminAtivo) {
            garantirQueNaoEOUltimoAdmin(usuario);
        }

        usuario.setNome(request.getNome());
        String email = request.getEmail().trim().toLowerCase(Locale.ROOT);
        usuarioRepository.findByEmailIgnoreCase(email)
                .filter(outro -> !outro.getId().equals(id))
                .ifPresent(outro -> { throw new ConflictException("Esse e-mail já está cadastrado"); });
        usuario.setEmail(email);
        usuario.setSenha(passwordEncoder.encode(request.getSenha()));
        usuario.setRole(request.getRole());
        usuario.setAtivo(request.getAtivo());


        Usuario usuarioAtualizado = usuarioRepository.save(usuario);
        return usuarioMapper.toResponse(usuarioAtualizado);
    }

    @CacheEvict(value = {"usuarios", "usuario"}, allEntries = true)
    public void deletar(Long id){
        log.info("Deletando usuario com ID {}", id);
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new NotFound("Usuário não encontrado"));

        garantirQueNaoEOUltimoAdmin(usuario);

        usuario.setAtivo(false);

        usuarioRepository.save(usuario);
    }

    // sem nenhum ADMIN ativo, ninguém mais consegue entrar para cadastrar ou reativar usuários
    private void garantirQueNaoEOUltimoAdmin(Usuario usuario) {
        boolean eAdminAtivo = usuario.getRole() == Users.ADMIN && Boolean.TRUE.equals(usuario.getAtivo());

        if (eAdminAtivo && usuarioRepository.countByRoleAndAtivoTrue(Users.ADMIN) <= 1) {
            throw new ConflictException("Não é possível desativar ou rebaixar o último administrador ativo");
        }
    }
}
