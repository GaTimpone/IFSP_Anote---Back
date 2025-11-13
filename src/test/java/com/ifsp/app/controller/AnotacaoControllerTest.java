package com.ifsp.app.controller;

import com.ifsp.app.controller.dto.AnotacaoDTO;
import com.ifsp.app.model.Anotacao;
import com.ifsp.app.service.AnotacaoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AnotacaoControllerTest {

    @Mock
    private AnotacaoService anotacaoService;

    @InjectMocks
    private AnotacaoController anotacaoController;

    private Anotacao anotacao;
    private AnotacaoDTO anotacaoDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        anotacao = new Anotacao();
        anotacaoDTO = new AnotacaoDTO();
    }

    @Test
    void deveListarTodasAnotacoes() {
        when(anotacaoService.findAll()).thenReturn(List.of(anotacao));

        List<Anotacao> resultado = anotacaoController.findAll();

        assertEquals(1, resultado.size());
        verify(anotacaoService, times(1)).findAll();
    }

    @Test
    void deveBuscarAnotacaoPorId() {
        when(anotacaoService.findById(1L)).thenReturn(anotacao);

        Anotacao resultado = anotacaoController.findById(1L);

        assertEquals(anotacao, resultado);
        verify(anotacaoService, times(1)).findById(1L);
    }

    @Test
    void deveCriarNovaAnotacao() {
        when(anotacaoService.save(anotacaoDTO)).thenReturn(anotacao);

        Anotacao resultado = anotacaoController.create(anotacaoDTO);

        assertEquals(anotacao, resultado);
        verify(anotacaoService, times(1)).save(anotacaoDTO);
    }

    @Test
    void deveAtualizarAnotacao() {
        when(anotacaoService.update(1L, anotacaoDTO)).thenReturn(anotacao);

        Anotacao resultado = anotacaoController.update(1L, anotacaoDTO);

        assertEquals(anotacao, resultado);
        verify(anotacaoService, times(1)).update(1L, anotacaoDTO);
    }

    @Test
    void deveDeletarAnotacao() {
        anotacaoController.deleteById(1L);
        verify(anotacaoService, times(1)).deleteById(1L);
    }
}
