package com.restaurante.RestauranteAPI.services;

import com.restaurante.RestauranteAPI.config.mappers.PagamentoMapper;
import com.restaurante.RestauranteAPI.dto.request.EstornarPagamentoRequest;
import com.restaurante.RestauranteAPI.dto.request.PagamentoRequest;
import com.restaurante.RestauranteAPI.dto.response.PagamentoResponse;
import com.restaurante.RestauranteAPI.entities.Comanda;
import com.restaurante.RestauranteAPI.entities.Pagamento;
import com.restaurante.RestauranteAPI.entities.Usuario;
import com.restaurante.RestauranteAPI.enums.StatusComanda;
import com.restaurante.RestauranteAPI.enums.StatusItemPedido;
import com.restaurante.RestauranteAPI.enums.StatusPagamento;
import com.restaurante.RestauranteAPI.exceptions.ConflictException;
import com.restaurante.RestauranteAPI.exceptions.NotFound;
import com.restaurante.RestauranteAPI.repositories.ComandaRepository;
import com.restaurante.RestauranteAPI.repositories.ItemPedidoRepository;
import com.restaurante.RestauranteAPI.repositories.PagamentoRepository;
import com.restaurante.RestauranteAPI.repositories.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PagamentoService {

    public BigDecimal calcularRestante(Long comandaId) {
        return calcularTotalConsumido(comandaId).subtract(calcularTotalPago(comandaId));
    }

    private final PagamentoRepository pagamentoRepository;
    private final PagamentoMapper pagamentoMapper;
    private final ComandaRepository comandaRepository;
    private final UsuarioRepository usuarioRepository;
    private final ItemPedidoRepository itemPedidoRepository;
    private final Clock clock;

    public PagamentoService(PagamentoRepository pagamentoRepository, PagamentoMapper pagamentoMapper,
                            ComandaRepository comandaRepository, UsuarioRepository usuarioRepository,
                            ItemPedidoRepository itemPedidoRepository, Clock clock) {
        this.pagamentoRepository = pagamentoRepository;
        this.pagamentoMapper = pagamentoMapper;
        this.comandaRepository = comandaRepository;
        this.usuarioRepository = usuarioRepository;
        this.itemPedidoRepository = itemPedidoRepository;
        this.clock = clock;
    }

    private static final Logger log = LoggerFactory.getLogger(PagamentoService.class);

    @Transactional
    public PagamentoResponse registrar(PagamentoRequest request, String emailUsuarioAutenticado) {
        log.info("Registrando pagamento de {} na comanda {}", request.getValor(), request.getComandaId());

        Optional<Pagamento> existente =
                pagamentoRepository.findByChaveIdempotencia(request.getChaveIdempotencia());

        if (existente.isPresent()) {
            if (!existente.get().getComanda().getId().equals(request.getComandaId())) {
                throw new ConflictException("Essa chave de idempotência já foi usada em outra comanda");
            }
            log.info("Pagamento repetido (mesma chave), devolvendo o existente");
            return pagamentoMapper.toResponse(existente.get());
        }

        Comanda comanda = buscarComandaAberta(request.getComandaId());

        BigDecimal restante = calcularTotalConsumido(comanda.getId())
                .subtract(calcularTotalPago(comanda.getId()));

        if (request.getValor().compareTo(restante) > 0) {
            throw new ConflictException("O valor excede o restante da conta: R$ " + restante);
        }

        Usuario usuario = usuarioRepository.findByEmailIgnoreCase(emailUsuarioAutenticado)
                .orElseThrow(() -> new NotFound("Usuário não encontrado"));

        Pagamento pagamento = pagamentoMapper.toEntity(request);
        pagamento.setComanda(comanda);
        pagamento.setStatus(StatusPagamento.CONFIRMADO);
        pagamento.setRegistradoPor(usuario);

        return pagamentoMapper.toResponse(pagamentoRepository.save(pagamento));
    }

    @Transactional
    public PagamentoResponse estornar(Long id, EstornarPagamentoRequest request,
                                      String emailUsuarioAutenticado) {
        log.info("Estornando pagamento {}", id);

        Pagamento pagamento = pagamentoRepository.findById(id)
                .orElseThrow(() -> new NotFound("Pagamento não encontrado"));

        if (pagamento.getStatus() != StatusPagamento.CONFIRMADO) {
            throw new ConflictException("Só é possível estornar pagamentos confirmados");
        }

        if (pagamento.getComanda().getStatus() != StatusComanda.ABERTA) {
            throw new ConflictException("Não é possivel estornar pagamento de comanda encerrada");
        }

        Usuario usuario = usuarioRepository.findByEmailIgnoreCase(emailUsuarioAutenticado)
                .orElseThrow(() -> new NotFound("Usuário não encontrado"));

        pagamento.setStatus(StatusPagamento.ESTORNADO);
        pagamento.setEstornadoEm(OffsetDateTime.now(clock));
        pagamento.setEstornadoPor(usuario);
        pagamento.setMotivoEstorno(request.getMotivo());

        return pagamentoMapper.toResponse(pagamentoRepository.save(pagamento));
    }

    public List<PagamentoResponse> buscarPorComanda(Long comandaId) {
        log.info("Buscando pagamentos da comanda {}", comandaId);

        if (!comandaRepository.existsById(comandaId)) {
            throw new NotFound("Comanda não encontrada");
        }

        return pagamentoRepository.findByComanda_Id(comandaId).stream()
                .map(pagamentoMapper::toResponse)
                .toList();
    }

    private Comanda buscarComandaAberta(Long comandaId) {
        Comanda comanda = comandaRepository.findById(comandaId)
                .orElseThrow(() -> new NotFound("Comanda não encontrada"));

        if (comanda.getStatus() != StatusComanda.ABERTA) {
            throw new ConflictException("Só é possível pagar comandas abertas");
        }
        return comanda;
    }

    private BigDecimal calcularTotalConsumido(Long comandaId) {
        return itemPedidoRepository.findByPedido_Comanda_Id(comandaId).stream()
                .filter(item -> item.getStatus() != StatusItemPedido.CANCELADO)
                .map(item -> item.getPrecoUnitario().multiply(BigDecimal.valueOf(item.getQuantidade())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calcularTotalPago(Long comandaId) {
        return pagamentoRepository.findByComanda_Id(comandaId).stream()
                .filter(p -> p.getStatus() == StatusPagamento.CONFIRMADO)
                .map(Pagamento::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}