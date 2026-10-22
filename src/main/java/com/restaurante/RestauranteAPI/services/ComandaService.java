package com.restaurante.RestauranteAPI.services;

import com.restaurante.RestauranteAPI.config.mappers.ComandaMapper;
import com.restaurante.RestauranteAPI.dto.request.CancelarComandaRequest;
import com.restaurante.RestauranteAPI.dto.request.ComandaRequest;
import com.restaurante.RestauranteAPI.dto.response.ComandaResponse;
import com.restaurante.RestauranteAPI.entities.Cliente;
import com.restaurante.RestauranteAPI.entities.Comanda;
import com.restaurante.RestauranteAPI.entities.Mesa;
import com.restaurante.RestauranteAPI.entities.Usuario;
import com.restaurante.RestauranteAPI.enums.StatusComanda;
import com.restaurante.RestauranteAPI.exceptions.BadRequestException;
import com.restaurante.RestauranteAPI.exceptions.ConflictException;
import com.restaurante.RestauranteAPI.exceptions.NotFound;
import com.restaurante.RestauranteAPI.repositories.ClienteRepository;
import com.restaurante.RestauranteAPI.repositories.ComandaRepository;
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


    public ComandaService(ComandaRepository comandaRepository, ComandaMapper comandaMapper, MesaRepository mesaRepository,
                          ClienteRepository clienteRepository,
                          UsuarioRepository usuarioRepository, Clock clock, PagamentoService pagamentoService) {
        this.comandaRepository = comandaRepository;
        this.comandaMapper = comandaMapper;
        this.mesaRepository = mesaRepository;
        this.clienteRepository = clienteRepository;
        this.usuarioRepository = usuarioRepository;
        this.clock = clock;
        this.pagamentoService = pagamentoService;
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
    public ComandaResponse cancelar(Long id, CancelarComandaRequest request) {
        log.info("Cancelando comanda com ID {}", id);

        Comanda comanda = buscarComandaAberta(id);

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
