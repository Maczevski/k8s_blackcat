package app.ControllerTest;

import app.Controller.UsuarioController;
import app.auth.Usuario;
import app.reponse.UserUpdateResponse;
import app.Repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class UsuarioControllerTest {

	@Autowired
	UsuarioController usuarioController;

	@MockBean
	UsuarioRepository usuarioRepository;

	@BeforeEach
	void setUp() {
		Usuario usuario = new Usuario(1, "Marcela Garcia", "Marci", "Senha123", Usuario.Role.FUNCIONARIO, true, null);

		Optional<Usuario> usuarioOptional = Optional.of(usuario);

		Mockito.when(usuarioRepository.save(Mockito.any())).thenReturn(usuario);

		Mockito.when(usuarioRepository.findByLogin("Marci")).thenReturn(usuarioOptional);
		Mockito.when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

	}

	@Test
	@DisplayName("Salvar Usuario")
	void cenarioSave() {
		Usuario usuario = new Usuario(1, "Marcela Garcia", "Marci", "Senha123", Usuario.Role.FUNCIONARIO, true, null);
		ResponseEntity<String> response = this.usuarioController.save(usuario);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals("Usuário salvo com sucesso", response.getBody());
	}

	@Test
	@DisplayName("Bad Request Usuario")
	void cenarioBadResquest() {
		Usuario usuario = new Usuario(2, "Marcela Garcia", "Marci", "Senha123", Usuario.Role.FUNCIONARIO, true, null);
		ResponseEntity<String> response = this.usuarioController.save(usuario);

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		assertEquals("Erro: Login ou Senha já está em uso", response.getBody());
	}

//	@Test
//	@DisplayName("Cenário Update da Usuario")
//	void cenarioUpdateUsuario() {
//
//		Usuario usuario = new Usuario(1, "Marcela Garcia", "Marci", "Senha123", Usuario.Role.FUNCIONARIO, true, null);
//		ResponseEntity<UserUpdateResponse> retorno = usuarioController.update(usuario, 1L);
//
//		assertEquals("Atualizado com sucesso", retorno.getBody().getMessage());
//		assertEquals(HttpStatus.OK, retorno.getStatusCode());
//	}

	@Test
	@DisplayName("Cenário Update da Usuario com Bad Request")
	void cenarioUpdateUsuarioBadRequest() {

		Usuario usuario = null;

		ResponseEntity<UserUpdateResponse> retorno = usuarioController.update(usuario, 1L);
		assertEquals(HttpStatus.BAD_REQUEST, retorno.getStatusCode());
	}

	@Test
	@DisplayName("Find Usuario By ID")
	void cenarioFindByIdUsuario() {

		ResponseEntity<Usuario> retorno = this.usuarioController.findById(1);

		Usuario usuario = retorno.getBody();

		assertEquals(HttpStatus.OK, retorno.getStatusCode());
	}

	@Test

	@DisplayName("Find Usuario By ID Bad Request")
	void cenarioFindByIdUsuarioBadRequest() {

		ResponseEntity<Usuario> retorno = this.usuarioController.findById(0);

		assertEquals(HttpStatus.BAD_REQUEST, retorno.getStatusCode());
	}

	@Test
	@DisplayName("Find All Usuario")
	void cenarioFindAllUsuario() {
		ResponseEntity<List<Usuario>> retorno = this.usuarioController.findAll(true);

		List<Usuario> usuarios = retorno.getBody();

		assertEquals(0, usuarios.size());
		assertEquals(HttpStatus.OK, retorno.getStatusCode());
	}

	@Test
	@DisplayName("Delete Usuario")
	void cenarioDeleteUsuario() {

		ResponseEntity<String> retorno = this.usuarioController.delete(1L);

		String msg = retorno.getBody();

		assertEquals(HttpStatus.OK, retorno.getStatusCode());
		assertEquals("Usuario deletado com sucesso", msg);
	}

	@Test
	@DisplayName("Enable Usuario")
	void cenarioEnableUsuario() {

		Usuario usuario = new Usuario(1, "Marcela Garcia", "Marci", "Senha123", Usuario.Role.FUNCIONARIO, true, null);
		ResponseEntity<String> retorno = usuarioController.enable(1L);

		assertEquals("Usuário ativado com sucesso!", retorno.getBody());
		assertEquals(HttpStatus.OK, retorno.getStatusCode());
	}

//	@Test
//	@DisplayName("Disable Usuario")
//	void cenarioDisableUsuario() {
//
//		Usuario usuario = new Usuario(1, "Marcela Garcia", "Marci", "Senha123", Usuario.Role.FUNCIONARIO, true, null);
//		usuarioRepository.save(usuario);
//
//		ResponseEntity<String> retorno = usuarioController.disable(1L);
//
//		assertEquals("Usuário desativado com sucesso!", retorno.getBody());
//		assertEquals(HttpStatus.OK, retorno.getStatusCode());
//	}

	@Test
	@DisplayName("Enable Usuario bad request")
	void cenarioEnableUsuarioBadRequest() {

		Usuario usuario = new Usuario(1, "Marcela Garcia", "Marci", "Senha123", Usuario.Role.FUNCIONARIO, true, null);
		ResponseEntity<String> retorno = usuarioController.enable(0L);

		assertEquals(HttpStatus.BAD_REQUEST, retorno.getStatusCode());
	}

	@Test
	@DisplayName("Disable Usuario Bad Request")
	void cenarioDisableUsuarioBadRequest() {

		Usuario usuario = new Usuario(1, "Marcela Garcia", "Marci", "Senha123", Usuario.Role.FUNCIONARIO, true, null);
		usuarioRepository.save(usuario);

		ResponseEntity<String> retorno = usuarioController.disable(0L);

		assertEquals(HttpStatus.BAD_REQUEST, retorno.getStatusCode());
	}

	@Test
	@DisplayName("Cenário Save do Usuario com Nome em Branco")
	void cenarioSaveUsuarioExcecaoNomeBlank() {
		Usuario usuario = new Usuario(1, "", "Marci", "Senha123", Usuario.Role.FUNCIONARIO, true, null);

		assertThrows(Exception.class, () -> {
			usuarioController.save(usuario);
		});

	}

	@Test
	@DisplayName("Cenário Save do Usuario com Login Muito Curto")
	void cenarioSaveUsuarioExcecaoLoginCurto() {
		Usuario usuario = new Usuario(1, "", "Ma", "Senha123", Usuario.Role.FUNCIONARIO, true, null);

		assertThrows(Exception.class, () -> {
			usuarioController.save(usuario);
		});
	}

}
