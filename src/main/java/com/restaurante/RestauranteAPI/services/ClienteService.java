package com.restaurante.RestauranteAPI.services;

import com.restaurante.RestauranteAPI.config.mappers.ClienteMapper;
import com.restaurante.RestauranteAPI.dto.request.ClienteRequest;
import com.restaurante.RestauranteAPI.dto.externals.ViaCepResponse;
import java.util.Locale;
import com.restaurante.RestauranteAPI.dto.response.ClienteResponse;
import com.restaurante.RestauranteAPI.entities.Cliente;
import com.restaurante.RestauranteAPI.exceptions.ConflictException;
import com.restaurante.RestauranteAPI.exceptions.NotFound;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import com.restaurante.RestauranteAPI.repositories.ClienteRepository;

import java.util.List;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final ClienteMapper clienteMapper;
    private final ViaCepService viaCepService;

    public ClienteService(ClienteRepository clienteRepository, ClienteMapper clienteMapper, ViaCepService viaCepService) {
        this.clienteRepository = clienteRepository;
        this.clienteMapper = clienteMapper;
        this.viaCepService = viaCepService;
    }

    private static final Logger log = LoggerFactory.getLogger(ClienteService.class);

    @Cacheable("clientes")
    public List<ClienteResponse> buscarTodos(){
        log.info("Buscando todos os clientes");
        return clienteRepository.findAll().stream().map(clienteMapper::toResponse).toList();
    }

    @Cacheable(value = "cliente", key = "#id")
    public ClienteResponse buscarPorId(Long id) {
        log.info("Buscando clientes com o ID {}", id);
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new NotFound("Cliente não encontrado"));
        return clienteMapper.toResponse(cliente);
    }

    @CacheEvict(value = {"clientes", "cliente"}, allEntries = true)
    public ClienteResponse salvar (ClienteRequest request) {
        log.info("Salvando clientes");

        String cpf = request.getCpf().replaceAll("[^0-9]", "");
        String email = request.getEmail().trim().toLowerCase(Locale.ROOT);
        if (clienteRepository.findByCpf(cpf).isPresent()) {
            throw new ConflictException("CPF já cadastrado");
        }
        if (clienteRepository.findByEmailIgnoreCase(email).isPresent()) {
            throw new ConflictException("E-mail já cadastrado");
        }

        ViaCepResponse endereco = viaCepService.buscarEndereco(request.getCep());

        Cliente cliente = clienteMapper.toEntity(request);
        cliente.setCep(endereco.getCep());
        cliente.setLogradouro(endereco.getLogradouro());
        cliente.setBairro(endereco.getBairro());
        cliente.setCidade(endereco.getLocalidade());
        cliente.setUf(endereco.getUf());

        Cliente clienteSalvo = clienteRepository.save(cliente);

        log.info("Cliente salvo com sucesso, ID {}", clienteSalvo.getId());

        return clienteMapper.toResponse(clienteSalvo);
    }

    @CacheEvict(value = {"clientes", "cliente"}, allEntries = true)
    public ClienteResponse atualizar(Long id, ClienteRequest request){
        log.info("Atualizando clientes com o ID {}", id);

        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new NotFound("Cliente não encontrado"));

        cliente.setNome(request.getNome());
        cliente.setCpf(request.getCpf());
        cliente.setEmail(request.getEmail());
        cliente.setTelefone(request.getTelefone());

        Cliente clienteAtualizado = clienteRepository.save(cliente);

        return clienteMapper.toResponse(clienteAtualizado);
    }

    @CacheEvict(value = {"clientes", "cliente"}, allEntries = true)
    public void deletar (Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new NotFound("Cliente não encontrado"));

        cliente.setAtivo(false);

        Cliente clienteDesativo = clienteRepository.save(cliente);
    }
}
