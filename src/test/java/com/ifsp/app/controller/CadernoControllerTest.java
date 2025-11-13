package com.ifsp.app.controller;

import com.ifsp.app.controller.dto.CadernoDTO;
import com.ifsp.app.model.Caderno;
import com.ifsp.app.service.CadernoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CadernoControllerTest {

    @Mock
    private CadernoService cadernoService;

    @InjectMocks
    private CadernoController cadernoController;

    private Caderno caderno;
    private CadernoDTO cadernoDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        caderno = new Caderno();
        cadernoDTO = new CadernoDTO();
    }

    @Test
    void deveListarTodosCadernos() {
        when(cadernoService.findAll()).thenReturn(List.of(caderno));

        List<Caderno> resultado = cadernoController.findAll();

        assertEquals(1, resultado.size());
        verify(cadernoService, times(1)).findAll();
    }

    @Test
    void deveBuscarCadernoPorId() {
        when(cadernoService.findById(1L)).thenReturn(caderno);

        Caderno resultado = cadernoController.findById(1L);

        assertEquals(caderno, resultado);
        verify(cadernoService, times(1)).findById(1L);
    }

    @Test
    void deveCriarNovoCaderno() {
        when(cadernoService.save(cadernoDTO)).thenReturn(caderno);

        Caderno resultado = cadernoController.create(cadernoDTO);

        assertEquals(caderno, resultado);
        verify(cadernoService, times(1)).save(cadernoDTO);
    }

    @Test
    void deveAtualizarCaderno() {
        when(cadernoService.update(1L, cadernoDTO)).thenReturn(caderno);

        Caderno resultado = cadernoController.update(1L, cadernoDTO);

        assertEquals(caderno, resultado);
        verify(cadernoService, times(1)).update(1L, cadernoDTO);
    }

    @Test
    void deveDeletarCaderno() {
        cadernoController.deleteById(1L);
        verify(cadernoService, times(1)).deleteById(1L);
    }
}
