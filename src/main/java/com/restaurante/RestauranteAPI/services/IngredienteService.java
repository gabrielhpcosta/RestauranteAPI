package com.restaurante.RestauranteAPI.services;

import com.restaurante.RestauranteAPI.config.mappers.IngredienteMapper;
import com.restaurante.RestauranteAPI.dto.request.IngredienteRequest;
import com.restaurante.RestauranteAPI.dto.response.IngredienteResponse;
import com.restaurante.RestauranteAPI.entities.Ingrediente;
import com.restaurante.RestauranteAPI.exceptions.ConflictException;
import com.restaurante.RestauranteAPI.exceptions.NotFound;
import com.restaurante.RestauranteAPI.repositories.IngredienteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IngredienteService {

    private final IngredienteRepository ingredienteRepository;
    private final IngredienteMapper ingredienteMapper;

    public IngredienteService(IngredienteRepository ingredienteRepository, IngredienteMapper ingredienteMapper) {
        this.ingredienteRepository = ingredienteRepository;
        this.ingredienteMapper = ingredienteMapper;
    }

    private static final Logger log = LoggerFactory.getLogger(IngredienteService.class);

    @Cacheable("ingredientes")
    public List<IngredienteResponse> buscarTodos() {
        log.info("Buscando todos os ingredientes");

        return ingredienteRepository.findAll().stream().map(ingredienteMapper::toResponse).toList();
    }

    @Cacheable(value = "ingrediente", key = "#id")
    public IngredienteResponse buscarPorId(Long id) {
        log.info("Buscando ingrediente pelo ID: {}", id);

        Ingrediente ingrediente = ingredienteRepository.findById(id)
                .orElseThrow(() -> new NotFound("Ingrediente não encontrado"));
        return ingredienteMapper.toResponse(ingrediente);
    }

    @CacheEvict(value = {"ingredientes", "ingrediente"}, allEntries = true)
    public IngredienteResponse salvar(IngredienteRequest request) {
        log.info("Salvando ingrediente: {}", request.getNome());

        if (ingredienteRepository.existsByNomeIgnoreCase(request.getNome())) {
            throw new ConflictException("Já existe um ingrediente com este nome");
        }

        Ingrediente ingrediente = ingredienteMapper.toEntity(request);
        ingrediente.setAtivo(true);

        Ingrediente ingredienteSalvo = ingredienteRepository.save(ingrediente);
        log.info("Ingrediente salvo com sucesso, ID {}", ingredienteSalvo.getId());

        return ingredienteMapper.toResponse(ingredienteSalvo);
    }

    @CacheEvict(value = {"ingredientes", "ingrediente"}, allEntries = true)
    public IngredienteResponse atualizar(Long id, IngredienteRequest request) {
        log.info("Atualizando ingrediente com o ID {}", id);

        Ingrediente ingrediente = ingredienteRepository.findById(id)
                .orElseThrow(() -> new NotFound("Ingrediente não encontrado"));

        if (!ingrediente.getNome().equalsIgnoreCase(request.getNome())
                && ingredienteRepository.existsByNomeIgnoreCase(request.getNome())) {
            throw new ConflictException("Já existe um ingrediente com esse nome");
        }

        ingrediente.setNome(request.getNome());
        ingrediente.setUnidadeMedida(request.getUnidadeMedida());
        ingrediente.setEstoqueMinimo(request.getEstoqueMinimo());

        Ingrediente ingredienteAtualizado = ingredienteRepository.save(ingrediente);

        return ingredienteMapper.toResponse(ingredienteAtualizado);
    }

    @CacheEvict(value = {"ingredientes", "ingrediente"}, allEntries = true)
    public void desativar(Long id) {
        log.info("Desativando ingrediente com o ID {}", id);

        Ingrediente ingrediente = ingredienteRepository.findById(id)
                .orElseThrow(() -> new NotFound("Ingrediente não encontrado"));

        ingrediente.setAtivo(false);
        ingredienteRepository.save(ingrediente);

    }
}
