package com.ifsp.app.service;

import com.ifsp.app.controller.dto.CadernoDTO;
import com.ifsp.app.model.Caderno;
import com.ifsp.app.model.Usuario;
import com.ifsp.app.model.repository.CadernoRepository;
import com.ifsp.app.model.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CadernoServiceTest {

    @Mock
    private CadernoRepository cadernoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private CadernoService cadernoService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findAll_deveRetornarLista() {
        Caderno c = new Caderno();
        when(cadernoRepository.findAll()).thenReturn(List.of(c));

        List<Caderno> resultado = cadernoService.findAll();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(cadernoRepository).findAll();
    }

    @Test
    void findById_quandoExistir_deveRetornar() {
        Caderno c = new Caderno();
        when(cadernoRepository.findById(1L)).thenReturn(Optional.of(c));

        Caderno res = cadernoService.findById(1L);

        assertSame(c, res);
    }

    @Test
    void findById_quandoNaoExistir_deveLancarNotFound() {
        when(cadernoRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> cadernoService.findById(1L));
        assertEquals(org.springframework.http.HttpStatus.NOT_FOUND, ex.getStatusCode());
        assertTrue(ex.getReason().contains("Caderno não encontrado"));
    }

    @Test
    void save_quandoUsuarioNaoExistir_deveLancarNotFound() {
        CadernoDTO dto = new CadernoDTO();
        dto.setUsuarioId(2L);

        when(usuarioRepository.findById(2L)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> cadernoService.save(dto));
        assertEquals(org.springframework.http.HttpStatus.NOT_FOUND, ex.getStatusCode());
        assertTrue(ex.getReason().contains("Usuário não encontrado"));

        verify(cadernoRepository, never()).save(any());
    }

    @Test
    void save_quandoValido_deveSalvar() {
        CadernoDTO dto = new CadernoDTO();
        dto.setUsuarioId(1L);
        dto.setTitulo("Meu Caderno");

        Usuario usuario = new Usuario();
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(cadernoRepository.save(any(Caderno.class))).thenAnswer(i -> i.getArgument(0));

        Caderno salvo = cadernoService.save(dto);

        assertEquals("Meu Caderno", salvo.getTitulo());
        assertSame(usuario, salvo.getUsuario());
        verify(cadernoRepository).save(any(Caderno.class));
    }

    @Test
    void deleteById_quandoNaoExistir_deveLancarNotFound() {
        when(cadernoRepository.existsById(1L)).thenReturn(false);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> cadernoService.deleteById(1L));
        assertEquals(org.springframework.http.HttpStatus.NOT_FOUND, ex.getStatusCode());
        assertTrue(ex.getReason().contains("Caderno não encontrado"));

        verify(cadernoRepository, never()).deleteById(any());
    }

    @Test
    void deleteById_quandoExistir_deveDeletar() {
        when(cadernoRepository.existsById(1L)).thenReturn(true);

        cadernoService.deleteById(1L);

        verify(cadernoRepository).deleteById(1L);
    }

    @Test
    void update_quandoNaoExistirCaderno_deveLancarNotFound() {
        CadernoDTO dto = new CadernoDTO();
        when(cadernoRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> cadernoService.update(1L, dto));
        assertEquals(org.springframework.http.HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void update_quandoUsuarioIdFornecidoEMasUsuarioNaoExistir_deveLancarNotFound() {
        Caderno existing = new Caderno();
        when(cadernoRepository.findById(1L)).thenReturn(Optional.of(existing));

        CadernoDTO dto = new CadernoDTO();
        dto.setUsuarioId(10L);

        when(usuarioRepository.findById(10L)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> cadernoService.update(1L, dto));
        assertEquals(org.springframework.http.HttpStatus.NOT_FOUND, ex.getStatusCode());
        assertTrue(ex.getReason().contains("Usuário não encontrado"));
    }

    @Test
    void update_quandoAlterarTitulo_deveSalvarComTituloAtualizado() {
        Caderno existing = new Caderno();
        existing.setTitulo("antigo");
        when(cadernoRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(cadernoRepository.save(any(Caderno.class))).thenAnswer(i -> i.getArgument(0));

        CadernoDTO dto = new CadernoDTO();
        dto.setTitulo("novo");

        Caderno updated = cadernoService.update(1L, dto);

        assertEquals("novo", updated.getTitulo());
        verify(cadernoRepository).save(existing);
    }
}
