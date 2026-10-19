package com.restaurante.RestauranteAPI.services;

import com.restaurante.RestauranteAPI.config.mappers.ComandaMapper;
import com.restaurante.RestauranteAPI.dto.request.ComandaRequest;
import com.restaurante.RestauranteAPI.dto.response.ComandaResponse;
import com.restaurante.RestauranteAPI.entities.Cliente;
import com.restaurante.RestauranteAPI.entities.Comanda;
import com.restaurante.RestauranteAPI.entities.Mesa;
import com.restaurante.RestauranteAPI.entities.Usuario;
import com.restaurante.RestauranteAPI.enums.StatusComanda;
import com.restaurante.RestauranteAPI.exceptions.ConflictException;
import com.restaurante.RestauranteAPI.exceptions.NotFound;
import com.restaurante.RestauranteAPI.repositories.ClienteRepository;
import com.restaurante.RestauranteAPI.repositories.ComandaRepository;
import com.restaurante.RestauranteAPI.repositories.MesaRepository;
import com.restaurante.RestauranteAPI.repositories.UsuarioRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ComandaService {

    private final ComandaRepository comandaRepository;
    private final ComandaMapper comandaMapper;
    private final MesaRepository mesaRepository;
    private final ClienteRepository clienteRepository;
    private final UsuarioRepository usuarioRepository;


    public ComandaService(ComandaRepository comandaRepository, ComandaMapper comandaMapper, MesaRepository mesaRepository, ClienteRepository clienteRepository, UsuarioRepository usuarioRepository) {
        this.comandaRepository = comandaRepository;
        this.comandaMapper = comandaMapper;
        this.mesaRepository = mesaRepository;
        this.clienteRepository = clienteRepository;
        this.usuarioRepository = usuarioRepository;
    }

    private static final Logger log = LoggerFactory.getLogger(ComandaService.class);

    @Cacheable("comanda")
    public List<ComandaResponse> buscarTodos(){
        log.info("Buscando todas as comandas");

        return comandaRepository.findAll().stream()
                .map(comandaMapper::toResponse)
                .toList();
    }

    @Cacheable(value = "cliente", key = "#id")
    public ComandaResponse buscarPorId(Long id){
        log.info("Buscando comanda por id, ID: {}", id);
        Comanda comanda = comandaRepository.findById(id)
                .orElseThrow(() -> new NotFound("Comanda não encontrada"));
        return comandaMapper.toResponse(comanda);
    }

    @Transactional
    @CacheEvict(value = {"comandas","comanda"}, allEntries = true)
    public ComandaResponse salvar(ComandaRequest request, String emailUsuarioAutenticado){
        log.info("Abrindo comanda para a mesa {}", request.getMesaId());

        Mesa mesa = mesaRepository.findById(request.getMesaId())
                .orElseThrow(() -> new NotFound("Mesa não encontrada"));

        if(!Boolean.TRUE.equals(mesa.getDisponivel())) {
            throw new ConflictException("A mesa está desativada");
        }

        if (comandaRepository.existsByMesa_IdAndStatus(mesa.getId(), StatusComanda.ABERTA)) {
            throw new ConflictException("A mesa já possui uma comanda aberta");
        }

        Cliente cliente = null;
        if (request.getClienteId() != null) {
            cliente = clienteRepository.findById(request.getClienteId())
                    .orElseThrow(() -> new NotFound("Cliente não encontrado"));


            if (!Boolean.TRUE.equals(cliente.getAtivo())) {
                throw new ConflictException("O cliente está inativo");
            }
        }

        Usuario usuario = usuarioRepository
                .findByEmailIgnoreCase(emailUsuarioAutenticado)
                .orElseThrow(() -> new NotFound("Usuário não encontrado"));

        if (!Boolean.TRUE.equals(usuario.getAtivo())) {
            throw new ConflictException("O usuário está inativo");
        }

        Comanda comanda = comandaMapper.toEntity(request);
        comanda.setMesa(mesa);
        comanda.setCliente(cliente);
        comanda.setAbertaPor(usuario);
        comanda.setStatus(StatusComanda.ABERTA);

        Comanda comandaSalva = comandaRepository.save(comanda);
        return comandaMapper.toResponse(comandaSalva);
    }

//    @CacheEvict(value = {"comandas", "comanda"}, allEntries = true)
//    public ComandaResponse atualizar(ComandaRequest request, Long id) {
//        log.info("Atualizando comanda ID: {}", id);
//    }

}
