package com.restaurante.RestauranteAPI.services;

import com.restaurante.RestauranteAPI.config.mappers.MovimentacaoEstoqueMapper;
import com.restaurante.RestauranteAPI.dto.request.MovimentacaoEstoqueRequest;
import com.restaurante.RestauranteAPI.dto.response.MovimentacaoEstoqueResponse;
import com.restaurante.RestauranteAPI.entities.*;
import com.restaurante.RestauranteAPI.enums.TipoMovimentacao;
import com.restaurante.RestauranteAPI.exceptions.BadRequestException;
import com.restaurante.RestauranteAPI.exceptions.ConflictException;
import com.restaurante.RestauranteAPI.exceptions.NotFound;
import com.restaurante.RestauranteAPI.repositories.IngredienteRepository;
import com.restaurante.RestauranteAPI.repositories.MovimentacaoEstoqueRepository;
import com.restaurante.RestauranteAPI.repositories.ReceitaItemRepository;
import com.restaurante.RestauranteAPI.repositories.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class MovimentacaoEstoqueService {

    private final MovimentacaoEstoqueRepository movimentacaoEstoqueRepository;
    private final IngredienteRepository ingredienteRepository;
    private final UsuarioRepository usuarioRepository;
    private final MovimentacaoEstoqueMapper movimentacaoEstoqueMapper;
    private final ReceitaItemRepository receitaItemRepository;

    public MovimentacaoEstoqueService(MovimentacaoEstoqueRepository movimentacaoEstoqueRepository, IngredienteRepository ingredienteRepository, UsuarioRepository usuarioRepository, MovimentacaoEstoqueMapper movimentacaoEstoqueMapper, ReceitaItemRepository receitaItemRepository) {
        this.movimentacaoEstoqueRepository = movimentacaoEstoqueRepository;
        this.ingredienteRepository = ingredienteRepository;
        this.usuarioRepository = usuarioRepository;
        this.movimentacaoEstoqueMapper = movimentacaoEstoqueMapper;
        this.receitaItemRepository = receitaItemRepository;
    }

    private static final Logger log = LoggerFactory.getLogger(MovimentacaoEstoqueService.class);

    @Transactional
    public MovimentacaoEstoqueResponse registrar(MovimentacaoEstoqueRequest request,
                                                 String emailUsuarioAutenticado) {
        log.info("Registrando {} de {} no ingrediente {}", request.getTipo(), request.getQuantidade(),
                request.getIngredienteId());

        if (request.getTipo() == TipoMovimentacao.DEVOLUCAO) {
            throw new BadRequestException("Devolução é registrada automaticamente pelo sistema");
        }

        Ingrediente ingrediente = ingredienteRepository.findById(request.getIngredienteId())
                .orElseThrow(() -> new NotFound("Ingrediente não encontrado"));

        if (!Boolean.TRUE.equals(ingrediente.getAtivo())) {
            throw new ConflictException("O ingrediente " + ingrediente.getNome() + " está desativado");
        }

        if (request.getTipo() == TipoMovimentacao.ENTRADA) {
            ingrediente.setSaldo(ingrediente.getSaldo().add(request.getQuantidade()));
        } else {
            BigDecimal novoSaldo = ingrediente.getSaldo().subtract(request.getQuantidade());

            if (novoSaldo.compareTo(BigDecimal.ZERO) < 0) {
                throw new ConflictException("Saldo insuficiente de " + ingrediente.getNome()
                        + ": disponível " + ingrediente.getSaldo());
            }
            ingrediente.setSaldo(novoSaldo);
        }
        ingredienteRepository.save(ingrediente);

        Usuario usuario = usuarioRepository.findByEmailIgnoreCase(emailUsuarioAutenticado)
                .orElseThrow(() -> new NotFound("Usuário não encontrado"));

        MovimentacaoEstoque movimentacao = movimentacaoEstoqueMapper.toEntity(request);
        movimentacao.setIngrediente(ingrediente);
        movimentacao.setResponsavel(usuario);

        return movimentacaoEstoqueMapper.toResponse(movimentacaoEstoqueRepository.save(movimentacao));
    }

    @Transactional
    public void baixarEstoqueDoItem(ItemPedido item, Usuario usuario) {
        List<ReceitaItem> receita = receitaItemRepository.findByProduto_Id(item.getProduto().getId());

        for (ReceitaItem receitaItem : receita) {
            Ingrediente ingrediente = receitaItem.getIngrediente();

            BigDecimal necessario = receitaItem.getQuantidade()
                    .multiply(BigDecimal.valueOf(item.getQuantidade()));

            BigDecimal novoSaldo = ingrediente.getSaldo().subtract(necessario);

            if (novoSaldo.compareTo(BigDecimal.ZERO) < 0) {
                throw new ConflictException("Estoque insuficiente de " + ingrediente.getNome()
                        + " para " + item.getNomeProduto()
                        + ": precisa " + necessario + ", disponível " + ingrediente.getSaldo());
            }

            ingrediente.setSaldo(novoSaldo);
            ingredienteRepository.save(ingrediente);

            MovimentacaoEstoque saida = new MovimentacaoEstoque();
            saida.setIngrediente(ingrediente);
            saida.setTipo(TipoMovimentacao.SAIDA);
            saida.setQuantidade(necessario);
            saida.setItemPedido(item);
            saida.setResponsavel(usuario);
            saida.setMotivo("Pedido " + item.getPedido().getId() + ": " + item.getQuantidade()
                    + "x " + item.getNomeProduto());
            movimentacaoEstoqueRepository.save(saida);
        }
    }

    @Transactional
    public void devolverEstoqueDoItem(ItemPedido item, Usuario usuario) {
        List<MovimentacaoEstoque> saidas =
                movimentacaoEstoqueRepository.findByItemPedido_IdAndTipo(item.getId(), TipoMovimentacao.SAIDA);

        for (MovimentacaoEstoque saida : saidas) {
            Ingrediente ingrediente = saida.getIngrediente();
            ingrediente.setSaldo(ingrediente.getSaldo().add(saida.getQuantidade()));
            ingredienteRepository.save(ingrediente);

            MovimentacaoEstoque devolucao = new MovimentacaoEstoque();
            devolucao.setIngrediente(ingrediente);
            devolucao.setTipo(TipoMovimentacao.DEVOLUCAO);
            devolucao.setQuantidade(saida.getQuantidade());
            devolucao.setItemPedido(item);
            devolucao.setResponsavel(usuario);
            devolucao.setMotivo("Cancelamento do item " + item.getId() + " (" + item.getNomeProduto() + ")");
            movimentacaoEstoqueRepository.save(devolucao);
        }
    }

    public List<MovimentacaoEstoqueResponse> buscarPorIngrediente(Long ingredienteId) {
        log.info("Buscando as movimentações do ingrediente {}", ingredienteId);

        if (!ingredienteRepository.existsById(ingredienteId)) {
            throw new NotFound("Ingrediente não encontrado");
        }

        return movimentacaoEstoqueRepository.findByIngrediente_IdOrderByCriadoEmDesc(ingredienteId)
                .stream().map(movimentacaoEstoqueMapper::toResponse).toList();
    }
}
