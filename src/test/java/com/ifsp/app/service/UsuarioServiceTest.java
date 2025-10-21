package com.ifsp.app.service;

import com.ifsp.app.controller.dto.UsuarioDTO;
import com.ifsp.app.model.Usuario;
import com.ifsp.app.model.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioService usuarioService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void deveRetornarUsuarioQuandoLoginValido() {
        // ARRANGE
        Usuario mockUser = new Usuario();
        mockUser.setEmail("teste@ifsp.com");
        mockUser.setSenha("12345");

        when(usuarioRepository.findByEmail("teste@ifsp.com"))
                .thenReturn(Optional.of(mockUser));

        // ACT
        Usuario resultado = usuarioService.login("teste@ifsp.com", "12345");

        // ASSERT
        assertNotNull(resultado);
        assertEquals("teste@ifsp.com", resultado.getEmail());
    }

    @Test
    void deveLancarErroQuandoEmailNaoExiste() {
        // ARRANGE
        when(usuarioRepository.findByEmail(anyString()))
                .thenReturn(Optional.empty());

        // ACT + ASSERT
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> {
            usuarioService.login("naoexiste@ifsp.com", "12345");
        });

        assertEquals("Email ou senha inválidos", ex.getReason());
        assertEquals(401, ex.getStatusCode().value());
    }

    @Test
    void deveLancarErroQuandoSenhaIncorreta() {
        // ARRANGE
        Usuario mockUser = new Usuario();
        mockUser.setEmail("teste@ifsp.com");
        mockUser.setSenha("correta");

        when(usuarioRepository.findByEmail("teste@ifsp.com"))
                .thenReturn(Optional.of(mockUser));

        // ACT + ASSERT
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> {
            usuarioService.login("teste@ifsp.com", "errada");
        });

        assertEquals("Email ou senha inválidos", ex.getReason());
        assertEquals(401, ex.getStatusCode().value());
    }
    
    @Test
    void deveCadastrarUsuarioComSucesso() {
        // ARRANGE
        UsuarioDTO dto = new UsuarioDTO();
        dto.setNome("Gabriel");
        dto.setEmail("novo@ifsp.com");
        dto.setSenha("123");

        Usuario mockUsuario = new Usuario();
        mockUsuario.setNome(dto.getNome());
        mockUsuario.setEmail(dto.getEmail());
        mockUsuario.setSenha(dto.getSenha());

        when(usuarioRepository.save(any(Usuario.class))).thenReturn(mockUsuario);

        // ACT
        Usuario resultado = usuarioService.save(dto);

        // ASSERT
        assertNotNull(resultado);
        assertEquals("Gabriel", resultado.getNome());
        assertEquals("novo@ifsp.com", resultado.getEmail());
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    void deveLancarErroAoCadastrarEmailDuplicado() {
        // ARRANGE
        UsuarioDTO dto = new UsuarioDTO();
        dto.setNome("Gabriel");
        dto.setEmail("teste@ifsp.com");
        dto.setSenha("123");

        when(usuarioRepository.save(any(Usuario.class)))
                .thenThrow(new org.springframework.dao.DataIntegrityViolationException("Email duplicado"));

        // ACT + ASSERT
        Exception ex = assertThrows(Exception.class, () -> {
            usuarioService.save(dto);
        });

        assertTrue(ex.getMessage().contains("Email"));
    }

}
