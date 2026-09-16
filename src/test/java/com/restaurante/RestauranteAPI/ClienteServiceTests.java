package com.restaurante.RestauranteAPI;

import com.restaurante.RestauranteAPI.config.mappers.ClienteMapper;
import com.restaurante.RestauranteAPI.dto.request.ClienteRequest;
import com.restaurante.RestauranteAPI.dto.externals.ViaCepResponse;
import com.restaurante.RestauranteAPI.dto.response.ClienteResponse;
import com.restaurante.RestauranteAPI.entities.Cliente;
import com.restaurante.RestauranteAPI.exceptions.ConflictException;
import com.restaurante.RestauranteAPI.repositories.ClienteRepository;
import com.restaurante.RestauranteAPI.services.ClienteService;
import com.restaurante.RestauranteAPI.services.ViaCepService;
import org.junit.jupiter.api.Test;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ClienteServiceTests {
    private final ClienteRepository repository = mock(ClienteRepository.class);
    private final ClienteMapper mapper = mock(ClienteMapper.class);
    private final ViaCepService viaCep = mock(ViaCepService.class);
    private final ClienteService service = new ClienteService(repository, mapper, viaCep);

    private ClienteRequest request() {
        ClienteRequest request = new ClienteRequest();
        request.setNome("Cliente");
        request.setCpf("123.456.789-09");
        request.setEmail(" CLIENTE@EXAMPLE.COM ");
        return request;
    }

    @Test
    void emailExistenteImpedeSalvar() {
        when(repository.findByEmailIgnoreCase("cliente@example.com"))
                .thenReturn(Optional.of(new Cliente()));
        assertThrows(ConflictException.class, () -> service.salvar(request()));
        verify(repository, never()).save(any());
        verifyNoInteractions(viaCep);
    }

    @Test
    void cpfNormalizadoExistenteImpedeSalvar() {
        when(repository.findByCpf("12345678909"))
                .thenReturn(Optional.of(new Cliente()));
        assertThrows(ConflictException.class, () -> service.salvar(request()));
        verify(repository, never()).save(any());
    }

    @Test
    void consultaESalvaCepInformado() {
        ClienteRequest request = request();
        request.setCep("01001000");
        ViaCepResponse endereco = new ViaCepResponse();
        endereco.setCep("01001-000");
        Cliente cliente = new Cliente();
        when(mapper.toEntity(request)).thenReturn(cliente);
        when(viaCep.buscarEndereco("01001000")).thenReturn(endereco);
        when(repository.save(cliente)).thenReturn(cliente);
        service.salvar(request);
        assertEquals("01001-000", cliente.getCep());
        verify(repository).save(cliente);
    }

    @Test
    void salvaClienteSemCepNormalizandoOsDados() {
        ClienteRequest request = request();
        Cliente cliente = new Cliente();
        ClienteResponse response = new ClienteResponse();
        when(mapper.toEntity(request)).thenReturn(cliente);
        when(repository.save(cliente)).thenReturn(cliente);
        when(mapper.toResponse(cliente)).thenReturn(response);
        assertSame(response, service.salvar(request));
        assertEquals("cliente@example.com", cliente.getEmail());
        assertEquals("12345678909", cliente.getCpf());
        assertTrue(cliente.getAtivo());
        verifyNoInteractions(viaCep);
    }
}
