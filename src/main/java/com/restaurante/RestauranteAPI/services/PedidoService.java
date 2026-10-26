package com.restaurante.RestauranteAPI.services;

import com.restaurante.RestauranteAPI.config.mappers.ItemPedidoMapper;
import com.restaurante.RestauranteAPI.config.mappers.PedidoMapper;
import com.restaurante.RestauranteAPI.dto.request.ItemPedidoRequest;
import com.restaurante.RestauranteAPI.dto.request.PedidoRequest;
import com.restaurante.RestauranteAPI.dto.response.PedidoResponse;
import com.restaurante.RestauranteAPI.entities.*;
import com.restaurante.RestauranteAPI.enums.StatusComanda;
import com.restaurante.RestauranteAPI.enums.StatusItemPedido;
import com.restaurante.RestauranteAPI.exceptions.ConflictException;
import com.restaurante.RestauranteAPI.exceptions.NotFound;
import com.restaurante.RestauranteAPI.repositories.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ItemPedidoRepository itemPedidoRepository;
    private final ComandaRepository comandaRepository;
    private final ProdutoRepository produtoRepository;
    private final UsuarioRepository usuarioRepository;
    private final PedidoMapper pedidoMapper;
    private final ItemPedidoMapper itemPedidoMapper;
    private final MovimentacaoEstoqueService movimentacaoEstoqueService;

    public PedidoService(PedidoRepository pedidoRepository, ItemPedidoRepository itemPedidoRepository, ComandaRepository comandaRepository, ProdutoRepository produtoRepository, UsuarioRepository usuarioRepository, PedidoMapper pedidoMapper, ItemPedidoMapper itemPedidoMapper, MovimentacaoEstoqueService movimentacaoEstoqueService) {
        this.pedidoRepository = pedidoRepository;
        this.itemPedidoRepository = itemPedidoRepository;
        this.comandaRepository = comandaRepository;
        this.produtoRepository = produtoRepository;
        this.usuarioRepository = usuarioRepository;
        this.pedidoMapper = pedidoMapper;
        this.itemPedidoMapper = itemPedidoMapper;
        this.movimentacaoEstoqueService = movimentacaoEstoqueService;
    }

    private static final Logger log = LoggerFactory.getLogger(PedidoService.class);

    public List<PedidoResponse> buscarTodos() {
        log.info("Buscando todos os pedidos");

        return pedidoRepository.findAll().stream().map(pedido -> montarResponse
                (pedido, itemPedidoRepository.findByPedido_Id(pedido.getId()))).toList();
    }

    public PedidoResponse buscarPorId(Long id) {
        log.info("Buscando pedido com ID {}", id);

        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new NotFound("Pedido não encontrado"));
        return montarResponse(pedido, itemPedidoRepository.findByPedido_Id(id));
    }

    @Transactional
    public PedidoResponse salvar(PedidoRequest request, String emailUsuarioAutenticado) {
        log.info("Registrando pedido na comanda {}", request.getComandaId());

        Comanda comanda = buscarComandaAberta(request.getComandaId());
        Usuario usuario = buscarUsuario(emailUsuarioAutenticado);

        Pedido pedido = pedidoMapper.toEntity(request);
        pedido.setComanda(comanda);
        pedido.setRegistradoPor(usuario);
        Pedido pedidoSalvo = pedidoRepository.save(pedido);

        List<ItemPedido> itensSalvos = new ArrayList<>();

        for (ItemPedidoRequest itemRequest : request.getItens()) {
            Produto produto = buscarProdutoDisponivel(itemRequest.getProdutoId());

            ItemPedido item = itemPedidoMapper.toEntity(itemRequest);
            item.setPedido(pedidoSalvo);
            item.setProduto(produto);

            item.setNomeProduto(produto.getNome());
            item.setPrecoUnitario(produto.getPreco());
            item.setExigePreparo(produto.getExigePreparo());

            item.setStatus(StatusItemPedido.RECEBIDO);

            ItemPedido itemSalvo = itemPedidoRepository.save(item);
            movimentacaoEstoqueService.baixarEstoqueDoItem(itemSalvo, usuario);

            itensSalvos.add(itemSalvo);
        }

        log.info("Pedido {} registrado com {} itens", pedidoSalvo.getId(), itensSalvos.size());

        return montarResponse(pedidoSalvo, itensSalvos);
    }

    private Comanda buscarComandaAberta(Long comandaId) {
        Comanda comanda = comandaRepository.findById(comandaId)
                .orElseThrow(() -> new NotFound("Comanda não encontrada"));

        if (comanda.getStatus() != StatusComanda.ABERTA) {
            throw new ConflictException("Só é possível lançar pedidos em comandas abertas");
        }
        return comanda;
    }

    private Produto buscarProdutoDisponivel(Long produtoId) {
        Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() -> new NotFound("Produto não encontrado: " + produtoId));

        if (!Boolean.TRUE.equals(produto.getDisponivel())) {
            throw new ConflictException("O produto " + produto.getNome() + " não está disponível");
        }
        return produto;
    }

    private Usuario buscarUsuario(String email) {
        return usuarioRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new NotFound("Usuário não encontrado"));
    }

    private PedidoResponse montarResponse(Pedido pedido, List<ItemPedido> itens) {
        PedidoResponse response = pedidoMapper.toResponse(pedido);

        response.setItens(itens.stream()
                .map(itemPedidoMapper::toResponse)
                .toList());

        BigDecimal total = itens.stream()
                .map(item -> item.getPrecoUnitario()
                        .multiply(BigDecimal.valueOf(item.getQuantidade())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        response.setTotal(total);

        return response;
    }
}
