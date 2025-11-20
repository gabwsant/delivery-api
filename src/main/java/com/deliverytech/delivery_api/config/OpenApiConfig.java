package com.deliverytech.delivery_api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                // 2.4: Servidores (dev, prod)
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Servidor Local (DEV)"),
                        new Server().url("https://api.deliverytech.com.br").description("Servidor de Produção")
                ))
                // 2.4: Informações da API (título, versão, descrição)
                .info(new Info()
                        .title("DeliveryTech API - Sistema de Pedidos")
                        .version("v1.0.0")
                        .description("API RESTful para gerenciamento de restaurantes, produtos e pedidos de delivery.")
                        // 2.4: Contato e licença
                        .contact(new Contact()
                                .name("Equipe de Desenvolvimento")
                                .email("contato@deliverytech.com.br")
                                .url("https://www.deliverytech.com.br"))
                        .license(new License().name("Apache 2.0").url("http://springdoc.org")));
    }
}