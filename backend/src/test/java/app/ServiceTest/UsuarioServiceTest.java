package app.ServiceTest;

import app.auth.Usuario;
import app.Repository.UsuarioRepository;
import app.Service.UsuarioService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;


@SpringBootTest
public class UsuarioServiceTest {

    @Autowired
    UsuarioService usuarioService;

    @MockBean
    UsuarioRepository usuarioRepository;


    @BeforeEach
    void setUp() {
        Usuario usuario = new Usuario(1, "Marcela Garcia", "Marci", "Senha123", Usuario.Role.FUNCIONARIO, true, null);

        Mockito.when(usuarioRepository.save(Mockito.any())).thenReturn(usuario);

        Mockito.when(usuarioRepository.findByLogin("Marci")).thenReturn(Optional.of(usuario));

        Mockito.when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        Mockito.doNothing().when(usuarioRepository).deleteById(1L);

        Mockito.when(usuarioRepository.findById(2L)).thenReturn(Optional.of(usuario));


    }


    @Test
    @DisplayName("Teste Unitario - Salvar usuário com sucesso")
    void testSaveSuccess() {
        Usuario usuario = new Usuario(1, "Marcela Garcia", "Marci", "Senha123", Usuario.Role.FUNCIONARIO, true, null);
        String message = usuarioService.save(usuario);
        Assertions.assertEquals("Usuário salvo com sucesso", message);
    }

    @Test
    @DisplayName("Teste Unitario - Salvar Usuario com login existente")
    void testSaveLoginExistente() {
        Usuario usuario = new Usuario(2, "Marcela Garcia", "Marci", "Senha123", Usuario.Role.FUNCIONARIO, true, null);

        Exception e = Assertions.assertThrows(Exception.class, () -> {
            usuarioService.save(usuario);
        });

        Assertions.assertEquals("Login ou Senha já está em uso", e.getMessage());

    }

    @Test
    @DisplayName("Teste Unitario - Conferindo usuario com login existente")
    void testConferirUser() {

        Usuario usuario = new Usuario(2, "Marcela Garcia", "Marci", "Senha123", Usuario.Role.FUNCIONARIO, true, null);
        boolean exists = usuarioService.conferirUser(usuario);

        Assertions.assertTrue(exists);

    }

    @Test
    @DisplayName("Teste Uniatrio - Conferindo usuario inesistente ")
    void testConferirUserInexistente() {

        Usuario usuario = new Usuario(1, "Emilly Souza", "Emis", "Hope", Usuario.Role.GESTOR, true);
        Assertions.assertFalse(usuarioService.conferirUser(usuario));
    }


    @Test
    @DisplayName("Teste Unitario - FindByID ")
    void testById() {
        Usuario usuario = usuarioService.findById(1);

        Assertions.assertEquals("Marcela Garcia", usuario.getNome());

    }

    @Test
    @DisplayName("Teste Unitario - Excluir Usuario ")
    void testExcluirUsser() {
        String message = usuarioService.delete(1l);

        Assertions.assertEquals("Usuario deletado com sucesso", message);
    }

//    @Test
//    @DisplayName("Teste Unitario - Usuario Desativado")
//    void testDesativarUsser() {
//
//        Usuario usuario = new Usuario(2, "Marcela Garcia", "Marci", "Senha123", Usuario.Role.FUNCIONARIO, true, null);
//        Mockito.when(usuarioRepository.findById(2l)).thenReturn(Optional.of(usuario));
//
//        Usuario usuarioInativo = new Usuario(2, "Marcela Garcia", "Marci", "Senha123", Usuario.Role.FUNCIONARIO, false, null);
//        Mockito.when(usuarioRepository.save(Mockito.any())).thenReturn(usuarioInativo);
//
//        String message = usuarioService.disable(2l);
//        Assertions.assertEquals("Usuário desativado com sucesso!",message);
//
//    }

    @Test
    @DisplayName("Teste Unitario - Usuario Ativado")
    void testAtivarUsser (){

        Usuario usuario = new Usuario(2, "Marcela Garcia", "Marci", "Senha123", Usuario.Role.FUNCIONARIO, true, null);
        Mockito.when(usuarioRepository.findById(2l)).thenReturn(Optional.of(usuario));

        Usuario usuarioInativo = new Usuario(2, "Marcela Garcia", "Marci", "Senha123", Usuario.Role.FUNCIONARIO, false, null);
        Mockito.when(usuarioRepository.save(Mockito.any())).thenReturn(usuarioInativo);

        String message = usuarioService.enable(2l);
        Assertions.assertEquals("Usuário ativado com sucesso!",message);

    }

}











