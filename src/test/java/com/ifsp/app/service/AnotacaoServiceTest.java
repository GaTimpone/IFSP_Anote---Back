package com.ifsp.app.service;

import com.ifsp.app.controller.dto.AnotacaoDTO;
import com.ifsp.app.model.Anotacao;
import com.ifsp.app.model.Caderno;
import com.ifsp.app.model.Usuario;
import com.ifsp.app.model.repository.AnotacaoRepository;
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

class AnotacaoServiceTest {

    @Mock
    private AnotacaoRepository anotacaoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private CadernoRepository cadernoRepository;

    @InjectMocks
    private AnotacaoService anotacaoService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findAll_deveRetornarLista() {
        Anotacao a = new Anotacao();
        when(anotacaoRepository.findAll()).thenReturn(List.of(a));

        List<Anotacao> resultado = anotacaoService.findAll();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(anotacaoRepository, times(1)).findAll();
    }

    @Test
    void findById_quandoExistir_deveRetornar() {
        Anotacao a = new Anotacao();
        when(anotacaoRepository.findById(1L)).thenReturn(Optional.of(a));

        Anotacao resultado = anotacaoService.findById(1L);

        assertSame(a, resultado);
        verify(anotacaoRepository).findById(1L);
    }

    @Test
    void findById_quandoNaoExistir_deveLancarNotFound() {
        when(anotacaoRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> anotacaoService.findById(1L));
        assertEquals(org.springframework.http.HttpStatus.NOT_FOUND, ex.getStatusCode());
        assertTrue(ex.getReason().contains("Anotação não encontrada"));
    }

    @Test
    void save_quandoUsuarioENaoExistir_deveLancarNotFound() {
        AnotacaoDTO dto = new AnotacaoDTO();
        dto.setUsuarioId(1L);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> anotacaoService.save(dto));
        assertEquals(org.springframework.http.HttpStatus.NOT_FOUND, ex.getStatusCode());
        assertTrue(ex.getReason().contains("Usuário não encontrado"));

