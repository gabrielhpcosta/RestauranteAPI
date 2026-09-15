package services;

import config.mappers.ClienteMapper;
import dto.response.ClienteResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import repositories.ClienteRepository;

import java.util.List;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final ClienteMapper clienteMapper;

    public ClienteService(ClienteRepository clienteRepository, ClienteMapper clienteMapper) {
        this.clienteRepository = clienteRepository;
        this.clienteMapper = clienteMapper;
    }

    private static final Logger log = LoggerFactory.getLogger(ClienteService.class);

    @Cacheable("clientes")
    public List<ClienteResponse> buscarTodos(){
        log.info("Buscando todos os clientes");
        return clienteRepository.findAll().stream().map(clienteMapper::toResponse).toList();
    }

    @Cacheable(value = "cliente", key = "#id")
    public ClienteResponse buscarPorId(Long id){
        log.info("Buscando clientes com o ID {}", id);
    }


}
