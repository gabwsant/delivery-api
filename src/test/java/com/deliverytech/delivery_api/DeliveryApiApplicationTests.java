package com.deliverytech.delivery_api;

import com.deliverytech.delivery_api.entity.Cliente;
import com.deliverytech.delivery_api.entity.Pedido;
import com.deliverytech.delivery_api.entity.Produto;
import com.deliverytech.delivery_api.entity.Restaurante;
import com.deliverytech.delivery_api.exception.RegraNegocioException;
import com.deliverytech.delivery_api.repository.ClienteRepository;
import com.deliverytech.delivery_api.repository.PedidoRepository;
import com.deliverytech.delivery_api.repository.ProdutoRepository;
import com.deliverytech.delivery_api.repository.RestauranteRepository;
import com.deliverytech.delivery_api.service.ClienteService;
import com.deliverytech.delivery_api.service.PedidoService;
import com.deliverytech.delivery_api.service.ProdutoService;
import com.deliverytech.delivery_api.service.RestauranteService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal; 
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
@ActiveProfiles("test")
@SpringBootTest
class DeliveryApiApplicationTests {

	@Autowired private ClienteRepository clienteRepository;
	@Autowired private ClienteService clienteService;
	@Autowired private RestauranteRepository restauranteRepository;
	@Autowired private RestauranteService restauranteService;
	@Autowired private PedidoRepository pedidoRepository;
	@Autowired private PedidoService pedidoService;
	@Autowired private ProdutoRepository produtoRepository;
	@Autowired private ProdutoService produtoService;


	@Test
	void contextLoads() {
	}

	@Test
	@Transactional
	void deveBuscarClienteAtivoPorEmail() {
		// Arrange
		Cliente cliente = new Cliente();
		cliente.setNome("Cliente Teste");
		cliente.setEmail("teste@email.com");
		cliente.setTelefone("11999999999");
		cliente.setEndereco("Rua Teste, 123");
		cliente.setDataCadastro(LocalDateTime.now());
		cliente.setAtivo(true);
		clienteRepository.save(cliente);

		// Act
		Optional<Cliente> resultado = clienteRepository.findByEmail("teste@email.com");

		// Assert
		assertTrue(resultado.isPresent(), "O cliente deveria ter sido encontrado pelo e-mail");
		assertTrue(resultado.get().isAtivo(), "O cliente encontrado deveria estar ativo");
	}

	@Test
	@Transactional // Boa prática
	void deveCadastrarRestaurante() {
		// Arrange
		Restaurante restaurante = new Restaurante();
		restaurante.setNome("Restaurante Teste");
		// O Service deve ser responsável por 'setAtivo' e 'setDataCadastro'

		// Act
		Restaurante salvo = restauranteService.cadastrar(restaurante);

		// Assert
		assertNotNull(salvo.getId(), "O ID deve ser gerado após o cadastro");
		assertEquals("Restaurante Teste", salvo.getNome());
		assertTrue(salvo.isAtivo());
		assertNotNull(salvo.getDataCadastro());

		Optional<Restaurante> encontrado = restauranteRepository.findById(salvo.getId());
		assertTrue(encontrado.isPresent(), "O restaurante deve existir no banco");
		assertEquals("Restaurante Teste", encontrado.get().getNome());
	}

	@Test
	@Transactional // Boa prática
	void deveAtualizarRestaurante() {
		// Arrange
		Restaurante restaurante = new Restaurante();
		restaurante.setNome("Antigo Nome");
		Restaurante salvo = restauranteService.cadastrar(restaurante);

		// Act
		Restaurante dadosUpdate = new Restaurante();
		dadosUpdate.setNome("Novo Nome");
		restauranteService.atualizar(salvo.getId(), dadosUpdate);

		// Assert
		Optional<Restaurante> resultado = restauranteRepository.findById(salvo.getId());
		assertTrue(resultado.isPresent(), "O restaurante deve existir");
		assertEquals("Novo Nome", resultado.get().getNome(), "O nome deve ter sido atualizado");
	}

	// --- TESTES DE PEDIDO CORRIGIDOS ---

