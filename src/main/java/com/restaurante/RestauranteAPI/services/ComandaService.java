package com.restaurante.RestauranteAPI.services;

import com.restaurante.RestauranteAPI.config.mappers.ComandaMapper;
import com.restaurante.RestauranteAPI.dto.response.ComandaResponse;
import com.restaurante.RestauranteAPI.repositories.ClienteRepository;
import com.restaurante.RestauranteAPI.repositories.ComandaRepository;
import com.restaurante.RestauranteAPI.repositories.MesaRepository;
import com.restaurante.RestauranteAPI.repositories.UsuarioRepository;
import org.springframework.cache.annotation.Cacheable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

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


}
