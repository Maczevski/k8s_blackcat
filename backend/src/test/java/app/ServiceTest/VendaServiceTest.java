package app.ServiceTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import app.Entity.Produto;
import app.Entity.ProdutoVenda;
import app.auth.Usuario;
import app.Entity.Venda;
import app.Repository.VendaRepository;
import app.Service.ProdutoService;
import app.Service.UsuarioService;
import app.Service.VendaService;

@SpringBootTest
public class VendaServiceTest {

	@Autowired
	VendaService vendaService;

	@MockBean
	VendaRepository vendaRepository;

	@MockBean
	ProdutoService produtoService;

	@MockBean
	UsuarioService usuarioService;

	@BeforeEach
	void setup() {
		Usuario usuario01 = new Usuario(3L, "jose de amado", "jose123", "senha123", Usuario.Role.GESTOR, true);
		Produto produto01 = new Produto(1L, "torta de banana", "é uma torta e é de banana", 99.99, true);

		Venda venda01 = new Venda();
		venda01.setId(1L);
		venda01.setData(LocalDateTime.now());
		venda01.setDesconto(10);
		venda01.setFormaPagamento("Cartao de Debito");
		venda01.setUsuario(usuario01);
		venda01.setProdutosVenda(Arrays.asList());
		venda01.setTotal(89.99);

		Mockito.when(vendaRepository.save(Mockito.any())).thenReturn(venda01);
		Mockito.when(produtoService.findById(1L)).thenReturn(produto01);
		Mockito.when(usuarioService.findById(3L)).thenReturn(usuario01);
	}

	// unitarios
	
	@Test
	@DisplayName("Erro ao validar venda com usuário nulo")
	void ValidarVendaUsuarioNulo() {
		Venda venda = new Venda();
		venda.setUsuario(null);
		venda.setFormaPagamento("Cartão de Crédito");

		RuntimeException thrown = assertThrows(RuntimeException.class, () -> vendaService.validarVenda(venda));
		assertEquals("Erro: null foi desativado", thrown.getMessage());
	}
	
	@Test
	@DisplayName("Deve lançar exceção quando o usuário da venda está desativado")
	void ValidarVendaUsuarioDesativado() {
		Usuario usuarioDesativado = new Usuario();
		usuarioDesativado.setAtivo(false); 
		usuarioDesativado.setNome("José");

		Venda venda = new Venda();
		venda.setUsuario(usuarioDesativado); 
		venda.setFormaPagamento("Cartão de Crédito"); 
		RuntimeException thrown = assertThrows(RuntimeException.class, () -> vendaService.validarVenda(venda));
		assertEquals("Erro: José foi desativado", thrown.getMessage());
	}
	
	@Test
	@DisplayName("Validar uma venda com sucesso")
	void validarVendaSuccess() {
		
		Usuario usuario = new Usuario();
		usuario.setAtivo(true); 
		usuario.setNome("José");

		Venda venda = new Venda();
		venda.setUsuario(usuario); 
		venda.setFormaPagamento("Cartão de Crédito"); 
		assertDoesNotThrow(() -> vendaService.validarVenda(venda));
		
	}
	
	@Test
	@DisplayName("Somando ProdutosVenda Repetidos")
	void somarProdutosVenda() {
		List<ProdutoVenda> produtosVenda = new ArrayList<>();
		
		Produto produto = new Produto(1L, "Horchata", "Suco de arroz com canela - Copo 400ml", 10, true);
		
		ProdutoVenda produtoVenda1 = new ProdutoVenda(produto, 1);
		
		ProdutoVenda produtoVenda2 = new ProdutoVenda(produto, 2);
		
		produtosVenda.add(produtoVenda1);
		produtosVenda.add(produtoVenda2);
		
		List<ProdutoVenda> resultList = this.vendaService.juntarProdutosVendaIguais(produtosVenda);
		
		assertEquals(1, resultList.size());
		
		assertEquals(3, resultList.get(0).getQuantidade());
	}

	
	@Test
	@DisplayName("Lista sem ProdutosVenda repetidos")
	void nenhumProdutoVendaRepetido() {
		List<ProdutoVenda> produtosVenda = new ArrayList<>();
		
		Produto horchata = new Produto(1L, "Horchata", "Suco de arroz com canela - Copo 400ml", 10, true);
		
		Produto quesadilla = new Produto(2L, "Quesadilha", "Tortilha recheada", 15, true);
		
		ProdutoVenda produtoVenda1 = new ProdutoVenda(horchata, 1);
		
		ProdutoVenda produtoVenda2 = new ProdutoVenda(quesadilla, 2);
		
		produtosVenda.add(produtoVenda1);
		produtosVenda.add(produtoVenda2);
		
		List<ProdutoVenda> resultList = this.vendaService.juntarProdutosVendaIguais(produtosVenda);
		
		assertEquals(2, resultList.size());
		
	}
	
	
		