	@Test
	@Transactional
	void deveCriarPedidoComSucesso() {
		// Arrange
		Cliente cliente = new Cliente();
		cliente.setNome("Gabriel");
		cliente.setAtivo(true);
		clienteRepository.save(cliente);

		Restaurante restaurante = new Restaurante();
		restaurante.setNome("Restaurante Bom Sabor");
		restaurante.setAtivo(true);
		restauranteRepository.save(restaurante);

		Produto produto1 = new Produto();
		produto1.setNome("Pizza");
		produto1.setPreco(new BigDecimal("30.00")); // CORRIGIDO
		produto1.setRestaurante(restaurante); // CORRIGIDO: Lógica de negócio
		produto1.setAtivo(true);
		produtoRepository.save(produto1);

		Produto produto2 = new Produto();
		produto2.setNome("Suco");
		produto2.setPreco(new BigDecimal("10.00")); // CORRIGIDO
		produto2.setRestaurante(restaurante); // CORRIGIDO: Lógica de negócio
		produto2.setAtivo(true);
		produtoRepository.save(produto2);

		// CORRIGIDO: O service agora espera um Map (ID do Produto -> Quantidade)
		// Pedindo: 1 Pizza e 2 Sucos
		Map<Long, Integer> itensPedido = Map.of(
				produto1.getId(), 1,
				produto2.getId(), 2
		);

		// Act
		Pedido pedido = pedidoService.criarPedido(cliente.getId(), restaurante.getId(), itensPedido);

		// Assert
		assertNotNull(pedido.getId());
		assertEquals(cliente.getId(), pedido.getCliente().getId());
		assertEquals(restaurante.getId(), pedido.getRestaurante().getId());

		// CORRIGIDO: Deve ter 2 ItemPedido, não 2 Produto
		assertEquals(2, pedido.getItens().size());

		// CORRIGIDO: O total é 1x30 + 2x10 = 50.00
		// Usar compareTo para comparar BigDecimal é mais seguro
		assertEquals(0, new BigDecimal("50.00").compareTo(pedido.getTotal()), "O total do pedido está incorreto");
		assertEquals("PENDENTE", pedido.getStatus());

		Optional<Pedido> encontrado = pedidoRepository.findById(pedido.getId());
		assertTrue(encontrado.isPresent());
	}

	@Test
	@Transactional
	void deveLancarExcecaoQuandoClienteInativo() {
		// Arrange
		Cliente cliente = new Cliente();
		cliente.setNome("Inativo");
		cliente.setAtivo(false); // Inativo
		clienteRepository.save(cliente);

		Restaurante restaurante = new Restaurante();
		restaurante.setNome("Restaurante Ativo");
		restaurante.setAtivo(true);
		restauranteRepository.save(restaurante);

		Produto produto = new Produto();
		produto.setNome("Produto Teste");
		produto.setPreco(new BigDecimal("20.00")); // CORRIGIDO
		produto.setRestaurante(restaurante); // CORRIGIDO: Lógica de negócio
		produto.setAtivo(true);
		produtoRepository.save(produto);

		Map<Long, Integer> item = Map.of(produto.getId(), 1);

		// Act + Assert
		assertThrows(RegraNegocioException.class, () ->
				pedidoService.criarPedido(
						cliente.getId(),
						restaurante.getId(),
						item // CORRIGIDO
				)
		);
	}

	@Test
	@Transactional
	void deveLancarExcecaoQuandoProdutoNaoPertenceAoRestaurante() {
		// Arrange
		Cliente cliente = new Cliente();
		cliente.setAtivo(true);
		clienteRepository.save(cliente);

		Restaurante restaurante1 = new Restaurante();
		restaurante1.setNome("Restaurante A");
		restaurante1.setAtivo(true);
		restauranteRepository.save(restaurante1);

		Restaurante restaurante2 = new Restaurante();
		restaurante2.setNome("Restaurante B");
		restaurante2.setAtivo(true);
		restauranteRepository.save(restaurante2);

		Produto produtoDoRestaurante2 = new Produto();
		produtoDoRestaurante2.setNome("Produto do B");
		produtoDoRestaurante2.setPreco(new BigDecimal("10.00"));
		produtoDoRestaurante2.setRestaurante(restaurante2); // <-- Pertence ao restaurante 2
		produtoDoRestaurante2.setAtivo(true);
		produtoRepository.save(produtoDoRestaurante2);

		Map<Long, Integer> item = Map.of(produtoDoRestaurante2.getId(), 1);

		// Act + Assert: Tenta pedir o produto do restaurante B no restaurante A
		assertThrows(RegraNegocioException.class, () ->
				pedidoService.criarPedido(
						cliente.getId(),
						restaurante1.getId(), // <-- Pedindo no restaurante 1
						item
				)
		);
	}


	@Test
	@Transactional
	void deveLancarExcecaoQuandoNenhumProdutoSelecionado() {
		// Arrange
		Cliente cliente = new Cliente();
		cliente.setNome("Cliente Teste");
		cliente.setAtivo(true);
		clienteRepository.save(cliente);

		Restaurante restaurante = new Restaurante();
		restaurante.setNome("Restaurante Teste");
		restaurante.setAtivo(true);
		restauranteRepository.save(restaurante);

		// Act + Assert
		assertThrows(RegraNegocioException.class, () ->
				pedidoService.criarPedido(
						cliente.getId(),
						restaurante.getId(),
						Collections.emptyMap() // CORRIGIDO: de emptyList para emptyMap
				)
		);
	}

	/**
	 * 🔎 Cenário 1: Busca de Cliente por Email
	 */
	@Test
	@Transactional
	void deveBuscarClientePorEmailCorretamente() {
		// Arrange
		Cliente cliente = new Cliente();
		cliente.setNome("Joao Silva");
		cliente.setEmail("joao@email.com");
		cliente.setAtivo(true);
		clienteRepository.save(cliente);

		// Act
		Optional<Cliente> resultado = clienteRepository.findByEmail("joao@email.com");

		// Assert
		assertTrue(resultado.isPresent(), "Cliente com e-mail joao@email.com deve ser encontrado");
		assertEquals("Joao Silva", resultado.get().getNome());
	}

