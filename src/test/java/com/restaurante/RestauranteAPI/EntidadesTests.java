package com.restaurante.RestauranteAPI;

import com.restaurante.RestauranteAPI.entities.Mesa;
import com.restaurante.RestauranteAPI.entities.ReceitaItem;
import com.restaurante.RestauranteAPI.entities.Comanda;
import com.restaurante.RestauranteAPI.entities.Cliente;
import com.restaurante.RestauranteAPI.config.mappers.ClienteMapper;
import com.restaurante.RestauranteAPI.dto.request.ClienteRequest;
import jakarta.validation.Validation;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import static org.junit.jupiter.api.Assertions.*;

class EntidadesTests {
    @Test
    void validaNumerosDeMesaSemAnotacaoDeTexto() {
        try (var factory = Validation.buildDefaultValidatorFactory()) {
            Mesa mesa = new Mesa();
            mesa.setNumero(1);
            mesa.setCapacidade(4);
            assertTrue(factory.getValidator().validate(mesa).isEmpty());
            mesa.setNumero(0);
            assertFalse(factory.getValidator().validate(mesa).isEmpty());
        }
    }

    @Test
    void receitaExigeProdutoIngredienteEQuantidade() {
        try (var factory = Validation.buildDefaultValidatorFactory()) {
            assertEquals(3, factory.getValidator().validate(new ReceitaItem()).size());
        }
    }

    @Test
    void criaDatasEMapeiaCliente() {
        Comanda comanda = new Comanda();
        comanda.preencherData();
        assertNotNull(comanda.getAbertaEm());
        ClienteRequest request = new ClienteRequest();
        request.setNome("Cliente");
        request.setEmail("cliente@example.com");
        ClienteMapper mapper = Mappers.getMapper(ClienteMapper.class);
        Cliente cliente = mapper.toEntity(request);
        cliente.preencherData();
        assertEquals("Cliente", cliente.getNome());
        assertNotNull(mapper.toResponse(cliente).getCriadoEm());
    }
}
