package com.restaurante.RestauranteAPI;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Map;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class RestauranteApiApplication {

	public static void main(String[] args) {

		Dotenv dotenv = Dotenv.configure()
				.ignoreIfMissing()
				.load();

		Map<String, Object> properties = Map.of(
				"spring.datasource.url", obrigatoria(dotenv, "DB_URL"),
				"spring.datasource.username", obrigatoria(dotenv, "DB_USERNAME"),
				"spring.datasource.password", obrigatoria(dotenv, "DB_PASSWORD"),
				"api.security.token.secret", obrigatoria(dotenv, "JWT_SECRET")
		);

		SpringApplication app = new SpringApplication(RestauranteApiApplication.class);

		app.setDefaultProperties(properties);

		app.run(args);
	}
    private static String obrigatoria(Dotenv dotenv, String nome) {
        String valor = dotenv.get(nome);
        if (valor == null || valor.isBlank()) {
            throw new IllegalStateException("Variável obrigatória não configurada: " + nome);
        }
        return valor;
    }
}