	/**
	 * 🍔 Cenário 2: Produtos por Restaurante
	 */
	@Test
	@Transactional
	void deveBuscarProdutosPorRestauranteId() {
		// Arrange
		Restaurante r1 = new Restaurante();
		r1.setNome("Restaurante A");
		restauranteRepository.save(r1); // r1 terá o ID 1 (neste contexto de teste)

		Restaurante r2 = new Restaurante();
		r2.setNome("Restaurante B");
		restauranteRepository.save(r2);

		Produto p1 = new Produto();
		p1.setNome("Produto 1A");
		p1.setRestaurante(r1);
		produtoRepository.save(p1);

		Produto p2 = new Produto();
		p2.setNome("Produto 2A");
		p2.setRestaurante(r1);
		produtoRepository.save(p2);

		Produto p3 = new Produto();
		p3.setNome("Produto 3B");
		p3.setRestaurante(r2);
		produtoRepository.save(p3);

		// Act
		List<Produto> produtos = produtoRepository.findByRestauranteId(r1.getId());

		// Assert
		assertNotNull(produtos);
		assertEquals(2, produtos.size(), "Deveria encontrar 2 produtos para o Restaurante A");
		assertTrue(produtos.stream().allMatch(p -> p.getRestaurante().getId().equals(r1.getId())));
	}

	/**
	 * 📅 Cenário 3: Pedidos Recentes
	 */
	@Test
	@Transactional
	void deveBuscarTop10PedidosMaisRecentes() {
		// Arrange
		Cliente c = new Cliente(); c.setNome("Cliente Pedidos");
		clienteRepository.save(c);
		Restaurante r = new Restaurante(); r.setNome("Restaurante Pedidos");
		restauranteRepository.save(r);

		// Pedido 1 (Mais antigo)
		Pedido p1 = new Pedido();
		p1.setCliente(c); p1.setRestaurante(r);
		p1.setDataPedido(LocalDateTime.now().minusDays(2));
		pedidoRepository.save(p1);

		// Pedido 3 (Mais recente)
		Pedido p3 = new Pedido();
		p3.setCliente(c); p3.setRestaurante(r);
		p3.setDataPedido(LocalDateTime.now());
		pedidoRepository.save(p3);

		// Pedido 2 (Intermediário)
		Pedido p2 = new Pedido();
		p2.setCliente(c); p2.setRestaurante(r);
		p2.setDataPedido(LocalDateTime.now().minusDays(1));
		pedidoRepository.save(p2);

		// Act
		List<Pedido> pedidos = pedidoRepository.findTop10ByOrderByDataPedidoDesc();

		// Assert
		assertNotNull(pedidos);
		assertEquals(3, pedidos.size(), "Deveria encontrar 3 pedidos no total");

		// Verifica a ordem (o mais recente primeiro)
		assertEquals(p3.getId(), pedidos.get(0).getId(), "p3 deveria ser o primeiro (mais recente)");
		assertEquals(p2.getId(), pedidos.get(1).getId(), "p2 deveria ser o segundo");
		assertEquals(p1.getId(), pedidos.get(2).getId(), "p1 deveria ser o último (mais antigo)");
	}

	/**
	 * 💰 Cenário 4: Restaurantes por Taxa
	 */
	@Test
	@Transactional
	void deveBuscarRestaurantesPorTaxaDeEntrega() {
		// Arrange
		Restaurante r1 = new Restaurante();
		r1.setNome("Taxa Barata");
		r1.setTaxaEntrega(new BigDecimal("4.99"));
		restauranteRepository.save(r1);

		Restaurante r2 = new Restaurante();
		r2.setNome("Taxa Limite");
		r2.setTaxaEntrega(new BigDecimal("5.00"));
		restauranteRepository.save(r2);

		Restaurante r3 = new Restaurante();
		r3.setNome("Taxa Cara");
		r3.setTaxaEntrega(new BigDecimal("8.00"));
		restauranteRepository.save(r3);

		// Act
		List<Restaurante> restaurantes = restauranteRepository
				.findByTaxaEntregaLessThanEqual(new BigDecimal("5.00"));

		// Assert
		assertNotNull(restaurantes);
		assertEquals(2, restaurantes.size(), "Deveria encontrar 2 restaurantes com taxa <= 5.00");

		List<String> nomes = restaurantes.stream().map(Restaurante::getNome).toList();
		assertTrue(nomes.contains("Taxa Barata"), "Deve incluir o restaurante com taxa 4.99");
		assertTrue(nomes.contains("Taxa Limite"), "Deve incluir o restaurante com taxa 5.00");
		assertFalse(nomes.contains("Taxa Cara"), "Não deve incluir o restaurante com taxa 8.00");
	}
}