package com.restaurante.RestauranteAPI.services;

import com.restaurante.RestauranteAPI.config.mappers.ReceitaItemMapper;
import com.restaurante.RestauranteAPI.dto.request.AtualizarReceitaItemRequest;
import com.restaurante.RestauranteAPI.dto.request.ReceitaItemRequest;
import com.restaurante.RestauranteAPI.dto.response.ReceitaItemResponse;
import com.restaurante.RestauranteAPI.entities.Ingrediente;
import com.restaurante.RestauranteAPI.entities.Produto;
import com.restaurante.RestauranteAPI.entities.ReceitaItem;
import com.restaurante.RestauranteAPI.exceptions.ConflictException;
import com.restaurante.RestauranteAPI.exceptions.NotFound;
import com.restaurante.RestauranteAPI.repositories.IngredienteRepository;
import com.restaurante.RestauranteAPI.repositories.ProdutoRepository;
import com.restaurante.RestauranteAPI.repositories.ReceitaItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ReceitaItemService {

    private final ReceitaItemRepository receitaItemRepository;
    private final ProdutoRepository produtoRepository;
    private final IngredienteRepository ingredienteRepository;
    private final ReceitaItemMapper receitaItemMapper;


    public ReceitaItemService(ReceitaItemRepository receitaItemRepository, ProdutoRepository produtoRepository, IngredienteRepository ingredienteRepository, ReceitaItemMapper receitaItemMapper) {
        this.receitaItemRepository = receitaItemRepository;
        this.produtoRepository = produtoRepository;
        this.ingredienteRepository = ingredienteRepository;
        this.receitaItemMapper = receitaItemMapper;
    }

    private static final Logger log = LoggerFactory.getLogger(ReceitaItemService.class);

    @Transactional
    public ReceitaItemResponse salvar(ReceitaItemRequest request) {
        log.info("Adicionando ingredientes {} na receita do produto {}", request.getIngredienteId(),
                request.getProdutoId());

        Produto produto = buscarProduto(request.getProdutoId());
        Ingrediente ingrediente = buscarIngredienteAtivo(request.getIngredienteId());

        if (receitaItemRepository.existsByProduto_IdAndIngrediente_Id(produto.getId(), ingrediente.getId())) {
            throw new ConflictException("Já está na receita de " + produto.getNome());
        }

        ReceitaItem receitaItem = receitaItemMapper.toEntity(request);
        receitaItem.setProduto(produto);
        receitaItem.setIngrediente(ingrediente);

        return receitaItemMapper.toResponse(receitaItemRepository.save(receitaItem));
    }

    public List<ReceitaItemResponse> buscarPorProduto(Long produtoId) {
        log.info("Buscando receita do produto {}", produtoId);

        if (!produtoRepository.existsById(produtoId)) {
            throw new NotFound("Produto não encontrado");
        }

        return receitaItemRepository.findByProduto_Id(produtoId).stream().map(receitaItemMapper::toResponse).toList();
    }

    @Transactional
    public ReceitaItemResponse atualizar(Long id, AtualizarReceitaItemRequest request) {
        log.info("Atualizando quantidade do item de receita");

        ReceitaItem receitaItem = receitaItemRepository.findById(id)
                .orElseThrow(() -> new NotFound("Item de receita não encontrado"));

        receitaItem.setQuantidade(request.getQuantidade());

        return receitaItemMapper.toResponse(receitaItemRepository.save(receitaItem));
    }

    @Transactional
    public void deletar(Long id) {
        log.info("Removendo item de receita {}", id);

        if (!receitaItemRepository.existsById(id)) {
            throw new NotFound("Item da receita não encontrado");
        }

        receitaItemRepository.deleteById(id);
    }

    private Produto buscarProduto(Long produtoId) {
        return produtoRepository.findById(produtoId)
                .orElseThrow(() -> new NotFound("Produto não encontrado"));
    }

    private Ingrediente buscarIngredienteAtivo(Long ingredienteId) {
        Ingrediente ingrediente = ingredienteRepository.findById(ingredienteId)
                .orElseThrow(() -> new NotFound("Ingrediente não encontrado"));

        if (!Boolean.TRUE.equals(ingrediente.getAtivo())) {
            throw new ConflictException("O ingrediente " + ingrediente.getNome() + " está desativado");
        }
        return ingrediente;
    }
}
