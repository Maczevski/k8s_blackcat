package app.ControllerTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import app.Controller.ProdutoController;
import app.Entity.Produto;
import app.Service.ProdutoService;

@SpringBootTest
public class ProdutoControllerTest {

	@Autowired
	private ProdutoController produtoController;

	@MockBean
	private ProdutoService produtoService; // Adicionar MockBean para o ProdutoService

	@BeforeEach
	void setup() {
		Produto produto = new Produto();
		produto.setId(1L);
		produto.setNome("Batata crua");
		produto.setDescricao("Batata crua com casca colhida na esquina da macumba");
		produto.setPreco(99.99);
		produto.setAtivo(true);

		// Mock para simular a busca de um produto
		when(produtoService.findAll(true)).thenReturn(Arrays.asList(produto));
		when(produtoService.delete(1L)).thenReturn("Produto excluído com sucesso");
		when(produtoService.disable(1L)).thenReturn("Produto desativado com sucesso");
		when(produtoService.enable(1L)).thenReturn("Produto ativado com sucesso");
	}

	@Test
	@DisplayName("SALVAR PRODUTO OK")
	void SalvarProdutoOk() {
		Produto novoProduto = new Produto();
		novoProduto.setNome("Produto Teste");
		novoProduto.setDescricao("Descrição Teste");
		novoProduto.setPreco(100.0);
		novoProduto.setAtivo(true);

		Mockito.when(produtoService.save(novoProduto)).thenReturn("Produto salvo com sucesso");

		ResponseEntity<String> resposta = produtoController.save(novoProduto);

		assertEquals(HttpStatus.OK, resposta.getStatusCode());
		assertEquals("Produto salvo com sucesso", resposta.getBody());
	}

	@Test
	@DisplayName("ERRO SALVAR PRODUTO")
	void ErroAoSalvarProduto() {
		Produto novoProduto = new Produto();
		novoProduto.setNome("Produto Teste");
		novoProduto.setDescricao("Descrição Teste");
		novoProduto.setPreco(100.0);
		novoProduto.setAtivo(true);

		Mockito.when(produtoService.save(novoProduto)).thenThrow(new RuntimeException("Erro inesperado"));

		ResponseEntity<String> resposta = produtoController.save(novoProduto);

		assertEquals(HttpStatus.BAD_REQUEST, resposta.getStatusCode());
		assertEquals("Erro: Erro inesperado", resposta.getBody());
	}

	@Test
	@DisplayName("ATUALIZAR PRODUTO COM SUCESSO")
	void AtualizarProdutoOk() {
		Produto produtoAtualizado = new Produto();
		produtoAtualizado.setNome("Produto Atualizado");
		produtoAtualizado.setDescricao("Descrição Atualizada");
		produtoAtualizado.setPreco(120.0);
		produtoAtualizado.setAtivo(true);

		Mockito.when(produtoService.update(produtoAtualizado, 1L)).thenReturn("Produto atualizado com sucesso");

		ResponseEntity<String> resposta = produtoController.update(produtoAtualizado, 1L);

		assertEquals(HttpStatus.OK, resposta.getStatusCode());
		assertEquals("Produto atualizado com sucesso", resposta.getBody());
	}

	@Test
	@DisplayName("ERRO ATUALIZAR PRODUTO")
	void ErroAoAtualizarProduto() {
		Produto produtoAtualizado = new Produto();
		produtoAtualizado.setNome("Produto Atualizado");
		produtoAtualizado.setDescricao("Descrição Atualizada");
		produtoAtualizado.setPreco(120.0);
		produtoAtualizado.setAtivo(true);

		Mockito.when(produtoService.update(produtoAtualizado, 1L)).thenThrow(new RuntimeException("Erro ao atualizar"));

		ResponseEntity<String> resposta = produtoController.update(produtoAtualizado, 1L);

		assertEquals(HttpStatus.BAD_REQUEST, resposta.getStatusCode());
		assertEquals("Deu erro! Erro ao atualizar", resposta.getBody());
	}

	@Test
	@DisplayName("BUSCAR PRODUTO POR ID")
	void BuscarProdutoPorIdOk() {
		Produto produto = new Produto();
		produto.setId(1L);
		produto.setNome("Produto Encontrado");

		Mockito.when(produtoService.findById(1L)).thenReturn(produto);

		ResponseEntity<Produto> resposta = produtoController.findById(1L);

		assertEquals(HttpStatus.OK, resposta.getStatusCode());
		assertEquals(produto, resposta.getBody());
	}

