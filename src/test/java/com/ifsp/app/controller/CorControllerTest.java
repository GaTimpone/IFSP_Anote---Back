package com.ifsp.app.controller;

import com.ifsp.app.model.enums.Cor;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CorControllerTest {

    private final CorController corController = new CorController();

    @Test
    void deveListarTodasAsCores() {
        List<String> cores = corController.listarCores();

        assertNotNull(cores);
        assertFalse(cores.isEmpty());
        assertEquals(Cor.values().length, cores.size());
        assertTrue(cores.contains(Cor.values()[0].name()));
    }
}
