package com.deliverytech.delivery_api.service;

import com.deliverytech.delivery_api.entity.*; // Precisa do ItemPedido
import com.deliverytech.delivery_api.exception.EntidadeNaoEncontradaException;
import com.deliverytech.delivery_api.exception.RegraNegocioException;
import com.deliverytech.delivery_api.repository.ClienteRepository;
import com.deliverytech.delivery_api.repository.PedidoRepository;
import com.deliverytech.delivery_api.repository.ProdutoRepository;
import com.deliverytech.delivery_api.repository.RestauranteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList; // Importar
import java.util.List; // Importar
import java.util.Map; // Importar

@Service
public class PedidoService {
    private final PedidoRepository pedidoRepository;
    private final ClienteRepository clienteRepository;
    private final RestauranteRepository restauranteRepository;
    private final ProdutoRepository produtoRepository;
    // Não precisamos do ItemPedidoRepository aqui se usarmos Cascade

    public PedidoService(PedidoRepository pedidoRepository,
                         ClienteRepository clienteRepository,
                         RestauranteRepository restauranteRepository,
                         ProdutoRepository produtoRepository) {
        this.pedidoRepository = pedidoRepository;
        this.clienteRepository = clienteRepository;
        this.restauranteRepository = restauranteRepository;
        this.produtoRepository = produtoRepository;
    }

    /**
     * Cria um novo pedido.
     * @param clienteId ID do Cliente
     * @param restauranteId ID do Restaurante
     * @param itensPedido Map onde a Chave é o ID do Produto e o Valor é a Quantidade
     */
    @Transactional
    public Pedido criarPedido(Long clienteId, Long restauranteId, Map<Long, Integer> itensPedido) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Cliente não encontrado"));
        Restaurante restaurante = restauranteRepository.findById(restauranteId)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Restaurante não encontrado"));

        if(!cliente.isAtivo()) throw new RegraNegocioException("Cliente inativo");
        if(!restaurante.isAtivo()) throw new RegraNegocioException("Restaurante inativo");
        if(itensPedido == null || itensPedido.isEmpty()) {
            throw new RegraNegocioException("O pedido deve ter pelo menos um item");
        }

        // 1. Cria o Pedido "pai"
        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setRestaurante(restaurante);
        pedido.setStatus("PENDENTE");
        pedido.setDataPedido(LocalDateTime.now());

        List<ItemPedido> listaDeItens = new ArrayList<>();

        // 2. Processa os itens
        for (Map.Entry<Long, Integer> item : itensPedido.entrySet()) {
            Long produtoId = item.getKey();
            Integer quantidade = item.getValue();

            if (quantidade <= 0) {
                throw new RegraNegocioException("A quantidade do produto " + produtoId + " deve ser positiva");
            }

            Produto produto = produtoRepository.findById(produtoId)
                    .orElseThrow(() -> new EntidadeNaoEncontradaException("Produto " + produtoId + " não encontrado"));

            // Validação de negócio crucial:
            if (!produto.isAtivo()) {
                throw new RegraNegocioException("Produto " + produto.getNome() + " está indisponível");
            }
            if (!produto.getRestaurante().getId().equals(restauranteId)) {
                throw new RegraNegocioException("Produto " + produto.getNome() + " não pertence ao restaurante " + restaurante.getNome());
            }

            // 3. Cria o ItemPedido
            ItemPedido itemPedido = new ItemPedido();
            itemPedido.setPedido(pedido); // Linka o item ao pedido "pai"
            itemPedido.setProduto(produto);
            itemPedido.setQuantidade(quantidade);
            itemPedido.setPrecoUnitario(produto.getPreco()); // Salva o preço do momento da compra

            listaDeItens.add(itemPedido);
        }

        // 4. Finaliza o Pedido
        pedido.setItens(listaDeItens);
        pedido.calcularTotal(); // Usa o método helper da entidade

        // 5. Salva (Cascade.ALL salvará os ItemPedido juntos)
        return pedidoRepository.save(pedido);
    }

    @Transactional
    public Pedido atualizarStatus(Long pedidoId, String novoStatus) {
        // CORRIGIDO: Método 'findByIdWithItens' do repositório corrigido
        Pedido pedido = pedidoRepository.findByIdWithItens(pedidoId);
        if (pedido == null) throw new EntidadeNaoEncontradaException("Pedido não encontrado");

        pedido.setStatus(novoStatus);
        // O 'save' não é estritamente necessário dentro de @Transactional,
        // mas é uma boa prática para clareza.
        return pedidoRepository.save(pedido);
    }

    @Transactional(readOnly = true) // Boa prática para buscas complexas
    public List<Pedido> buscarPorCliente(Long clienteId) {
        if (!clienteRepository.existsById(clienteId)) {
            throw new EntidadeNaoEncontradaException("Cliente não encontrado");
        }

        // CORRIGIDO: Método 'findByClienteWithItens' do repositório corrigido
        return pedidoRepository.findByClienteWithItens(clienteId);
    }
}