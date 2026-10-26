package com.restaurante.RestauranteAPI.services;

import com.restaurante.RestauranteAPI.config.mappers.ComandaMapper;
import com.restaurante.RestauranteAPI.dto.request.CancelarComandaRequest;
import com.restaurante.RestauranteAPI.dto.request.ComandaRequest;
import com.restaurante.RestauranteAPI.dto.response.ComandaResponse;
import com.restaurante.RestauranteAPI.entities.Cliente;
import com.restaurante.RestauranteAPI.entities.Comanda;
import com.restaurante.RestauranteAPI.entities.HistoricoStatusItem;
import com.restaurante.RestauranteAPI.entities.ItemPedido;
import com.restaurante.RestauranteAPI.entities.Mesa;
import com.restaurante.RestauranteAPI.entities.Usuario;
import com.restaurante.RestauranteAPI.enums.StatusComanda;
import com.restaurante.RestauranteAPI.enums.StatusItemPedido;
import com.restaurante.RestauranteAPI.exceptions.BadRequestException;
import com.restaurante.RestauranteAPI.exceptions.ConflictException;
import com.restaurante.RestauranteAPI.exceptions.NotFound;
import com.restaurante.RestauranteAPI.repositories.ClienteRepository;
import com.restaurante.RestauranteAPI.repositories.ComandaRepository;
import com.restaurante.RestauranteAPI.repositories.HistoricoStatusItemRepository;
import com.restaurante.RestauranteAPI.repositories.ItemPedidoRepository;
import com.restaurante.RestauranteAPI.repositories.MesaRepository;
import com.restaurante.RestauranteAPI.repositories.UsuarioRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.List;

@Service
public class ComandaService {

    private final ComandaRepository comandaRepository;
    private final ComandaMapper comandaMapper;
    private final MesaRepository mesaRepository;
    private final ClienteRepository clienteRepository;
    private final UsuarioRepository usuarioRepository;
    private final Clock clock;
    private final PagamentoService pagamentoService;
    private final ItemPedidoRepository itemPedidoRepository;
    private final HistoricoStatusItemRepository historicoStatusItemRepository;
    private final MovimentacaoEstoqueService movimentacaoEstoqueService;


    public ComandaService(ComandaRepository comandaRepository, ComandaMapper comandaMapper, MesaRepository mesaRepository,
                          ClienteRepository clienteRepository,
                          UsuarioRepository usuarioRepository, Clock clock, PagamentoService pagamentoService,
                          ItemPedidoRepository itemPedidoRepository,
                          HistoricoStatusItemRepository historicoStatusItemRepository,
                          MovimentacaoEstoqueService movimentacaoEstoqueService) {
        this.comandaRepository = comandaRepository;
        this.comandaMapper = comandaMapper;
        this.mesaRepository = mesaRepository;
        this.clienteRepository = clienteRepository;
        this.usuarioRepository = usuarioRepository;
        this.clock = clock;
        this.pagamentoService = pagamentoService;
        this.itemPedidoRepository = itemPedidoRepository;
        this.historicoStatusItemRepository = historicoStatusItemRepository;
        this.movimentacaoEstoqueService = movimentacaoEstoqueService;
    }

    private static final Logger log = LoggerFactory.getLogger(ComandaService.class);

    @Cacheable("comandas")
    public List<ComandaResponse> buscarTodos(){
        log.info("Buscando todas as comandas");

        return comandaRepository.findAll().stream()
                .map(comandaMapper::toResponse)
                .toList();
    }

    @Cacheable(value = "comanda", key = "#id")
    public ComandaResponse buscarPorId(Long id){
        log.info("Buscando comanda por id, ID: {}", id);
        Comanda comanda = comandaRepository.findById(id)
                .orElseThrow(() -> new NotFound("Comanda não encontrada"));
        return comandaMapper.toResponse(comanda);
    }

    @Transactional
    @CacheEvict(value = {"comandas","comanda"}, allEntries = true)
    public ComandaResponse salvar(ComandaRequest request, String emailUsuarioAutenticado){
        log.info("Abrindo comanda para a mesa {}", request.getMesaId());

        Mesa mesa = buscarMesaDisponivel(request.getMesaId());

        if (comandaRepository.existsByMesa_IdAndStatus(mesa.getId(), StatusComanda.ABERTA)) {
            throw new ConflictException("A mesa já possui uma comanda aberta");
        }

        Cliente cliente = buscarClienteAtivo(request.getClienteId());

        Usuario usuario = usuarioRepository
                .findByEmailIgnoreCase(emailUsuarioAutenticado)
                .orElseThrow(() -> new NotFound("Usuário não encontrado"));

        if (!Boolean.TRUE.equals(usuario.getAtivo())) {
            throw new ConflictException("O usuário está inativo");
        }

        Comanda comanda = comandaMapper.toEntity(request);
        comanda.setMesa(mesa);
        comanda.setCliente(cliente);
        comanda.setAbertaPor(usuario);
        comanda.setStatus(StatusComanda.ABERTA);

        Comanda comandaSalva = comandaRepository.save(comanda);
        return comandaMapper.toResponse(comandaSalva);
    }