        verify(anotacaoRepository, never()).save(any());
        verify(cadernoRepository, never()).findById(any());
    }

    @Test
    void save_quandoCadernoNaoExistir_deveLancarNotFound() {
        AnotacaoDTO dto = new AnotacaoDTO();
        dto.setUsuarioId(1L);
        dto.setCadernoId(2L);

        Usuario usuario = new Usuario();
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(cadernoRepository.findById(2L)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> anotacaoService.save(dto));
        assertEquals(org.springframework.http.HttpStatus.NOT_FOUND, ex.getStatusCode());
        assertTrue(ex.getReason().contains("Caderno não encontrado"));

        verify(anotacaoRepository, never()).save(any());
    }

    @Test
    void save_quandoSemCaderno_deveSalvarComCadernoNull() {
        AnotacaoDTO dto = new AnotacaoDTO();
        dto.setUsuarioId(1L);
        dto.setTitulo("T");
        dto.setCorpo("B");
        dto.setCadernoId(null);

        Usuario usuario = new Usuario();
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        Anotacao saved = new Anotacao();
        when(anotacaoRepository.save(any(Anotacao.class))).thenReturn(saved);

        Anotacao resultado = anotacaoService.save(dto);

        assertSame(saved, resultado);

        ArgumentCaptor<Anotacao> captor = ArgumentCaptor.forClass(Anotacao.class);
        verify(anotacaoRepository).save(captor.capture());
        Anotacao capt = captor.getValue();
        assertEquals("T", capt.getTitulo());
        assertEquals("B", capt.getCorpo());
        assertSame(usuario, capt.getUsuario());
        assertNull(capt.getCaderno());

        verify(cadernoRepository, never()).findById(any());
    }

    @Test
    void save_quandoComCaderno_deveSalvarComCaderno() {
        AnotacaoDTO dto = new AnotacaoDTO();
        dto.setUsuarioId(1L);
        dto.setCadernoId(2L);
        dto.setTitulo("Titulo");
        dto.setCorpo("Corpo");

        Usuario usuario = new Usuario();
        Caderno caderno = new Caderno();

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(cadernoRepository.findById(2L)).thenReturn(Optional.of(caderno));
        when(anotacaoRepository.save(any(Anotacao.class))).thenAnswer(i -> i.getArgument(0));

        Anotacao resultado = anotacaoService.save(dto);

        assertNotNull(resultado);
        assertEquals("Titulo", resultado.getTitulo());
        assertEquals("Corpo", resultado.getCorpo());
        assertSame(usuario, resultado.getUsuario());
        assertSame(caderno, resultado.getCaderno());

        verify(anotacaoRepository).save(any());
    }

    @Test
    void deleteById_quandoNaoExistir_deveLancarNotFound() {
        when(anotacaoRepository.existsById(1L)).thenReturn(false);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> anotacaoService.deleteById(1L));
        assertEquals(org.springframework.http.HttpStatus.NOT_FOUND, ex.getStatusCode());
        assertTrue(ex.getReason().contains("Anotação não encontrada"));

        verify(anotacaoRepository, never()).deleteById(any());
    }

    @Test
    void deleteById_quandoExistir_deveDeletar() {
        when(anotacaoRepository.existsById(1L)).thenReturn(true);

        anotacaoService.deleteById(1L);

        verify(anotacaoRepository).deleteById(1L);
    }

    @Test
    void update_quandoNaoExistirAnotacao_deveLancarNotFound() {
        AnotacaoDTO dto = new AnotacaoDTO();
        when(anotacaoRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> anotacaoService.update(1L, dto));
        assertEquals(org.springframework.http.HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void update_quandoUsuarioIdFornecidoEMasUsuarioNaoExistir_deveLancarNotFound() {
        Anotacao existing = new Anotacao();
        existing.setTitulo("old");
        existing.setCorpo("old");
        when(anotacaoRepository.findById(1L)).thenReturn(Optional.of(existing));

        AnotacaoDTO dto = new AnotacaoDTO();
        dto.setUsuarioId(5L); // causará a busca no usuarioRepository

        when(usuarioRepository.findById(5L)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> anotacaoService.update(1L, dto));
        assertEquals(org.springframework.http.HttpStatus.NOT_FOUND, ex.getStatusCode());
        assertTrue(ex.getReason().contains("Usuário não encontrado"));
    }

    @Test
    void update_quandoCadernoIdFornecidoEMasCadernoNaoExistir_deveLancarNotFound() {
        Anotacao existing = new Anotacao();
        existing.setTitulo("old");
        when(anotacaoRepository.findById(1L)).thenReturn(Optional.of(existing));

        AnotacaoDTO dto = new AnotacaoDTO();
        dto.setCadernoId(10L);

        when(cadernoRepository.findById(10L)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> anotacaoService.update(1L, dto));
        assertEquals(org.springframework.http.HttpStatus.NOT_FOUND, ex.getStatusCode());
        assertTrue(ex.getReason().contains("Caderno não encontrado"));
    }

    @Test
    void update_quandoAtualizarTituloCorpoECadernoNull_deveSalvarComCadernoNull() {
        Caderno initialCaderno = new Caderno();
        Anotacao existing = new Anotacao();
        existing.setTitulo("velho");
        existing.setCorpo("velho corpo");
        existing.setCaderno(initialCaderno);

        when(anotacaoRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(anotacaoRepository.save(any(Anotacao.class))).thenAnswer(i -> i.getArgument(0));

        AnotacaoDTO dto = new AnotacaoDTO();
        dto.setTitulo("novo");
        dto.setCorpo("novo corpo");
        // dto.cadernoId == null -> service deve setar caderno = null

        Anotacao atualizado = anotacaoService.update(1L, dto);

        assertEquals("novo", atualizado.getTitulo());
        assertEquals("novo corpo", atualizado.getCorpo());
        assertNull(atualizado.getCaderno());
        verify(anotacaoRepository).save(existing);
    }

    @Test
    void update_quandoFornecerCadernoValido_deveAtribuirCaderno() {
        Anotacao existing = new Anotacao();
        existing.setTitulo("t");
        when(anotacaoRepository.findById(1L)).thenReturn(Optional.of(existing));

        Caderno novo = new Caderno();
        when(cadernoRepository.findById(2L)).thenReturn(Optional.of(novo));
        when(anotacaoRepository.save(any(Anotacao.class))).thenAnswer(i -> i.getArgument(0));

        AnotacaoDTO dto = new AnotacaoDTO();
        dto.setCadernoId(2L);

        Anotacao atualizado = anotacaoService.update(1L, dto);

        assertSame(novo, atualizado.getCaderno());
        verify(cadernoRepository).findById(2L);
        verify(anotacaoRepository).save(existing);
    }
}
