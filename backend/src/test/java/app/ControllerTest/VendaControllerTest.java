package app.ControllerTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import app.Controller.VendaController;
import app.Entity.Produto;
import app.Entity.ProdutoVenda;
import app.auth.Usuario;
import app.auth.Usuario.Role;
import app.Entity.Venda;
import app.Repository.VendaRepository;
import app.Service.ProdutoService;
import app.Service.UsuarioService;
import jakarta.validation.ConstraintViolationException;

@SpringBootTest
public class VendaControllerTest {

	@Autowired
	VendaController vendaController;

	@MockBean
	VendaRepository vendaRepository;

	@MockBean
	ProdutoService produtoService;

	@MockBean
	UsuarioService usuarioService;

	@BeforeEach
	void setUp() {

		LocalDateTime startDate = LocalDateTime.of(2024, 9, 1, 00, 00);
		LocalDateTime endDate = LocalDateTime.of(2024, 9, 30, 23, 59);

		Usuario usuario = new Usuario(1, "Marcela Garcia", "Marci", "Senha123", Role.FUNCIONARIO, true, null);

		Produto produtoBanana = new Produto(1, "Banana", "Penca de Banana", true, 6, null);

		ProdutoVenda produtoVendaBanana = new ProdutoVenda(1, 1, null, produtoBanana);

		List<ProdutoVenda> list = new ArrayList<>();

		list.add(produtoVendaBanana);

		Venda venda = new Venda(1, 6, LocalDateTime.of(2024, 9, 4, 14, 56), 0, /*193735171L,*/ "Pix", usuario, list);

		Produto produtoTomate = new Produto(2, "Tomate", "1 tomate", true, 2, null);

		ProdutoVenda produtoVendaTomate = new ProdutoVenda(2, 5, null, produtoTomate);

		List<ProdutoVenda> list2 = new ArrayList<>();

		list2.add(produtoVendaBanana);
		list2.add(produtoVendaTomate);

		Venda venda2 = new Venda(1, 16, LocalDateTime.of(2024, 9, 16, 14, 56), 0, /*876576535L,*/ "Pix", usuario, list2);

		List<Venda> vendas = new ArrayList<>();

		vendas.add(venda);
		vendas.add(venda2);

		Mockito.when(vendaRepository.save(Mockito.any())).thenReturn(venda);

		Mockito.when(vendaRepository.findById(1L)).thenReturn(Optional.of(venda));

		Mockito.when(vendaRepository.findById(0L)).thenReturn(Optional.ofNullable(null));

		Mockito.when(vendaRepository.findAll()).thenReturn(vendas);

		Mockito.when(vendaRepository.findByDataBetween(any(), any())).thenReturn(vendas);
		
		Mockito.when(vendaRepository.findByUsuarioId(1L)).thenReturn(vendas);
		
		Mockito.when(vendaRepository.findByUsuarioId(2L)).thenReturn(null);
		
		Mockito.when(usuarioService.findById(1)).thenReturn(usuario);

		Mockito.when(usuarioService.findById(0)).thenReturn(null);

		Mockito.when(produtoService.findById(1)).thenReturn(produtoBanana);

		doNothing().when(vendaRepository).deleteById(anyLong());

		doThrow(new RuntimeException("Venda não encontrada")).when(vendaRepository).deleteById(0L);

	}

//	@Test
//	@DisplayName("Integração - Cenário Save da Venda")
//	void cenarioSaveVenda() {
//
//		Usuario usuario = new Usuario(1, "Marcela Garcia", "Marci", "Senha123", Role.FUNCIONARIO, true, null);
//
//		Produto produtoBanana = new Produto(1, "Banana", "Penca de Banana", true, 6, null);
//
//		ProdutoVenda produtoVendaBanana = new ProdutoVenda(1, 1, null, produtoBanana);
//
//		List<ProdutoVenda> list = new ArrayList<>();
//
//		list.add(produtoVendaBanana);
//
//		Venda venda = new Venda(1, 0, null, 0, "Pix", usuario, list);
//
//		ResponseEntity<String> retorno = vendaController.save(venda);
//
//		assertEquals("Venda salva com sucesso", retorno.getBody());
//		assertEquals(HttpStatus.OK, retorno.getStatusCode());
//	}
//	
	@Test
	@DisplayName("Integração - Cenário Save da Venda Forma de Pagamento em Branco")
	void cenarioSaveVendaExcecaoPagamentoBlank() {
		
		Usuario usuario = new Usuario(1, "Marcela Garcia", "Marci", "Senha123", Role.FUNCIONARIO, true, null);
		
		Produto produtoBanana = new Produto(1, "Banana", "Penca de Banana", true, 6, null);
		
		ProdutoVenda produtoVendaBanana = new ProdutoVenda(1, 1, null, produtoBanana);
		
		List<ProdutoVenda> list = new ArrayList<>();
		
		list.add(produtoVendaBanana);
		
		Venda venda = new Venda(1, 0, null, 0, "", usuario, list);
		
		
		assertThrows(ConstraintViolationException.class, ()->{
			vendaController.save(venda);
		});
	}
	
