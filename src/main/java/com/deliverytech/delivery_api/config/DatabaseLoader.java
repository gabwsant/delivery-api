package com.deliverytech.delivery_api.config;

import com.deliverytech.delivery_api.entity.*;
import com.deliverytech.delivery_api.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Profile("!test")
@Component
public class DatabaseLoader implements CommandLineRunner {

    @Autowired private RestauranteRepository restauranteRepository;
    @Autowired private ClienteRepository clienteRepository;
    @Autowired private ProdutoRepository produtoRepository;
    @Autowired private PedidoRepository pedidoRepository;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("--- 2.1 INICIANDO CARGA DE DADOS DE TESTE ---");

        Restaurante r1 = new Restaurante();
        r1.setNome("Pizzaria do Bairro");
        r1.setCategoria("Pizza");
        r1.setAtivo(true);
        r1.setAvaliacao(4.5);
        r1.setTaxaEntrega(new BigDecimal("5.00"));

        Restaurante r2 = new Restaurante();
        r2.setNome("Sushi Master");
        r2.setCategoria("Japonesa");
        r2.setAtivo(true);
        r2.setAvaliacao(4.8);
        r2.setTaxaEntrega(new BigDecimal("10.00"));

        restauranteRepository.saveAll(Arrays.asList(r1, r2));

        Cliente c1 = new Cliente();
        c1.setNome("Maria Oliveira");
        c1.setEmail("maria@email.com");
        c1.setAtivo(true);
        Cliente c2 = new Cliente();
        c2.setNome("João Santos");
        c2.setEmail("joao@email.com");
        c2.setAtivo(true);
        Cliente c3 = new Cliente();
        c3.setNome("Pedro Alves");
        c3.setEmail("pedro@email.com");
        c3.setAtivo(true);
        clienteRepository.saveAll(Arrays.asList(c1, c2, c3));

        Produto p1 = new Produto();
        p1.setNome("Pizza Margherita");
        p1.setPreco(new BigDecimal("45.00"));
        p1.setRestaurante(r1);
        p1.setAtivo(true);

        Produto p2 = new Produto();
        p2.setNome("Pizza Calabresa");
        p2.setPreco(new BigDecimal("50.00"));
        p2.setRestaurante(r1);
        p2.setAtivo(true);

        Produto p3 = new Produto();
        p3.setNome("Combinado 20 peças");
        p3.setPreco(new BigDecimal("80.00"));
        p3.setRestaurante(r2);
        p3.setAtivo(true);

        Produto p4 = new Produto();
        p4.setNome("Temaki Salmão");
        p4.setPreco(new BigDecimal("25.00"));
        p4.setRestaurante(r2);
        p4.setAtivo(true);

        Produto p5 = new Produto();
        p5.setNome("Refrigerante 2L");
        p5.setPreco(new BigDecimal("10.00"));
        p5.setRestaurante(r1);
        p5.setAtivo(true);

        produtoRepository.saveAll(Arrays.asList(p1, p2, p3, p4, p5));

        // --- CRIAÇÃO DE PEDIDOS (MODELO NOVO COM ITEMPEDIDO) ---

        // Pedido 1: Maria (c1) pede na Pizzaria (r1)
        // 1x Pizza Margherita (p1)
        // 2x Refrigerante 2L (p5)
        Pedido ped1 = new Pedido();
        ped1.setCliente(c1);
        ped1.setRestaurante(r1);
        ped1.setDataPedido(LocalDateTime.now());
        ped1.setStatus("EM_PREPARO");

        ItemPedido item1 = new ItemPedido();
        item1.setPedido(ped1);
        item1.setProduto(p1);
        item1.setQuantidade(1);
        item1.setPrecoUnitario(p1.getPreco());

        ItemPedido item2 = new ItemPedido();
        item2.setPedido(ped1);
        item2.setProduto(p5);
        item2.setQuantidade(2);
        item2.setPrecoUnitario(p5.getPreco());

        ped1.setItens(Arrays.asList(item1, item2));
        ped1.calcularTotal(); // Calcula o total com base nos itens

        // Pedido 2: João (c2) pede no Sushi (r2)
        // 1x Combinado 20 peças (p3)
        Pedido ped2 = new Pedido();
        ped2.setCliente(c2);
        ped2.setRestaurante(r2);
        ped2.setDataPedido(LocalDateTime.now().minusHours(1));
        ped2.setStatus("ENTREGUE");

        ItemPedido item3 = new ItemPedido();
        item3.setPedido(ped2);
        item3.setProduto(p3);
        item3.setQuantidade(1);
        item3.setPrecoUnitario(p3.getPreco());

        ped2.setItens(Arrays.asList(item3));
        ped2.calcularTotal();

        // Salva os pedidos. O CascadeType.ALL salvará os ItemPedido juntos.
        pedidoRepository.saveAll(Arrays.asList(ped1, ped2));

        System.out.println("--- CARGA DE DADOS CONCLUÍDA ---");

        System.out.println("\n--- 2.2 INICIANDO VALIDAÇÃO DAS CONSULTAS (RestauranteRepository) ---");

        // 1. findByNomeContainingIgnoreCase
        System.out.println("\n1. findByNomeContainingIgnoreCase('pizzaria'):");
        List<Restaurante> res1 = restauranteRepository.findByNomeContainingIgnoreCase("pizzaria");
        res1.forEach(r -> System.out.println("   -> " + r.getNome()));

        // 2. findByCategoria
        System.out.println("\n2. findByCategoria('Japonesa'):");
        List<Restaurante> res2 = restauranteRepository.findByCategoria("Japonesa");
        res2.forEach(r -> System.out.println("   -> " + r.getNome()));

        // 3. findByAtivoTrue
        System.out.println("\n3. findByAtivoTrue():");
        List<Restaurante> res3 = restauranteRepository.findByAtivoTrue();
        System.out.println("   -> Total de ativos: " + res3.size());

        // 4. findAtivosOrderByAvaliacao
        System.out.println("\n4. findAtivosOrderByAvaliacao (DESC):");
        List<Restaurante> res4 = restauranteRepository.findAtivosOrderByAvaliacao();
        res4.forEach(r -> System.out.println("   -> " + r.getNome() + " | Avaliação: " + r.getAvaliacao()));

        // 5. findByTaxaEntregaLessThanEqual
        System.out.println("\n5. findByTaxaEntregaLessThanEqual(8.00):");
        List<Restaurante> res5 = restauranteRepository.findByTaxaEntregaLessThanEqual(new BigDecimal("8.00"));
        res5.forEach(r -> System.out.println("   -> " + r.getNome() + " | Taxa: " + r.getTaxaEntrega()));

        // 6. findTop5ByOrderByNomeAsc
        System.out.println("\n6. findTop5ByOrderByNomeAsc():");
        List<Restaurante> res6 = restauranteRepository.findTop5ByOrderByNomeAsc();
        res6.forEach(r -> System.out.println("   -> " + r.getNome()));

        System.out.println("\n--- VALIDAÇÃO DAS CONSULTAS CONCLUÍDA ---");
    }
}