	// testes com sucesso
	
//	@Test
//	@DisplayName("Salvar venda com sucesso")
//	void SaveSuccess() {
//		Usuario usuario = new Usuario(1L, "José", "jose", "senha", Usuario.Role.GESTOR, true);
//		Produto produto = new Produto(1L, "Produto 1", "Descrição", 50.0, true);
//		ProdutoVenda produtoVenda = new ProdutoVenda();
//		produtoVenda.setProduto(produto);
//		produtoVenda.setQuantidade(2);
//
//		Venda venda = new Venda();
//		venda.setUsuario(usuario);
//		venda.setProdutosVenda(Collections.singletonList(produtoVenda));
//		venda.setDesconto(10);
//		venda.setFormaPagamento("Cartão de Crédito");
//
//		when(usuarioService.findById(1L)).thenReturn(usuario);
//		when(produtoService.findById(1L)).thenReturn(produto);
//		when(vendaRepository.save(venda)).thenReturn(venda);
//
//		String result = vendaService.save(venda);
//		assertEquals("Venda salva com sucesso", result);
//		verify(vendaRepository, times(1)).save(venda);
//	}

	@Test
	@DisplayName("Atualizar venda com sucesso")
	void UpdateSuccess() {
		Usuario usuario = new Usuario(1L, "José", "jose", "senha", Usuario.Role.GESTOR, true);
		Produto produto = new Produto(1L, "Produto 1", "Descrição", 50.0, true);
		ProdutoVenda produtoVenda = new ProdutoVenda();
		produtoVenda.setProduto(produto);
		produtoVenda.setQuantidade(2);

		Venda vendaExistente = new Venda();
		vendaExistente.setId(1L);
		vendaExistente.setUsuario(usuario);
		vendaExistente.setProdutosVenda(Collections.singletonList(produtoVenda));
		vendaExistente.setDesconto(10);
		vendaExistente.setFormaPagamento("Cartão de Crédito");
		vendaExistente.setData(LocalDateTime.now().minusDays(1));

		Venda vendaAtualizada = new Venda();
		vendaAtualizada.setUsuario(usuario);
		vendaAtualizada.setProdutosVenda(Collections.singletonList(produtoVenda));
		vendaAtualizada.setDesconto(15);
		vendaAtualizada.setFormaPagamento("Dinheiro");
		vendaAtualizada.setData(LocalDateTime.now());

		when(vendaRepository.findById(1L)).thenReturn(Optional.of(vendaExistente));
		when(vendaRepository.save(vendaAtualizada)).thenReturn(vendaAtualizada);

		String result = vendaService.update(vendaAtualizada, 1L);
		assertEquals("Atualizada com sucesso", result);
		verify(vendaRepository, times(1)).save(vendaAtualizada);
	}

	@Test
	@DisplayName("Buscar venda por ID com sucesso")
	void FindByIdSuccess() {
		Usuario usuario = new Usuario(1L, "José", "jose", "senha", Usuario.Role.GESTOR, true);
		Produto produto = new Produto(1L, "Produto 1", "Descrição", 50.0, true);
		ProdutoVenda produtoVenda = new ProdutoVenda();
		produtoVenda.setProduto(produto);
		produtoVenda.setQuantidade(2);

		Venda venda = new Venda();
		venda.setId(1L);
		venda.setUsuario(usuario);
		venda.setProdutosVenda(Collections.singletonList(produtoVenda));
		venda.setDesconto(10);
		venda.setFormaPagamento("Cartão de Crédito");

		when(vendaRepository.findById(1L)).thenReturn(Optional.of(venda));

		Venda result = vendaService.findById(1L);
		assertEquals(1L, result.getId());
		assertEquals("José", result.getUsuario().getNome());
		assertEquals(1L, result.getProdutosVenda().get(0).getProduto().getId());
	}