	@Test
	@DisplayName("Integração - Cenário Save da Venda User Null")
	void cenarioSaveVendaExcecaoUserNull() {
		
		Usuario usuario = null;
		
		Produto produtoBanana = new Produto(1, "Banana", "Penca de Banana", true, 6, null);
		
		ProdutoVenda produtoVendaBanana = new ProdutoVenda(1, 1, null, produtoBanana);
		
		List<ProdutoVenda> list = new ArrayList<>();
		
		list.add(produtoVendaBanana);
		
		Venda venda = new Venda(1, 0, null, 0, "Pix", usuario, list);
		
		
		assertThrows(ConstraintViolationException.class, ()->{
			vendaController.save(venda);
		});
	}
	
	@Test
	@DisplayName("Integração - Cenário Save da Venda Lista de Produtos Vazia")
	void cenarioSaveVendaExcecaoListEmpty() {
		
		Usuario usuario = new Usuario(1, "Marcela Garcia", "Marci", "Senha123", Role.FUNCIONARIO, true, null);
		
		List<ProdutoVenda> list = new ArrayList<>();
		
		Venda venda = new Venda(1, 0, null, 0, "Pix", usuario, list);
		
		
		assertThrows(ConstraintViolationException.class, ()->{
			vendaController.save(venda);
		});
	}
	
	@Test
	@DisplayName("Integração - Cenário Save da Venda Forma de Pagamento Nula")
	void cenarioSaveVendaExcecaoPagamentoNull() {
		
		Usuario usuario = new Usuario(1, "Marcela Garcia", "Marci", "Senha123", Role.FUNCIONARIO, true, null);
		
		Produto produtoBanana = new Produto(1, "Banana", "Penca de Banana", true, 6, null);
		
		ProdutoVenda produtoVendaBanana = new ProdutoVenda(1, 1, null, produtoBanana);
		
		List<ProdutoVenda> list = new ArrayList<>();
		
		list.add(produtoVendaBanana);
		
		Venda venda = new Venda(1, 0, null, 0, null, usuario, list);
		
		
		assertThrows(ConstraintViolationException.class, ()->{
			vendaController.save(venda);
		});
	}

//	@Test
//	@DisplayName("Integração - Cenário Save da Venda com Bad Request")
//	void cenarioSaveVendaBadRequest() {
//
//		Usuario usuario = new Usuario(0, "Marcela Garcia", "Marci", "Senha123", Role.FUNCIONARIO, true, null);
//
//		Produto produtoBanana = new Produto(1, "Banana", "Penca de Banana", true, 6, null);
//
//		ProdutoVenda produtoVendaBanana = new ProdutoVenda(1, 1, null, produtoBanana);
//
//		List<ProdutoVenda> list = new ArrayList<>();
//
//		list.add(produtoVendaBanana);
//
//		Venda venda = new Venda(1, 0, null, 0,/*0,*/ "Pix", usuario, list);
//
//		ResponseEntity<String> retorno = vendaController.save(venda);
//
//		assertEquals("Deu Erro! Usuario não encontrado", retorno.getBody());
//		assertEquals(HttpStatus.BAD_REQUEST, retorno.getStatusCode());
//	}

	@Test
	@DisplayName("Integração - Cenário Update da Venda")
	void cenarioUpdateVenda() {

		Usuario usuario = new Usuario(1, "Marcela Garcia", "Marci", "Senha123", Role.FUNCIONARIO, true, null);

		Produto produtoBanana = new Produto(1, "Banana", "Penca de Banana", true, 6, null);

		ProdutoVenda produtoVendaBanana = new ProdutoVenda(1, 2, null, produtoBanana);

		List<ProdutoVenda> list = new ArrayList<>();

		list.add(produtoVendaBanana);

		Venda venda = new Venda(1, 0, null, 0, /*0,*/ "Pix", usuario, list);

		ResponseEntity<String> retorno = vendaController.update(venda, 1L);

		assertEquals("Atualizada com sucesso", retorno.getBody());
		assertEquals(HttpStatus.OK, retorno.getStatusCode());
	}

	@Test
	@DisplayName("Integração - Cenário Update da Venda com Bad Request")
	void cenarioUpdateVendaBadRequest() {

		Venda venda = null;

		ResponseEntity<String> retorno = vendaController.update(venda, 1L);

		assertEquals(HttpStatus.BAD_REQUEST, retorno.getStatusCode());
	}

	@Test
	@DisplayName("Integração - find venda by id")
	void cenarioFindByIdVenda() {
		ResponseEntity<Venda> retorno = this.vendaController.findById(1);

		Venda venda = retorno.getBody();

		assertEquals(HttpStatus.OK, retorno.getStatusCode());
		assertEquals(6, venda.getTotal());
	}

