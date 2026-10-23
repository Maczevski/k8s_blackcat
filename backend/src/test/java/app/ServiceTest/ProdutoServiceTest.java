package app.ServiceTest;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import app.Entity.Produto;
import app.Repository.ProdutoRepository;
import app.Service.ProdutoService;

@SpringBootTest
public class ProdutoServiceTest {

	@Autowired
	public ProdutoService produtoService;

	@MockBean
	ProdutoRepository produtoRepository;

	@BeforeEach
	void setup() {
		Produto produto = new Produto();
		produto.setId(1L);
		produto.setNome("Batata crua");
		produto.setDescricao("Batata crua com casca colhida na esquina da macumba");
		produto.setPreco(99.99);
		produto.setAtivo(true);

		Mockito.when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));
		Mockito.when(produtoRepository.findByNomeAndDescricao("Batata crua", "Batata ungida pelo padre Marcelo Rossi"))
				.thenReturn(Optional.empty());

		doNothing().when(produtoRepository).delete(Mockito.any());
	}

	@Test
	@DisplayName("SALVO COM SUCESSO")
	void ProdutoSalvoOk() {

		Produto produto = new Produto();
		produto.setId(1L);
		produto.setNome("Batata de macumba descascada");
		produto.setDescricao("Batata descascada colhida na esquina da macumba");
		produto.setPreco(10);
		produto.setAtivo(true);

		Mockito.when(produtoRepository.save(produto)).thenReturn(produto);

		String result = produtoService.save(produto);

		Assertions.assertEquals("Produto salvo com sucesso", result);
	}

	@Test
	@DisplayName("ATUALIZADO COM SUCESSO")
	void ProdutoAlteradoOk() {

		Produto produtoExistente = new Produto();
		produtoExistente.setId(1L);
		produtoExistente.setNome("Batata de macumba descascada");
		produtoExistente.setDescricao("Batata descascada colhida na esquina da macumba");
		produtoExistente.setPreco(10);
		produtoExistente.setAtivo(true);

		Produto produtoAlterado = new Produto();
		produtoAlterado.setId(1L);
		produtoAlterado.setNome("Batata Santa");
		produtoAlterado.setDescricao("Batata ungida pelo padre Marcelo Rossi");
		produtoAlterado.setPreco(200);
		produtoAlterado.setAtivo(true);

		Mockito.when(
				produtoRepository.findByNomeAndDescricao(produtoAlterado.getNome(), produtoAlterado.getDescricao()))
				.thenReturn(Optional.empty());

		Mockito.when(produtoRepository.save(produtoAlterado)).thenReturn(produtoAlterado);

		String resultado = produtoService.update(produtoAlterado, 1L);

		Assertions.assertEquals("Produto atualizado com sucesso", resultado);

		Mockito.verify(produtoRepository, times(1)).save(produtoAlterado);
	}

	@Test
	@DisplayName("NÃO ATUALIZADO POIS JÁ EXISTE")
	void ProdutoJaExisteNoUpdate() {

		Produto produtoExistente = new Produto();
		produtoExistente.setId(2L);
		produtoExistente.setNome("Batata de macumba descascada");
		produtoExistente.setDescricao("Batata crua com casca colhida na esquina da macumba");
		produtoExistente.setPreco(100);
		produtoExistente.setAtivo(true);

		Produto produtoAlterado = new Produto();
		produtoAlterado.setId(1L);
		produtoAlterado.setNome("Batata Santa");
		produtoAlterado.setDescricao("Batata ungida pelo padre Marcelo Rossi");
		produtoAlterado.setPreco(200);
		produtoAlterado.setAtivo(true);

		// simula que ja existe outro produto com mesmo nome e descricao mas id
		// diferente
		when(produtoRepository.findByNomeAndDescricao(produtoAlterado.getNome(), produtoAlterado.getDescricao()))
				.thenReturn(Optional.of(produtoExistente));

		// verifica se a excecao de duplicidade é lançada
		RuntimeException exception = assertThrows(RuntimeException.class, () -> {
			produtoService.update(produtoAlterado, 1L);
		});

		Assertions.assertEquals("Produto com o mesmo nome e descrição já existe", exception.getMessage());

		verify(produtoRepository, times(0)).save(produtoAlterado);
	}

	@Test
	@DisplayName("DESATIVADO COM SUCESSO")
	void ProdutoDesativadoOk() {

		Produto produtoExistente = new Produto();
		produtoExistente.setId(1L);
		produtoExistente.setNome("Batata de macumba descascada");
		produtoExistente.setPreco(10);
		produtoExistente.setAtivo(true);

		// mockando o comportamento no repository
		Mockito.when(produtoRepository.findById(1L)).thenReturn(Optional.of(produtoExistente));

		// executa o metodo desativado
		String resultado = produtoService.disable(1L);

		// verfica se os metodos corretos foram usandos
		Mockito.verify(produtoRepository).findById(1L);
		Mockito.verify(produtoRepository).save(produtoExistente);

		// assertivas
		Assertions.assertEquals("Produto desativado com sucesso!", resultado);
		Assertions.assertFalse((produtoExistente.isAtivo()), "O produto deveria estar desativado.");
	}
	
	@Test
	@DisplayName("ATIVADO COM SUCESSO")
	void ProdutoAtivadoOk (){
		Produto produtoExistente = new Produto();
		produtoExistente.setId(1L);
		produtoExistente.setNome("Batata de macumba descascada");
		produtoExistente.setPreco(10);
		produtoExistente.setAtivo(true);
		
		//simula busca no banco de dados
		Mockito.when(produtoRepository.findById(1L)).thenReturn(Optional.of(produtoExistente));
		//simula salvae o produto
		Mockito.when(produtoRepository.save(produtoExistente)).thenReturn(produtoExistente);
		//usa metodo enable para ativar o produto
		String resultado = produtoService.enable(1L);
		
		//verifica se foi ativado
		Assertions.assertTrue(produtoExistente.isAtivo());
		//verifica se o retorno é a string esperada
		Assertions.assertEquals("Produto ativado com sucesso!", resultado);
	}
	
	@Test
	@DisplayName("NÃO DESATIVA SE PRODUTO NÃO EXISTE")
	void ProdutoNaoDesativadoQuandoNaoExistente() {
	    Long produtoId = 1L;

	    // Simula que o produto não existe no banco
	    Mockito.when(produtoRepository.findById(produtoId)).thenReturn(Optional.empty());

	    Assertions.assertThrows(RuntimeException.class, () -> {
	        produtoService.disable(produtoId);
	    });
	}

	@Test
	@DisplayName("LISTA PRODUTOS ATIVOS")
	void ListaProdutosAtivos() {
	    // Cria uma lista de produtos simulados
	    Produto produto1 = new Produto();
	    produto1.setId(1L);
	    produto1.setNome("Batata de macumba descascada");
	    produto1.setDescricao("Batata descascada colhida na esquina da macumba");
	    produto1.setPreco(10);
	    produto1.setAtivo(true);
	    
	    Produto produto2 = new Produto();
	    produto2.setId(2L);
	    produto2.setNome("Batata Santa");
	    produto2.setDescricao("Batata ungida pelo padre Marcelo Rossi");
	    produto2.setPreco(200);
	    produto2.setAtivo(true);

	    List<Produto> produtosAtivos = Arrays.asList(produto1, produto2);

	    // Simula o retorno do repositório
	    Mockito.when(produtoRepository.findByAtivo(true)).thenReturn(produtosAtivos);

	    // Executa o método findAll
	    List<Produto> resultado = produtoService.findAll(true);

	    // Verifica se o resultado contém os produtos esperados
	    Assertions.assertEquals(2, resultado.size());
	    Assertions.assertTrue(resultado.contains(produto1));
	    Assertions.assertTrue(resultado.contains(produto2));
	}

	@Test
	@DisplayName("LISTA PRODUTOS INATIVOS")
	void ListaProdutosInativos() {
	    // Cria uma lista de produtos simulados
	    Produto produto1 = new Produto();
	    produto1.setId(3L);
	    produto1.setNome("Batata de macumba inativa");
	    produto1.setDescricao("Batata inativa colhida na esquina da macumba");
	    produto1.setPreco(5);
	    produto1.setAtivo(false);
	    
	    List<Produto> produtosInativos = Arrays.asList(produto1);

	    // Simula o retorno do repositório
	    Mockito.when(produtoRepository.findByAtivo(false)).thenReturn(produtosInativos);

	    // Executa o método findAll
	    List<Produto> resultado = produtoService.findAll(false);

	    // Verifica se o resultado contém os produtos esperados
	    Assertions.assertEquals(1, resultado.size());
	    Assertions.assertTrue(resultado.contains(produto1));
	}
	
	@Test
	@DisplayName("DELETADO COM SUCESSO")
	void ProdutoDeletadoOk() {
		Long produtoId = 1L;

		Mockito.doNothing().when(produtoRepository).deleteById(produtoId);

		String resultado = produtoService.delete(produtoId);

		Assertions.assertEquals("Produto deletado com sucesso!", resultado);
	}
	
	

}