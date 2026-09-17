package com.restaurante.RestauranteAPI.services;

import com.restaurante.RestauranteAPI.config.mappers.MesaMapper;
import com.restaurante.RestauranteAPI.dto.request.MesaRequest;
import com.restaurante.RestauranteAPI.dto.response.MesaResponse;
import com.restaurante.RestauranteAPI.entities.Mesa;
import com.restaurante.RestauranteAPI.enums.StatusComanda;
import com.restaurante.RestauranteAPI.exceptions.ConflictException;
import com.restaurante.RestauranteAPI.exceptions.NotFound;
import com.restaurante.RestauranteAPI.repositories.ComandaRepository;
import com.restaurante.RestauranteAPI.repositories.MesaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class MesaService {

    private final MesaRepository mesaRepository;
    private final MesaMapper mesaMapper;
    private final ComandaRepository comandaRepository;

    public MesaService(MesaRepository mesaRepository, MesaMapper mesaMapper, ComandaRepository comandaRepository) {
        this.mesaRepository = mesaRepository;
        this.mesaMapper = mesaMapper;
        this.comandaRepository = comandaRepository;
    }

    private static final Logger log = LoggerFactory.getLogger(MesaService.class);

    @Cacheable("mesas")
    public List<MesaResponse> buscarTodos(){
        log.info("Buscando todas as mesas");
        return mesaRepository.findAll().stream().map(mesaMapper::toResponse).toList();
    }

    @Cacheable(value = "mesa", key = "#id")
    public MesaResponse buscarPorId(Long id){
        log.info("Buscando mesa com o id {}", id);
        Mesa mesa = mesaRepository.findById(id)
                .orElseThrow(() -> new NotFound("Mesa não encontrada"));
        return mesaMapper.toResponse(mesa);
    }

    @CacheEvict(value = {"mesas", "mesa"}, allEntries = true)
    public MesaResponse salvar(MesaRequest request) {
        log.info("Salvando mesas");

        if (mesaRepository.existsByNumero(request.getNumero())) {
            throw new ConflictException("Já existe uma mesa com esse número");
        }

        Mesa mesa = mesaMapper.toEntity(request);
        mesa.setDisponivel(true);

        Mesa mesaSalva = mesaRepository.save(mesa);
        log.info("Mesa salva com sucesso, ID {}", mesaSalva.getId());

        return mesaMapper.toResponse(mesaSalva);
    }

    @CacheEvict(value = {"mesas", "mesa"}, allEntries = true)
    public MesaResponse atualizar(Long id ,MesaRequest request) {
        log.info("Atualizando mesas com o ID {}", id);

        Mesa mesa = mesaRepository.findById(id)
                .orElseThrow(() -> new NotFound("Mesa não encontrada"));

        if (!Objects.equals(mesa.getNumero(), request.getNumero())
            && mesaRepository.existsByNumero(request.getNumero())) {
            throw new ConflictException("Já existe uma mesa com esse número");
        }

        mesa.setNumero(request.getNumero());
        mesa.setCapacidade(request.getCapacidade());

        Mesa mesaAtualizada = mesaRepository.save(mesa);

        return mesaMapper.toResponse(mesaAtualizada);
    }

    @CacheEvict(value = {"mesas", "mesa"}, allEntries = true)
    public void deletar(Long id) {
        log.info("Desativando mesa com o ID {}", id);

        Mesa mesa = mesaRepository.findById(id)
                .orElseThrow(() -> new NotFound("Mesa não encontrada"));

        if (comandaRepository.existsByMesa_IdAndStatus(id, StatusComanda.ABERTA)) {
            throw new ConflictException(
                    "Não é possível desativar uma mesa com comanda aberta"
            );
        }

        mesa.setDisponivel(false);
        mesaRepository.save(mesa);
    }

}
