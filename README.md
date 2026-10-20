# RestauranteAPI

API REST para gestão de restaurante, desenvolvida com **Java 17 e Spring Boot**.

O objetivo é cobrir o fluxo de um restaurante: mesas, comandas, pedidos, cardápio, estoque de ingredientes e pagamentos, com autenticação via **JWT** e controle de acesso por perfil de usuário.

> 🚧 **Projeto em desenvolvimento.** Autenticação, usuários, clientes, mesas, comandas, categoria, e produto já estão implementados. As demais partes (pedidos, estoque e pagamentos) já têm entidades, repositórios e serviços modelados, e os endpoints estão sendo construídos.

## Tecnologias

- Java 17
- Spring Boot
- Spring Web
- Spring Security + JWT (jjwt)
- Spring Data JPA / Hibernate
- PostgreSQL (H2 disponível para testes)
- Spring Cache
- Bean Validation
- MapStruct
- Lombok
- Springdoc OpenAPI (Swagger)
- Spring Boot Actuator
- Maven
- -Docker

## Funcionalidades

### Implementadas

- Login com **access token** (15 min) e **refresh token** (7 dias)
- Controle de acesso por perfil: **ADMIN** e **GARCOM**
- Bloqueio temporário de 15 minutos após 3 tentativas de login erradas
- Senhas criptografadas com BCrypt
- CRUD de usuários, clientes e mesas
- Busca de endereço por CEP via **ViaCEP** no cadastro de clientes
- Cache nas consultas de usuários e mesas
- Tratamento global de exceções
- Logs da aplicação em arquivo (`logs/api-restaurante.log`)
- Documentação interativa com Swagger
- CRUD de produtos, comandas e categoria

### Em desenvolvimento

- Abertura e fechamento de comandas por mesa
- Pedidos e acompanhamento do status de cada item
- Cardápio com categorias e produtos
- Receitas (ingredientes de cada produto) e movimentação de estoque
- Pagamentos com diferentes formas de pagamento

## Endpoints

Base URL: `http://localhost:8082`

| Método | Rota | Acesso | Descrição |
|---|---|---|---|
| `POST` | `/auth/login` | Público | Autentica e retorna access e refresh token |
| `POST` | `/auth/refresh` | Público | Gera um novo access token a partir do refresh token |
| `GET` | `/usuarios` | Autenticado | Lista usuários |
| `GET` | `/usuarios/{id}` | Autenticado | Busca usuário por id |
| `POST` | `/usuarios` | ADMIN | Cadastra usuário |
| `PUT` | `/usuarios/{id}` | ADMIN | Atualiza usuário |
| `DELETE` | `/usuarios/{id}` | ADMIN | Remove usuário |
| `GET` | `/clientes` | ADMIN | Lista clientes |
| `GET` | `/clientes/{id}` | ADMIN | Busca cliente por id |
| `POST` | `/clientes` | ADMIN | Cadastra cliente (endereço preenchido pelo CEP) |
| `GET` | `/mesas` | Autenticado | Lista mesas |
| `GET` | `/mesas/{id}` | Autenticado | Busca mesa por id |
| `POST` | `/mesas` | Autenticado | Cadastra mesa |
| `PUT` | `/mesas/{id}` | Autenticado | Atualiza mesa |
| `DELETE` | `/mesas/{id}` | Autenticado | Remove mesa |

Nas rotas protegidas, envie o token no cabeçalho:

```text
Authorization: Bearer <access_token>
```

A documentação completa fica disponível no Swagger, com a aplicação rodando:

```text
http://localhost:8082/swagger-ui/index.html
```

## Arquitetura

O projeto segue uma estrutura em camadas:

```text
src/main/java/com/restaurante/RestauranteAPI
├── config        # segurança (JWT, filtros) e mappers
├── controllers   # endpoints HTTP
├── dto           # objetos de entrada (request) e saída (response)
├── entities      # entidades JPA
├── enums         # perfis, status e tipos
├── exceptions    # exceções e tratamento global de erros
├── repositories  # acesso aos dados
└── services      # regras de negócio
```

## Como executar

- Subir o banco com "docker compose up -d"

### Pré-requisitos

- Java 17
- Docker
- Git

### 1. Clonar o repositório

```bash
git clone https://github.com/gabrielhpcosta/RestauranteAPI.git
cd RestauranteAPI
```

### 2. Configurar as variáveis de ambiente

Crie um arquivo `.env` na raiz do projeto a partir do `.env.example`:

```env
DB_URL=jdbc:postgresql://localhost:5432/nome_do_seu_banco
DB_USERNAME=postgres
DB_PASSWORD=sua_senha
JWT_SECRET=sua-chave-jwt
```

> Nunca envie o arquivo `.env` para o repositório.

### 3. Executar

Windows:

```bash
.\mvnw.cmd spring-boot:run
```

Linux/macOS:

```bash
./mvnw spring-boot:run
```

A API sobe em `http://localhost:8082`.

## Autor

**Gabriel Henrique Costa**

[LinkedIn](https://www.linkedin.com/in/gabrielhpcosta) · [GitHub](https://github.com/gabrielhpcosta)

Backend Developer focado em Java e Spring Boot.
