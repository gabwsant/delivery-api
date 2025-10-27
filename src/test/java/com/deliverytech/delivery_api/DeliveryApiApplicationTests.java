package com.deliverytech.delivery_api;

import com.deliverytech.delivery_api.entity.Cliente;
import com.deliverytech.delivery_api.repository.ClienteRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class DeliveryApiApplicationTests {

	@Autowired
	private ClienteRepository clienteRepository;

	@Test
	void contextLoads() {
	}

	@Test
	@Transactional
	void deveBuscarClienteAtivoPorEmail() {
		// Arrange — cria e salva um cliente ativo
		Cliente cliente = new Cliente();
		cliente.setNome("Cliente Teste");
		cliente.setEmail("teste@email.com");
		cliente.setTelefone("11999999999");
		cliente.setEndereco("Rua Teste, 123");
		cliente.setDataCadastro(LocalDateTime.now());
		cliente.setAtivo(true);

		clienteRepository.save(cliente);

		// Act — busca o cliente pelo e-mail
		Optional<Cliente> resultado = clienteRepository.findByEmail("teste@email.com");

		// Assert — verifica se o cliente existe e está ativo
		assertTrue(resultado.isPresent(), "O cliente deveria ter sido encontrado pelo e-mail");
		assertTrue(resultado.get().getAtivo(), "O cliente encontrado deveria estar ativo");
	}
}