	@Test
	@DisplayName("Buscar todas as vendas com sucesso")
	void FindAll() {
		Usuario usuario = new Usuario(1L, "José", "jose", "senha", Usuario.Role.GESTOR, true);
		Produto produto = new Produto(1L, "Produto 1", "Descrição", 50.0, true);
		ProdutoVenda produtoVenda = new ProdutoVenda();
		produtoVenda.setProduto(produto);
		produtoVenda.setQuantidade(2);

		Venda venda = new Venda();
		venda.setId(1L);
		venda.setUsuario(usuario);
		venda.setProdutosVenda(Collections.singletonList(produtoVenda));
		venda.setDesconto(10);
		venda.setFormaPagamento("Cartão de Crédito");

		List<Venda> vendas = Collections.singletonList(venda);
		when(vendaRepository.findAll()).thenReturn(vendas);

		List<Venda> result = vendaService.findAll();
		assertEquals(1, result.size());
		assertEquals(1L, result.get(0).getId());
	}

	@Test
	@DisplayName("Deletar venda com sucesso")
	void DeleteSuccess() {
		Long vendaId = 1L;
		doNothing().when(vendaRepository).deleteById(vendaId);

		String result = vendaService.delete(vendaId);
		assertEquals("Venda deletada com sucesso", result);
		verify(vendaRepository, times(1)).deleteById(vendaId);
	}

	@Test
	@DisplayName("Verificar produtos com sucesso")
	void VerificarProdutosSuccess() {
		Usuario usuario = new Usuario(1L, "José", "jose", "senha", Usuario.Role.GESTOR, true);
		Produto produto = new Produto(1L, "Produto 1", "Descrição", 50.0, true);
		ProdutoVenda produtoVenda = new ProdutoVenda();
		produtoVenda.setProduto(produto);
		produtoVenda.setQuantidade(2);

		List<ProdutoVenda> produtosVenda = Collections.singletonList(produtoVenda);

		when(produtoService.findById(1L)).thenReturn(produto);

		List<ProdutoVenda> result = vendaService.verificarProdutos(produtosVenda);
		assertEquals(1, result.size());
		assertEquals(2, result.get(0).getQuantidade());
	}

	@Test
	@DisplayName("Calcular total com sucesso")
	void CalcularTotalSuccess() {
		Usuario usuario = new Usuario(1L, "José", "jose", "senha", Usuario.Role.GESTOR, true);
		Produto produto = new Produto(1L, "Produto 1", "Descrição", 50.0, true);
		ProdutoVenda produtoVenda = new ProdutoVenda();
		produtoVenda.setProduto(produto);
		produtoVenda.setQuantidade(2);

		Venda venda = new Venda();
		venda.setUsuario(usuario);
		venda.setProdutosVenda(Collections.singletonList(produtoVenda));
		venda.setDesconto(10);

		when(produtoService.findById(1L)).thenReturn(produto);

		double total = vendaService.calcularTotal(venda);
		assertEquals(90.0, total); // (50.0 * 2) * (1 - 0.10)
	}

	@Test
	@DisplayName("Buscar vendas por data com sucesso")
	void FindByData() {
		Usuario usuario = new Usuario(1L, "José", "jose", "senha", Usuario.Role.GESTOR, true);
		Produto produto = new Produto(1L, "Produto 1", "Descrição", 50.0, true);
		ProdutoVenda produtoVenda = new ProdutoVenda();
		produtoVenda.setProduto(produto);
		produtoVenda.setQuantidade(2);

		Venda venda = new Venda();
		venda.setId(1L);
		venda.setUsuario(usuario);
		venda.setProdutosVenda(Collections.singletonList(produtoVenda));
		venda.setDesconto(10);
		venda.setFormaPagamento("Cartão de Crédito");
		venda.setData(LocalDateTime.now());

		LocalDateTime startDate = LocalDateTime.now().minusDays(1);
		LocalDateTime endDate = LocalDateTime.now().plusDays(1);

		List<Venda> vendas = Collections.singletonList(venda);
		when(vendaRepository.findByDataBetween(startDate, endDate)).thenReturn(vendas);

		List<Venda> result = vendaService.findByData(startDate, endDate);
		assertEquals(1, result.size());
		assertEquals(1L, result.get(0).getId());
	}

