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
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class DeliveryApiApplicationTests {

	@Autowired
	private ClienteRepository clienteRepository;

	@Autowired
	private ClienteService clienteService;

	@Autowired
	private RestauranteRepository restauranteRepository;

	@Autowired
	private RestauranteService restauranteService;

	@Autowired
	private PedidoRepository pedidoRepository;

	@Autowired
	private PedidoService pedidoService;

	@Autowired
	private ProdutoRepository produtoRepository;

	@Autowired
	private ProdutoService produtoService;


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

	@Test
	void deveCadastrarRestaurante() {
		// Arrange
		Restaurante restaurante = new Restaurante();
		restaurante.setNome("Restaurante Teste");
		restaurante.setAtivo(true);
		restaurante.setDataCadastro(LocalDateTime.now());

		// Act
		Restaurante salvo = restauranteService.cadastrar(restaurante);

		// Assert
		assertNotNull(salvo.getId(), "O ID deve ser gerado após o cadastro");
		assertEquals("Restaurante Teste", salvo.getNome());
		assertTrue(salvo.getAtivo());
		assertNotNull(salvo.getDataCadastro());

		// Verifica se realmente foi salvo no banco
		Optional<Restaurante> encontrado = restauranteRepository.findById(salvo.getId());
		assertTrue(encontrado.isPresent(), "O restaurante deve existir no banco");
		assertEquals("Restaurante Teste", encontrado.get().getNome());
	}

	@Test
	void deveAtualizarRestaurante() {
		// Arrange — cria e salva um restaurante inicial
		Restaurante restaurante = new Restaurante();
		restaurante.setNome("Antigo Nome");
		restaurante.setAtivo(true);
		restaurante.setDataCadastro(LocalDateTime.now());

		Restaurante salvo = restauranteService.cadastrar(restaurante);

		// Act — atualiza o nome
		salvo.setNome("Novo Nome");
		restauranteService.atualizar(salvo.getId(), salvo);

		// Assert — busca e valida se o nome foi atualizado
		Optional<Restaurante> resultado = restauranteRepository.findById(salvo.getId());
		assertTrue(resultado.isPresent(), "O restaurante deve existir");
		assertEquals("Novo Nome", resultado.get().getNome(), "O nome deve ter sido atualizado");
	}

	@Test
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
		produto1.setPreco(30.0);
		produtoRepository.save(produto1);

		Produto produto2 = new Produto();
		produto2.setNome("Suco");
		produto2.setPreco(10.0);
		produtoRepository.save(produto2);

		List<Long> produtosIds = List.of(produto1.getId(), produto2.getId());

		// Act
		Pedido pedido = pedidoService.criarPedido(cliente.getId(), restaurante.getId(), produtosIds);

		// Assert
		assertNotNull(pedido.getId());
		assertEquals(cliente.getId(), pedido.getCliente().getId());
		assertEquals(restaurante.getId(), pedido.getRestaurante().getId());
		assertEquals(2, pedido.getProdutos().size());
		assertEquals(40.0, pedido.getTotal());
		assertEquals("PENDENTE", pedido.getStatus());

		// Confirma que realmente foi salvo no banco
		Optional<Pedido> encontrado = pedidoRepository.findById(pedido.getId());
		assertTrue(encontrado.isPresent());
	}

	@Test
	void deveLancarExcecaoQuandoClienteInativo() {
		// Arrange
		Cliente cliente = new Cliente();
		cliente.setNome("Inativo");
		cliente.setAtivo(false);
		clienteRepository.save(cliente);

		Restaurante restaurante = new Restaurante();
		restaurante.setNome("Restaurante Ativo");
		restaurante.setAtivo(true);
		restauranteRepository.save(restaurante);

		Produto produto = new Produto();
		produto.setNome("Produto Teste");
		produto.setPreco(20.0);
		produtoRepository.save(produto);

		// Act + Assert
		assertThrows(RegraNegocioException.class, () ->
				pedidoService.criarPedido(
						cliente.getId(),
						restaurante.getId(),
						List.of(produto.getId())
				)
		);
	}

	@Test
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
						Collections.emptyList()
				)
		);
	}

}
