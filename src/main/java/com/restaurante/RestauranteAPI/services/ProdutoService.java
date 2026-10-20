package com.restaurante.RestauranteAPI.services;

import com.restaurante.RestauranteAPI.config.mappers.ProdutoMapper;
import com.restaurante.RestauranteAPI.dto.request.ProdutoRequest;
import com.restaurante.RestauranteAPI.dto.response.ProdutoResponse;
import com.restaurante.RestauranteAPI.entities.Categoria;
import com.restaurante.RestauranteAPI.entities.Produto;
import com.restaurante.RestauranteAPI.exceptions.ConflictException;
import com.restaurante.RestauranteAPI.exceptions.NotFound;
import com.restaurante.RestauranteAPI.repositories.CategoriaRepository;
import com.restaurante.RestauranteAPI.repositories.ProdutoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProdutoService {

    private final ProdutoRepository produtoRepository;
    private final ProdutoMapper produtoMapper;
    private final CategoriaRepository categoriaRepository;

    public ProdutoService(ProdutoRepository produtoRepository, ProdutoMapper produtoMapper, CategoriaRepository categoriaRepository) {
        this.produtoRepository = produtoRepository;
        this.produtoMapper = produtoMapper;
        this.categoriaRepository = categoriaRepository;
    }

    private static final Logger log = LoggerFactory.getLogger(ProdutoService.class);

    @Cacheable("produtos")
    public List<ProdutoResponse> buscarTodos() {
        log.info("Buscando todos os produtos");

        return produtoRepository.findAll().stream().map(produtoMapper::toResponse).toList();
    }

    @Cacheable(value = "produto", key = "#id")
    public ProdutoResponse buscarPorId(Long id) {
        log.info("Buscando produto pelo ID {}", id);

        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new NotFound("Produto não encontrado"));
        return produtoMapper.toResponse(produto);
    }

    @CacheEvict(value = {"produtos", "produto"}, allEntries = true)
    public ProdutoResponse salvar(ProdutoRequest request) {
        log.info("Salvando produto {}", request.getNome());

        Categoria categoria = buscarCategoriaAtiva(request.getCategoriaId());

        Produto produto = produtoMapper.toEntity(request);
        produto.setCategoria(categoria);
        produto.setDisponivel(true);

        Produto produtoSalvo = produtoRepository.save(produto);
        log.info("Produto salvo com sucesso, ID {}", produtoSalvo.getId());

        return produtoMapper.toResponse(produtoSalvo);
    }

    @CacheEvict(value = {"produtos", "produto"}, allEntries = true)
    public ProdutoResponse atualizar(Long id, ProdutoRequest request) {
        log.info("Atualizando produto com o ID {}", id);

        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new NotFound("Produto não encontrado"));

        Categoria categoria = buscarCategoriaAtiva(request.getCategoriaId());

        produto.setCategoria(categoria);
        produto.setNome(request.getNome());
        produto.setDescricao(request.getDescricao());
        produto.setPreco(request.getPreco());
        produto.setExigePreparo(request.getExigePreparo());

        Produto produtoAtualizado = produtoRepository.save(produto);

        return produtoMapper.toResponse(produtoAtualizado);
    }

    @CacheEvict(value = {"produtos", "produto"}, allEntries = true)
    public void desativar(Long id) {
        log.info("Desativando produto com o ID {}", id);

        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new NotFound("Produto não encontrado"));

        produto.setDisponivel(false);
        produtoRepository.save(produto);
    }

    private Categoria buscarCategoriaAtiva(Long categoriaId) {
        Categoria categoria = categoriaRepository.findById(categoriaId)
                .orElseThrow(() -> new NotFound("Categoria não encontrada"));

        if (!categoria.getAtivo()) {
            throw new ConflictException("A categoria informada está desativada");
        }

        return categoria;
    }
}
