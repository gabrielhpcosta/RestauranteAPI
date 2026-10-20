package com.restaurante.RestauranteAPI.services;

import com.restaurante.RestauranteAPI.config.mappers.CategoriaMapper;
import com.restaurante.RestauranteAPI.dto.request.CategoriaRequest;
import com.restaurante.RestauranteAPI.dto.response.CategoriaResponse;
import com.restaurante.RestauranteAPI.entities.Categoria;
import com.restaurante.RestauranteAPI.exceptions.ConflictException;
import com.restaurante.RestauranteAPI.exceptions.NotFound;
import com.restaurante.RestauranteAPI.repositories.CategoriaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final CategoriaMapper categoriaMapper;

    public CategoriaService(CategoriaRepository categoriaRepository, CategoriaMapper categoriaMapper) {
        this.categoriaRepository = categoriaRepository;
        this.categoriaMapper = categoriaMapper;
    }

    private static final Logger log = LoggerFactory.getLogger(CategoriaService.class);

    @Cacheable("categorias")
    public List<CategoriaResponse> buscarTodas(){
        log.info("Buscando todas as categorias");

        return categoriaRepository.findAll().stream().map(categoriaMapper::toResponse).toList();
    }

    @Cacheable(value = "categoria", key = "#id")
    public CategoriaResponse buscarPorId(Long id) {
        log.info("Buscando categoria pelo ID: {}", id);

        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new NotFound("Categoria não encontrada"));
        return categoriaMapper.toResponse(categoria);
    }

    @CacheEvict(value = {"categorias", "categoria"}, allEntries = true)
    public CategoriaResponse salvar(CategoriaRequest request) {
        log.info("Salvando categoria: {}", request.getNome());

        if (categoriaRepository.existsByNomeIgnoreCase(request.getNome())) {
            throw new ConflictException("Já existe uma categoria com este nome");
        }

        Categoria categoria = categoriaMapper.toEntity(request);
        categoria.setAtivo(true);

        Categoria categoriaSalva = categoriaRepository.save(categoria);
        log.info("Categoria salva com sucesso, ID {}", categoriaSalva.getId());

        return categoriaMapper.toResponse(categoriaSalva);
    }

    @CacheEvict(value = {"categorias", "categoria"}, allEntries = true)
    public CategoriaResponse atualizar(Long id, CategoriaRequest request) {
        log.info("Atualizando categoria com o ID {}", id);

        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new NotFound("Categoria não encontrada"));

        if (!categoria.getNome().equalsIgnoreCase(request.getNome())
                && categoriaRepository.existsByNomeIgnoreCase(request.getNome())) {
            throw new ConflictException("Já existe uma categoria com esse nome");
        }

        categoria.setNome(request.getNome());

        Categoria categoriaAtualizada = categoriaRepository.save(categoria);

        return categoriaMapper.toResponse(categoriaAtualizada);
    }

    @CacheEvict(value = {"categorias", "categoria"}, allEntries = true)
    public void desativar(Long id) {
        log.info("Desativando categoria com o ID {}", id);

        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new NotFound("Categoria não encontrada"));

        categoria.setAtivo(false);
        categoriaRepository.save(categoria);
    }
}
