package com.restaurante.RestauranteAPI.services;

import com.restaurante.RestauranteAPI.config.mappers.ItemPedidoMapper;
import com.restaurante.RestauranteAPI.dto.request.AlterarStatusItemRequest;
import com.restaurante.RestauranteAPI.dto.response.ItemPedidoResponse;
import com.restaurante.RestauranteAPI.entities.HistoricoStatusItem;
import com.restaurante.RestauranteAPI.entities.ItemPedido;
import com.restaurante.RestauranteAPI.entities.Usuario;
import com.restaurante.RestauranteAPI.enums.StatusComanda;
import com.restaurante.RestauranteAPI.enums.StatusItemPedido;
import com.restaurante.RestauranteAPI.exceptions.BadRequestException;
import com.restaurante.RestauranteAPI.exceptions.ConflictException;
import com.restaurante.RestauranteAPI.exceptions.NotFound;
import com.restaurante.RestauranteAPI.repositories.HistoricoStatusItemRepository;
import com.restaurante.RestauranteAPI.repositories.ItemPedidoRepository;
import com.restaurante.RestauranteAPI.repositories.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class ItemPedidoService {

    private final ItemPedidoRepository itemPedidoRepository;
    private final ItemPedidoMapper itemPedidoMapper;
    private final HistoricoStatusItemRepository historicoStatusItemRepository;
    private final UsuarioRepository usuarioRepository;
    private final MovimentacaoEstoqueService movimentacaoEstoqueService;
    private final PagamentoService pagamentoService;

    public ItemPedidoService(ItemPedidoRepository itemPedidoRepository, ItemPedidoMapper itemPedidoMapper, HistoricoStatusItemRepository historicoStatusItemRepository, UsuarioRepository usuarioRepository, MovimentacaoEstoqueService movimentacaoEstoqueService, PagamentoService pagamentoService) {
        this.itemPedidoRepository = itemPedidoRepository;
        this.itemPedidoMapper = itemPedidoMapper;
        this.historicoStatusItemRepository = historicoStatusItemRepository;
        this.usuarioRepository = usuarioRepository;
        this.movimentacaoEstoqueService = movimentacaoEstoqueService;
        this.pagamentoService = pagamentoService;
    }

    private static final Logger log = LoggerFactory.getLogger(ItemPedidoService.class);

    @Transactional
    public ItemPedidoResponse alterarStatus(Long id, AlterarStatusItemRequest request,
                                           String emailUsuarioAutenticado) {
        log.info("Alterando status do item {} para {}", id, request.getStatus());

        ItemPedido item = itemPedidoRepository.findById(id)
                .orElseThrow(() -> new NotFound("Item do pedido não encontrado"));

        if (item.getPedido().getComanda().getStatus() != StatusComanda.ABERTA) {
            throw new ConflictException("Só é possível alterar itens de comandas abertas");
        }

        StatusItemPedido statusAtual = item.getStatus();
        StatusItemPedido statusNovo = request.getStatus();

        if (!transicaoPermitida(statusAtual, statusNovo)) {
            throw new ConflictException("Não é possivel mudar o item de " + statusAtual + " para " + statusNovo);
        }

        if (statusNovo == StatusItemPedido.CANCELADO && (request.getMotivo() == null || request.getMotivo().isBlank())) {
            throw new BadRequestException("Informe o motivo do cancelamento");
        }

        if (statusNovo == StatusItemPedido.CANCELADO) {
            BigDecimal valorItem = item.getPrecoUnitario().multiply(BigDecimal.valueOf(item.getQuantidade()));
            BigDecimal restante = pagamentoService.calcularRestante(item.getPedido().getComanda().getId());

            if (restante.subtract(valorItem).compareTo(BigDecimal.ZERO) < 0) {
                throw new ConflictException("Cancelar este item deixaria a conta paga a mais. "
                        + "Estorne o pagamento antes (restante atual: R$ " + restante + ")");
            }
        }

        Usuario usuario = usuarioRepository.findByEmailIgnoreCase(emailUsuarioAutenticado)
                .orElseThrow(() -> new NotFound("Usuário não encontrado"));

        item.setStatus(statusNovo);
        ItemPedido itemAtualizado = itemPedidoRepository.save(item);

        if (statusNovo == StatusItemPedido.CANCELADO) {
            movimentacaoEstoqueService.devolverEstoqueDoItem(itemAtualizado, usuario);
        }

        HistoricoStatusItem historico = new HistoricoStatusItem();
        historico.setItemPedido(itemAtualizado);
        historico.setStatusAnterior(statusAtual);
        historico.setStatusNovo(statusNovo);
        historico.setAlteradoPor(usuario);
        historico.setMotivo(request.getMotivo());
        historicoStatusItemRepository.save(historico);

        return itemPedidoMapper.toResponse(itemAtualizado);
    }

    private boolean transicaoPermitida(StatusItemPedido atual, StatusItemPedido novo) {
        return switch (atual) {
            case RECEBIDO -> novo == StatusItemPedido.PREPARANDO || novo == StatusItemPedido.CANCELADO;
            case PREPARANDO -> novo == StatusItemPedido.PRONTO || novo == StatusItemPedido.CANCELADO;
            case PRONTO -> novo == StatusItemPedido.ENTREGUE;
            case ENTREGUE, CANCELADO -> false;
        };
    }
}