	@Test
	@DisplayName("Buscar vendas por mês e ano com sucesso")
	void FindByMonthAndYear() {
		Usuario usuario = new Usuario(1L, "José", "jose", "senha", Usuario.Role.GESTOR, true);
		Produto produto = new Produto(1L, "Produto 1", "Descrição", 50.0, true);
		ProdutoVenda produtoVenda = new ProdutoVenda();
		produtoVenda.setProduto(produto);
		produtoVenda.setQuantidade(2);

		Venda venda = new Venda();
		venda.setId(1L);
		venda.setUsuario(usuario);
		venda.setProdutosVenda(Collections.singletonList(produtoVenda));
		venda.setDesconto(10);
		venda.setFormaPagamento("Cartão de Crédito");
		venda.setData(LocalDateTime.of(2024, 9, 15, 10, 0));

		YearMonth yearMonth = YearMonth.of(2024, 9);
		LocalDateTime startDate = yearMonth.atDay(1).atStartOfDay();
		LocalDateTime endDate = yearMonth.atEndOfMonth().atTime(23, 59, 59);

		List<Venda> vendas = Collections.singletonList(venda);
		when(vendaRepository.findByDataBetween(startDate, endDate)).thenReturn(vendas);

		List<Venda> result = vendaService.findByMonthAndYear(9, 2024);
		assertEquals(1, result.size());
		assertEquals(1L, result.get(0).getId());
	}

	@Test
	@DisplayName("Buscar vendas por ID do usuário com sucesso")
	void FindByUsuarioId() {
		Usuario usuario = new Usuario(1L, "José", "jose", "senha", Usuario.Role.GESTOR, true);
		Produto produto = new Produto(1L, "Produto 1", "Descrição", 50.0, true);
		ProdutoVenda produtoVenda = new ProdutoVenda();
		produtoVenda.setProduto(produto);
		produtoVenda.setQuantidade(2);

		Venda venda = new Venda();
		venda.setId(1L);
		venda.setUsuario(usuario);
		venda.setProdutosVenda(Collections.singletonList(produtoVenda));
		venda.setDesconto(10);
		venda.setFormaPagamento("Cartão de Crédito");

		List<Venda> vendas = Collections.singletonList(venda);
		when(vendaRepository.findByUsuarioId(1L)).thenReturn(vendas);

		List<Venda> result = vendaService.findByUsuarioId(1L);
		assertEquals(1, result.size());
		assertEquals(1L, result.get(0).getId());
	}
	
