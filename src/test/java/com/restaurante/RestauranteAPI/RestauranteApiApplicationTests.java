package com.restaurante.RestauranteAPI;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

// sobe a aplicação inteira com o perfil "test" (banco H2 em memória, sem precisar do Docker nem do .env)
@SpringBootTest
@ActiveProfiles("test")
class RestauranteApiApplicationTests {

    @Test
    void contextLoads() {
    }

}
