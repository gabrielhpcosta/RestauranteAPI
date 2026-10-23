package com.restaurante.RestauranteAPI.services;

import com.restaurante.RestauranteAPI.config.mappers.MovimentacaoEstoqueMapper;
import com.restaurante.RestauranteAPI.dto.request.MovimentacaoEstoqueRequest;
import com.restaurante.RestauranteAPI.dto.response.MovimentacaoEstoqueResponse;
import com.restaurante.RestauranteAPI.entities.Ingrediente;
import com.restaurante.RestauranteAPI.entities.MovimentacaoEstoque;
import com.restaurante.RestauranteAPI.entities.Usuario;
import com.restaurante.RestauranteAPI.enums.TipoMovimentacao;
import com.restaurante.RestauranteAPI.exceptions.BadRequestException;
import com.restaurante.RestauranteAPI.exceptions.ConflictException;
import com.restaurante.RestauranteAPI.exceptions.NotFound;
import com.restaurante.RestauranteAPI.repositories.IngredienteRepository;
import com.restaurante.RestauranteAPI.repositories.MovimentacaoEstoqueRepository;
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

    public MovimentacaoEstoqueService(MovimentacaoEstoqueRepository movimentacaoEstoqueRepository, IngredienteRepository ingredienteRepository, UsuarioRepository usuarioRepository, MovimentacaoEstoqueMapper movimentacaoEstoqueMapper) {
        this.movimentacaoEstoqueRepository = movimentacaoEstoqueRepository;
        this.ingredienteRepository = ingredienteRepository;
        this.usuarioRepository = usuarioRepository;
        this.movimentacaoEstoqueMapper = movimentacaoEstoqueMapper;
    }

    private static final Logger log = LoggerFactory.getLogger(MovimentacaoEstoqueService.class);

    @Transactional
    public MovimentacaoEstoqueResponse registrar(MovimentacaoEstoqueRequest request,
                                                 String emailUsuarioAutenticado) {
        log.info("Registrando {} de {} no ingrediente", request.getTipo(), request.getQuantidade(),
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
                        + ": disponivel " + ingrediente.getSaldo());
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

    public List<MovimentacaoEstoqueResponse> buscarPorIngrediente(Long ingredienteId) {
        log.info("Buscando as movimentações do ingrediente {}", ingredienteId);

        if (!ingredienteRepository.existsById(ingredienteId)) {
            throw new NotFound("Ingrediente não encontrado");
        }

        return movimentacaoEstoqueRepository.findByIngrediente_IdOrderByCriadoEmDesc(ingredienteId)
                .stream().map(movimentacaoEstoqueMapper::toResponse).toList();
    }
}