    @Transactional
    @CacheEvict(value = {"comandas", "comanda"}, allEntries = true)
    public ComandaResponse atualizar(Long id, ComandaRequest request) {
        log.info("Atualizando comanda ID: {}", id);

        Comanda comanda = buscarComandaAberta(id);

        if (!comanda.getMesa().getId().equals(request.getMesaId())) {
            Mesa novaMesa = buscarMesaDisponivel(request.getMesaId());

            if (comandaRepository.existsByMesa_IdAndStatus(novaMesa.getId(), StatusComanda.ABERTA)) {
                throw new ConflictException("A nova mesa já possui uma comanda aberta");
            }
            comanda.setMesa(novaMesa);
        }

        comanda.setCliente(buscarClienteAtivo(request.getClienteId()));

        return comandaMapper.toResponse(comandaRepository.save(comanda));
    }

    @Transactional
    @CacheEvict(value = {"comandas", "comanda"}, allEntries = true)
    public ComandaResponse cancelar(Long id, CancelarComandaRequest request, String emailUsuarioAutenticado) {
        log.info("Cancelando comanda com ID {}", id);

        Comanda comanda = buscarComandaAberta(id);

        if (pagamentoService.existePagamentoConfirmado(comanda.getId())) {
            throw new ConflictException("A comanda tem pagamentos confirmados. Estorne os pagamentos antes de cancelar");
        }

        Usuario usuario = usuarioRepository.findByEmailIgnoreCase(emailUsuarioAutenticado)
                .orElseThrow(() -> new NotFound("Usuário não encontrado"));

        // itens que ainda não ficaram prontos são cancelados e devolvem o estoque
        for (ItemPedido item : itemPedidoRepository.findByPedido_Comanda_Id(comanda.getId())) {
            StatusItemPedido statusAnterior = item.getStatus();

            if (statusAnterior == StatusItemPedido.RECEBIDO || statusAnterior == StatusItemPedido.PREPARANDO) {
                item.setStatus(StatusItemPedido.CANCELADO);
                itemPedidoRepository.save(item);

                movimentacaoEstoqueService.devolverEstoqueDoItem(item, usuario);

                HistoricoStatusItem historico = new HistoricoStatusItem();
                historico.setItemPedido(item);
                historico.setStatusAnterior(statusAnterior);
                historico.setStatusNovo(StatusItemPedido.CANCELADO);
                historico.setAlteradoPor(usuario);
                historico.setMotivo("Comanda cancelada: " + request.getMotivo());
                historicoStatusItemRepository.save(historico);
            }
        }

        comanda.setStatus(StatusComanda.CANCELADA);
        comanda.setEncerradaEm(OffsetDateTime.now(clock));
        comanda.setMotivoCancelamento(request.getMotivo());

        return comandaMapper.toResponse(comandaRepository.save(comanda));
    }

    @Transactional
    @CacheEvict(value = {"comandas", "comanda"}, allEntries = true)
    public ComandaResponse fechar(Long id) {
        log.info("Fechando comanda com o ID {}", id);

        Comanda comanda = buscarComandaAberta(id);

        boolean temItemPendente = itemPedidoRepository.findByPedido_Comanda_Id(comanda.getId()).stream()
                .anyMatch(item -> item.getStatus() != StatusItemPedido.ENTREGUE
                        && item.getStatus() != StatusItemPedido.CANCELADO);

        if (temItemPendente) {
            throw new ConflictException("Ainda há itens que não foram entregues nem cancelados");
        }

        BigDecimal restante = pagamentoService.calcularRestante(comanda.getId());

        if (restante.compareTo(BigDecimal.ZERO) !=0) {
            throw new ConflictException("Ainda falta pagar R$ " + restante);
        }

        comanda.setStatus(StatusComanda.FECHADA);
        comanda.setEncerradaEm(OffsetDateTime.now(clock));

        return comandaMapper.toResponse(comandaRepository.save(comanda));
    }

    private Comanda buscarComandaAberta(Long id) {
        Comanda comanda = comandaRepository.findById(id)
                .orElseThrow(() -> new NotFound("Comanda não encontrada"));

        if (comanda.getStatus() != StatusComanda.ABERTA) {
            throw new ConflictException("Só é possivel alterar comandas abertas");
        }
        return comanda;
    }

    private Mesa buscarMesaDisponivel(Long mesaId) {
        Mesa mesa = mesaRepository.findById(mesaId)
                .orElseThrow(() -> new NotFound("Mesa não encontrada"));

        if (!Boolean.TRUE.equals(mesa.getDisponivel())) {
            throw new ConflictException("A mesa está desativada");
        }
        return mesa;
    }

    private Cliente buscarClienteAtivo(Long clienteId) {
        if (clienteId == null) {
            return null;
        }
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new NotFound("Cliente não encontrado"));

        if (!Boolean.TRUE.equals(cliente.getAtivo())) {
            throw new ConflictException("O cliente está inativo");
        }
        return cliente;
    }

}