	//Testes com exceções
	
//	@Test
//	@DisplayName("Erro ao tentar salvar venda com forma de pagamento inválida")
//	void PagamentoInvalida() {
//		Usuario usuario = new Usuario(1L, "José", "jose", "senha", Usuario.Role.GESTOR, true);
//		Produto produto = new Produto(1L, "Produto 1", "Descrição", 50.0, true);
//		ProdutoVenda produtoVenda = new ProdutoVenda();
//		produtoVenda.setProduto(produto);
//		produtoVenda.setQuantidade(2);
//		
//		Venda venda = new Venda();
//		venda.setUsuario(usuario);
//		venda.setProdutosVenda(Collections.singletonList(produtoVenda));
//		venda.setDesconto(10);
//		venda.setFormaPagamento("Forma Inválida");
//		
//		when(usuarioService.findById(1L)).thenReturn(usuario);
//		when(produtoService.findById(1L)).thenReturn(produto);
//		
//		
//		RuntimeException thrown = assertThrows(RuntimeException.class, () -> vendaService.save(venda));
//		assertEquals("Forma de pagamento inválida", thrown.getMessage());
//	}
	
//	@Test
//	@DisplayName("Lançar exceção quando o ano for maior que o ano atual")
//	void AnoInvalido() {
//	  
//	    int anoInvalido = LocalDateTime.now().getYear() + 1; 
//	    int mes = 5; 
//
//	    RuntimeException exception = assertThrows(RuntimeException.class, () -> {
//	        vendaService.findByMonthAndYear(mes, anoInvalido);
//	    });
//
//	    assertEquals("O ano inserido não é válido", exception.getMessage());
//	}

	
	// testes em caso de erro
//	@Test
//	@DisplayName("Erro ao tentar salvar venda com usuário inativo")
//	void UsuarioInativo() {
//		Usuario usuarioInativo = new Usuario(1L, "José", "jose", "senha", Usuario.Role.GESTOR, false);
//		Produto produto = new Produto(1L, "Produto 1", "Descrição", 50.0, true);
//		ProdutoVenda produtoVenda = new ProdutoVenda();
//		produtoVenda.setProduto(produto);
//		produtoVenda.setQuantidade(2);
//
//		Venda venda = new Venda();
//		venda.setUsuario(usuarioInativo);
//		venda.setProdutosVenda(Collections.singletonList(produtoVenda));
//		venda.setDesconto(10);
//		venda.setFormaPagamento("Cartão de Crédito");
//
//		when(usuarioService.findById(1L)).thenReturn(usuarioInativo);
//
//		RuntimeException thrown = assertThrows(RuntimeException.class, () -> vendaService.save(venda));
//		assertEquals("Erro: José foi desativado", thrown.getMessage());
//	}

//	@Test
//	@DisplayName("Erro ao tentar salvar venda com lista de produtos vazia")
//	void ProdutosVazios() {
//		Usuario usuario = new Usuario(1L, "José", "jose", "senha", Usuario.Role.GESTOR, true);
//
//		Venda venda = new Venda();
//		venda.setUsuario(usuario);
//		venda.setProdutosVenda(Collections.emptyList()); // Lista vazia
//		venda.setDesconto(10);
//		venda.setFormaPagamento("Cartão de Crédito");
//		when(usuarioService.findById(1L)).thenReturn(usuario);
//
//
//		RuntimeException thrown = assertThrows(RuntimeException.class, () -> vendaService.save(venda));
//
//		assertEquals("A lista de produtos não pode estar vazia", thrown.getMessage());
//	}

//	@Test
//	@DisplayName("Erro ao tentar salvar venda com usuário não encontrado")
//	void UsuarioNaoEncontrado() {
//		Venda venda = new Venda();
//		venda.setUsuario(new Usuario(1L, "José", "jose", "senha", Usuario.Role.GESTOR, true));
//		venda.setProdutosVenda(Collections.emptyList());
//		venda.setDesconto(10);
//		venda.setFormaPagamento("Cartão de Crédito");
//
//		when(usuarioService.findById(1L)).thenReturn(null);
//
//		RuntimeException thrown = assertThrows(RuntimeException.class, () -> vendaService.save(venda));
//		assertEquals("Usuario não encontrado", thrown.getMessage());
//	}

	

//	@Test
//	@DisplayName("Erro ao tentar salvar venda com produto inativo")
//	void SaveProdutoInativo() {
//		Usuario usuario = new Usuario(1L, "José", "jose", "senha", Usuario.Role.GESTOR, true);
//		Produto produtoInativo = new Produto(1L, "Produto 1", "Descrição", 50.0, false);
//		ProdutoVenda produtoVenda = new ProdutoVenda();
//		produtoVenda.setProduto(produtoInativo);
//		produtoVenda.setQuantidade(2);
//
//		Venda venda = new Venda();
//		venda.setUsuario(usuario);
//		venda.setProdutosVenda(Collections.singletonList(produtoVenda));
//		venda.setDesconto(10);
//		venda.setFormaPagamento("Cartão de Crédito");
//
//		when(usuarioService.findById(1L)).thenReturn(usuario);
//		when(produtoService.findById(1L)).thenReturn(produtoInativo);
//
//		RuntimeException thrown = assertThrows(RuntimeException.class, () -> vendaService.save(venda));
//		assertEquals("Erro: o produto Produto 1 foi desativado.", thrown.getMessage());
//	}
//


	@Test
	@DisplayName("Testar a combinação de produtos duplicados em verificarProdutos")
	void ProdutosDuplicados() {

		Produto produto = new Produto(1L, "Produto 1", "Descrição", 50.0, true);

		ProdutoVenda produtoVenda1 = new ProdutoVenda(produto, 2);
		ProdutoVenda produtoVenda2 = new ProdutoVenda(produto, 3);

		List<ProdutoVenda> produtosVenda = Arrays.asList(produtoVenda1, produtoVenda2);

		when(produtoService.findById(1L)).thenReturn(produto);

		List<ProdutoVenda> result = vendaService.verificarProdutos(produtosVenda);

		assertEquals(1, result.size());
		assertEquals(5, result.get(0).getQuantidade());
	}