	@Test
	@DisplayName("Integração - find venda by id Bad Request")
	void cenarioFindByIdVendaBadRequest() {
		ResponseEntity<Venda> retorno = this.vendaController.findById(0);

		assertEquals(HttpStatus.BAD_REQUEST, retorno.getStatusCode());
	}

	@Test
	@DisplayName("Integração - find venda by id")
	void cenarioFindAllVenda() {
		ResponseEntity<List<Venda>> retorno = this.vendaController.findAll();

		List<Venda> venda = retorno.getBody();

		assertEquals(2, venda.size());
		assertEquals(HttpStatus.OK, retorno.getStatusCode());
	}

	@Test
	@DisplayName("Integração - Delete da Venda")
	void cenarioDeleteVenda() {
		ResponseEntity<String> retorno = this.vendaController.delete(1L);

		String msg = retorno.getBody();

		assertEquals(HttpStatus.OK, retorno.getStatusCode());
		assertEquals("Venda deletada com sucesso", msg);
	}

	@Test
	@DisplayName("Integração - Delete venda não encontrada")
	void cenarioDeleteVendaNaoEncontrada() {
		ResponseEntity<String> retorno = this.vendaController.delete(0L);

		assertEquals(HttpStatus.BAD_REQUEST, retorno.getStatusCode());
	}

	@Test
	@DisplayName("Integração - findByData da venda")
	void cenarioFindVendaByData() {

		LocalDateTime startDate = LocalDateTime.of(2024, 9, 1, 00, 00);
		LocalDateTime endDate = LocalDateTime.of(2024, 9, 30, 23, 59);

		ResponseEntity<List<Venda>> retorno = this.vendaController.findByDataBetween(startDate, endDate);

		List<Venda> venda = retorno.getBody();

		assertEquals(2, venda.size());
		assertEquals(HttpStatus.OK, retorno.getStatusCode());

	}

	@Test
	@DisplayName("Integração - findByData da venda")
	void cenarioFindVendaByDataError() {
		
		LocalDateTime startDate = LocalDateTime.of(2024, 9, 30, 00, 00);
		LocalDateTime endDate = LocalDateTime.of(2024, 9, 1, 23, 59);
		
		ResponseEntity<List<Venda>> retorno = this.vendaController.findByDataBetween(startDate, endDate);
		
		assertEquals(HttpStatus.BAD_REQUEST, retorno.getStatusCode());
		
	}
	
	@Test
	@DisplayName("Integração - find da venda por mes e ano")
	void cenarioFindVendaByMesAndAno() {
		
		ResponseEntity<List<Venda>> retorno = this.vendaController.findByMonthAndYear(2024, 9);
		
		List<Venda> venda = retorno.getBody();

		assertEquals(2, venda.size());
		assertEquals(HttpStatus.OK, retorno.getStatusCode());
	}
	
//	@Test
//	@DisplayName("Integração - find da venda por mes e ano - mes posterior ao atual")
//	void cenarioVendaByMonthAndYearMesPosterior() {
//		
//		ResponseEntity<List<Venda>> retorno = this.vendaController.findByMonthAndYear(2024, 10);
//		
//		assertEquals(HttpStatus.BAD_REQUEST, retorno.getStatusCode());
//		
//	}
	
	
	@Test
	@DisplayName("Integração - find da venda por mes e ano - mes invalido")
	void cenarioVendaByMonthAndYearMesInvalido() {
		
		ResponseEntity<List<Venda>> retorno = this.vendaController.findByMonthAndYear(2024, 13);
		
		assertEquals(HttpStatus.BAD_REQUEST, retorno.getStatusCode());
		
	}
	@Test
	@DisplayName("Integração - find da venda por mes e ano - mes negativo")
	void cenarioVendaByMonthAndYearMesNegativo() {
		
		ResponseEntity<List<Venda>> retorno = this.vendaController.findByMonthAndYear(2024, -2);
		
		assertEquals(HttpStatus.BAD_REQUEST, retorno.getStatusCode());
		
	}
	
	@Test
	@DisplayName("Integração - find da venda por mes e ano - ano posterior ao atual")
	void cenarioVendaByMonthAndYearAnoPosterior() {
		
		ResponseEntity<List<Venda>> retorno = this.vendaController.findByMonthAndYear(9, 2025);
		
		assertEquals(HttpStatus.BAD_REQUEST, retorno.getStatusCode());
		
	}
	
	@Test
	@DisplayName("Integração - find da venda pelo id do usuario")
	void cenarioFindVendaByUserId() {
		
		ResponseEntity<List<Venda>> retorno = this.vendaController.findByUsuarioId(1L);
		
		assertEquals(HttpStatus.OK, retorno.getStatusCode());
		assertEquals(2, retorno.getBody().size());
	}
	@Test
	@DisplayName("Integração - find da venda pelo id do usuario")
	void cenarioFindyUserIdVendaNull() {
		
		ResponseEntity<List<Venda>> retorno = this.vendaController.findByUsuarioId(0L);
		
		assertEquals(HttpStatus.NO_CONTENT, retorno.getStatusCode());
	}
	
}