	@Test
	@DisplayName("ERRO AO BUSCAR PORDUTO POR ID")
	void ErroAoBuscarProdutoPorId() {
		Mockito.when(produtoService.findById(1L)).thenThrow(new RuntimeException("Produto não encontrado"));

		ResponseEntity<Produto> resposta = produtoController.findById(1L);

		assertEquals(HttpStatus.BAD_REQUEST, resposta.getStatusCode());
		assertNull(resposta.getBody());
	}

	@Test
	@DisplayName("LISTAR TODOS OS PRODUTOS ATIVOS")
	void FindAllAtivoOk() {
		ResponseEntity<List<Produto>> resposta = produtoController.findAll(true);

		assertEquals(HttpStatus.OK, resposta.getStatusCode());
		assertEquals(1, resposta.getBody().size());
	}

	@Test
	@DisplayName("ERRO AO LISTAR PRODUTOS")
	void ErroAoBuscarProdutos() {
		when(produtoService.findAll(true)).thenThrow(new RuntimeException("Erro ao buscar produtos"));

		ResponseEntity<List<Produto>> resposta = produtoController.findAll(true);

		assertEquals(HttpStatus.BAD_REQUEST, resposta.getStatusCode());
		assertNull(resposta.getBody());
	}

	@Test
	@DisplayName("EXCLUIR PRODUTO OK")
	void DeleteProdutoOk() {
		ResponseEntity<String> resposta = produtoController.delete(1L);

		assertEquals(HttpStatus.OK, resposta.getStatusCode());
		assertEquals("Produto excluído com sucesso", resposta.getBody());
	}

	@Test
	@DisplayName("ERRO AO DELETAR PRODUTO")
	void ErroDeleteProduto() {
		when(produtoService.delete(1L)).thenThrow(new RuntimeException("Erro ao excluir produto"));

		ResponseEntity<String> resposta = produtoController.delete(1L);

		assertEquals(HttpStatus.BAD_REQUEST, resposta.getStatusCode());
		assertNull(resposta.getBody());
	}

	@Test
	@DisplayName("DESATIVAR PRODUTO")
	void DesativarProdutoOk() {
		ResponseEntity<String> resposta = produtoController.disable(1L);

		assertEquals(HttpStatus.OK, resposta.getStatusCode());
		assertEquals("Produto desativado com sucesso", resposta.getBody());
	}

	@Test
	@DisplayName("ERRO AO DESATIVAR PRODUTO")
	void ErroDesativarProduto() {
		when(produtoService.disable(1L)).thenThrow(new RuntimeException("Erro ao desativar produto"));

		ResponseEntity<String> resposta = produtoController.disable(1L);

		assertEquals(HttpStatus.BAD_REQUEST, resposta.getStatusCode());
		assertEquals("Deu erro! Erro ao desativar produto", resposta.getBody());
	}

	@Test
	@DisplayName("ATIVAR PRODUTO")
	void AtivarProdutoOk() {
		ResponseEntity<String> resposta = produtoController.enable(1L);

		assertEquals(HttpStatus.OK, resposta.getStatusCode());
		assertEquals("Produto ativado com sucesso", resposta.getBody());
	}

	@Test
	@DisplayName("ERRO AO ATIVAR PRODUTO")
	void ErroAtivarProduto() {
		when(produtoService.enable(1L)).thenThrow(new RuntimeException("Erro ao ativar produto"));

		ResponseEntity<String> resposta = produtoController.enable(1L);

		assertEquals(HttpStatus.BAD_REQUEST, resposta.getStatusCode());
		assertEquals("Deu erro! Erro ao ativar produto", resposta.getBody());
	}

	// -------------------------------------------------------------------------
	// TESTES ADICIONAIS DE VALIDATIONS:
	@Test
	@DisplayName("Cenário Save do Produto com Nome em Branco")
	void cenarioSaveProdutoExcecaoNomeBlank() {
		Produto produto = new Produto(1L, "", "Descrição válida", 10.0, true);
		assertThrows(Exception.class, () -> {
			produtoController.save(produto);
		});
	}

	@Test
	@DisplayName("Cenário Save do Produto com Preço Negativo")
	void cenarioSaveProdutoExcecaoPrecoNegativo() {
		Produto produto = new Produto(1L, "Produto válido", "Descrição válida", -1.0, true);
		assertThrows(Exception.class, () -> {
			produtoController.save(produto);
		});
	}
}