	@Test
	@DisplayName("Erro ao tentar atualizar venda com usuário inativo")
	void UpdateUsuarioInativo() {
		Usuario usuarioInativo = new Usuario(1L, "José", "jose", "senha", Usuario.Role.GESTOR, false);
		Produto produto = new Produto(1L, "Produto 1", "Descrição", 50.0, true);
		ProdutoVenda produtoVenda = new ProdutoVenda();
		produtoVenda.setProduto(produto);
		produtoVenda.setQuantidade(2);

		Venda vendaAtualizada = new Venda();
		vendaAtualizada.setUsuario(usuarioInativo);
		vendaAtualizada.setProdutosVenda(Collections.singletonList(produtoVenda));
		vendaAtualizada.setDesconto(15);
		vendaAtualizada.setFormaPagamento("Dinheiro");

		when(vendaRepository.findById(1L)).thenReturn(Optional.of(vendaAtualizada));

		RuntimeException thrown = assertThrows(RuntimeException.class, () -> vendaService.update(vendaAtualizada, 1L));
		assertEquals("Erro: José foi desativado", thrown.getMessage());
	}

	@Test
	@DisplayName("Erro ao tentar atualizar venda não encontrada")
	void UpdateVendaNaoEncontrada() {
		Usuario usuario = new Usuario(1L, "José", "jose", "senha", Usuario.Role.GESTOR, true);
		Produto produto = new Produto(1L, "Produto 1", "Descrição", 50.0, true);
		ProdutoVenda produtoVenda = new ProdutoVenda();
		produtoVenda.setProduto(produto);
		produtoVenda.setQuantidade(2);

		Venda vendaAtualizada = new Venda();
		vendaAtualizada.setUsuario(usuario);
		vendaAtualizada.setProdutosVenda(Collections.singletonList(produtoVenda));
		vendaAtualizada.setDesconto(15);
		vendaAtualizada.setFormaPagamento("Dinheiro");

		when(vendaRepository.findById(1L)).thenReturn(Optional.empty());

		RuntimeException thrown = assertThrows(RuntimeException.class, () -> vendaService.update(vendaAtualizada, 1L));
		assertEquals("Venda não encontrada", thrown.getMessage());
	}

	@Test
	@DisplayName("Erro ao buscar venda por ID não encontrada")
	void FindByIdNotFound() {
		when(vendaRepository.findById(1L)).thenReturn(Optional.empty());

		RuntimeException thrown = assertThrows(RuntimeException.class, () -> vendaService.findById(1L));
		assertEquals("Venda não encontrada", thrown.getMessage());
	}

	@Test
	@DisplayName("Erro ao verificar produtos com produto não encontrado")
	void VerificarProdutoNaoEncontrado() {
		ProdutoVenda produtoVenda = new ProdutoVenda();
		produtoVenda.setProduto(new Produto(1L, "Produto 1", "Descrição", 50.0, true));
		produtoVenda.setQuantidade(2);

		List<ProdutoVenda> produtosVenda = Collections.singletonList(produtoVenda);

		when(produtoService.findById(1L)).thenReturn(null);

		RuntimeException thrown = assertThrows(RuntimeException.class,
				() -> vendaService.verificarProdutos(produtosVenda));
		assertEquals("Produto não encontrado", thrown.getMessage());
	}

	@Test
	@DisplayName("Erro ao verificar produtos com produto inativo")
	void VerificarProdutoInativo() {
		Produto produtoInativo = new Produto(1L, "Produto 1", "Descrição", 50.0, false);
		ProdutoVenda produtoVenda = new ProdutoVenda();
		produtoVenda.setProduto(produtoInativo);
		produtoVenda.setQuantidade(2);

		List<ProdutoVenda> produtosVenda = Collections.singletonList(produtoVenda);

		when(produtoService.findById(1L)).thenReturn(produtoInativo);

		RuntimeException thrown = assertThrows(RuntimeException.class,
				() -> vendaService.verificarProdutos(produtosVenda));
		assertEquals("Erro: o produto Produto 1 foi desativado.", thrown.getMessage());
	}

	@Test
	@DisplayName("Testar cálculo do total com produto existente (id != 0)")
	void CalcularTotalComProdutoExistente() {
		Produto produtoExistente = new Produto(0L, "Produto 1", "Descrição", 30.0, true);
		ProdutoVenda produtoVenda = new ProdutoVenda();
		produtoVenda.setProduto(produtoExistente);
		produtoVenda.setQuantidade(3);

		Venda venda = new Venda();
		venda.setProdutosVenda(Arrays.asList(produtoVenda));
		venda.setDesconto(10);

		when(produtoService.findById(1L)).thenReturn(produtoExistente);

		double total = vendaService.calcularTotal(venda);

		assertEquals(81.0, total);
	}

	

}
