# AquaCare - Backend

Backend do AquaCare, sistema de apoio a decisao para gerenciamento de aquarios, desenvolvido como Trabalho de Conclusao de Curso (TCC) por Brayan Alves dos Santos e Saulo Gabriel dos Santos Souza.

O sistema cadastra aquarios, especies de animais e plantas, parametros fisico-quimicos da agua e equipamentos, e aplica regras de negocio para apoiar o usuario na manutencao do aquario:

- Analise de toxicidade de amonia/nitrito a partir dos parametros registrados.
- Verificacao de compatibilidade entre especies (fisico-quimica e comportamental).
- Verificacao de dimensionamento de equipamentos (filtro, aquecedor, iluminacao) em relacao ao volume do aquario.

## Tecnologias

- Java 21
- Spring Boot 3.5.15 (Web, Data JPA, Security, Validation, Mail)
- MySQL 8
- JWT (biblioteca jjwt) para autenticacao
- springdoc-openapi (Swagger UI)
- Maven (via wrapper `mvnw`)

## Pre-requisitos

- JDK 21
- Docker e Docker Compose (para o banco MySQL local)

## Configuracao do ambiente

O projeto le variaveis de ambiente de um arquivo `.env` na raiz do repositorio (nao versionado). Crie um `.env` com as seguintes chaves:

```
MYSQL_ROOT_PASSWORD=
MYSQL_PASSWORD=
DATASOURCE_PASSWORD=
JWT_SECRET=
MAIL_USERNAME=
MAIL_PASSWORD=
```

Observacoes:

- `DATASOURCE_PASSWORD` precisa ser igual a `MYSQL_PASSWORD` (e a senha do usuario `aquacare` que a aplicacao usa para conectar no banco).
- `JWT_SECRET` deve ser uma string aleatoria em base64, usada para assinar os tokens de autenticacao.
- `MAIL_USERNAME`/`MAIL_PASSWORD` sao as credenciais SMTP usadas no fluxo de recuperacao de senha por email (ex: conta Gmail com senha de app).

## Subindo o banco de dados

```
docker compose up -d
```

Isso cria um container MySQL com o banco `aquacare`, publicado na porta `3308` do host (porta `3306` padrao do MySQL pode ja estar em uso localmente). Os dados persistem em um volume Docker entre reinicializacoes.

## Executando a aplicacao

```
./mvnw spring-boot:run
```

A aplicacao sobe em `http://localhost:8080`. As tabelas sao criadas/atualizadas automaticamente pelo Hibernate (`ddl-auto=update`) na primeira execucao.

## Documentacao da API

Com a aplicacao rodando, a documentacao interativa (Swagger UI) fica disponivel em:

```
http://localhost:8080/swagger-ui.html
```

O JSON da especificacao OpenAPI fica em `http://localhost:8080/v3/api-docs`.

## Autenticacao

A API usa autenticacao stateless via JWT. O fluxo basico:

1. `POST /usuarios/cadastrar` para criar uma conta.
2. `POST /usuarios/login` para autenticar e receber o token.
3. Enviar o token nas demais requisicoes no header `Authorization: Bearer <token>`.

Endpoints publicos (sem necessidade de token): login, cadastro de usuario, recuperacao e redefinicao de senha, e a documentacao Swagger. Todos os demais endpoints exigem autenticacao, e as operacoes de escrita no catalogo de animais/plantas exigem o papel `ADMIN`.

## Modulos principais

| Area | Prefixo | Descricao |
|---|---|---|
| Usuarios | `/usuarios` | Cadastro, login, recuperacao de senha, dados da conta |
| Aquarios | `/aquarios` | CRUD de aquarios, diagnostico, parametros ideais e especies recomendadas |
| Animais | `/animal` | Catalogo de especies de animais (somente ADMIN pode escrever) |
| Plantas | `/planta` | Catalogo de especies de plantas (somente ADMIN pode escrever) |
| Equipamentos | `/equipamento` | Equipamentos do aquario e verificacao de dimensionamento |
| Registros de parametro | `/registro` | Historico de medicoes fisico-quimicas e alertas gerados |
| Animal-Aquario | `/animal-aquario` | Associacao de animais a um aquario |
| Planta-Aquario | `/planta-aquario` | Associacao de plantas a um aquario |
| Lembretes | `/lembrete` | Lembretes de manutencao do aquario |
| Dispositivos de notificacao | `/dispositivo` | Tokens de push notification do usuario |

## Autores

- Brayan Alves dos Santos
- Saulo Gabriel dos Santos Souza
