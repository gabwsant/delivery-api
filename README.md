# Delivery Tech API

Sistema de delivery desenvolvido com Spring Boot e Java 21.

## 🚀 Tecnologias
- **Java 21 LTS** (versão mais recente)
- Spring Boot 3.5.7
- Spring Web
- Spring Data JPA
- H2 Database
- Maven
- Lombok

## ⚡ Recursos Modernos Utilizados
- Records (Java 14+)
- Text Blocks (Java 15+)
- Pattern Matching (Java 17+)
- Virtual Threads (Java 21)

## 🏃‍♂️ Como executar
1. **Pré-requisitos:** JDK 21 instalado
2. Clone o repositório:

   git clone <URL_DO_REPOSITORIO>
   cd delivery-tech-api


3. Execute a aplicação:

   ./mvnw spring-boot:run

4. Acesse:

   * Health check: [http://localhost:8080/health](http://localhost:8080/health)
   * Informações da aplicação: [http://localhost:8080/info](http://localhost:8080/info)
   * Console H2: [http://localhost:8080/h2-console](http://localhost:8080/h2-console)

## 📋 Endpoints Principais

### Pedidos

| Método | Endpoint                                         | Descrição                            |
| ------ | ------------------------------------------------ | ------------------------------------ |
| GET    | /pedidos/cliente/{clienteId}                   | Lista todos os pedidos de um cliente |
| POST   | /pedidos                                       | Cria um novo pedido                  |
| PUT    | /pedidos/{pedidoId}/status?status=<novoStatus> | Atualiza o status de um pedido       |

### Produtos

| Método | Endpoint                                | Descrição                        |
| ------ | --------------------------------------- | -------------------------------- |
| GET    | /produtos/restaurante/{restauranteId} | Lista produtos de um restaurante |

## 🧪 Instruções de Teste

### 1. Criar um pedido

curl -X POST http://localhost:8080/pedidos \
-H "Content-Type: application/json" \
-d '{
  "clienteId": 1,
  "restauranteId": 1,
  "produtosIds": [1, 2, 3]
}'

### 2. Listar pedidos de um cliente

```bash
curl http://localhost:8080/pedidos/cliente/1
```

### 3. Atualizar status de um pedido

curl -X PUT "http://localhost:8080/pedidos/1/status?status=ENTREGUE"

### 4. Listar produtos de um restaurante

curl http://localhost:8080/produtos/restaurante/1

## 🔧 Configuração

* Porta: 8080
* Banco: H2 em memória
* Profile: development

## 👨‍💻 Desenvolvedor

Gabriel Barbosa Santos - Turma 535
Desenvolvido com JDK 21 e Spring Boot 3.5.7
