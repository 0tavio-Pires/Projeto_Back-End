package com.cptm.ProjetoCPTM;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.cptm.ProjetoCPTM.model.Estacao;

import static org.junit.jupiter.api.Assertions.*;

public class EstacaoTest {

    private Estacao estacao;

    @BeforeEach
    public void setUp() {
        // Inicializa uma estação para cada teste
        estacao = new Estacao("1", "Estação Teste", "Localização Teste", "Linha Teste",
                true, "Livre", "Livre", false);
    }

    @Test
    public void testGetId() {
        assertEquals("1", estacao.getId());
    }

    @Test
    public void testSetId() {
        estacao.setId("2");
        assertEquals("2", estacao.getId());
    }

    @Test
    public void testGetNome() {
        assertEquals("Estação Teste", estacao.getNome());
    }

    @Test
    public void testSetNome() {
        estacao.setNome("Nova Estação");
        assertEquals("Nova Estação", estacao.getNome());
    }

    @Test
    public void testGetLocalizacao() {
        assertEquals("Localização Teste", estacao.getLocalizacao());
    }

    @Test
    public void testSetLocalizacao() {
        estacao.setLocalizacao("Nova Localização");
        assertEquals("Nova Localização", estacao.getLocalizacao());
    }

    @Test
    public void testGetLinha() {
        assertEquals("Linha Teste", estacao.getLinha());
    }

    @Test
    public void testSetLinha() {
        estacao.setLinha("Nova Linha");
        assertEquals("Nova Linha", estacao.getLinha());
    }

    @Test
    public void testIsStatus() {
        assertTrue(estacao.isStatus());
    }

    @Test
    public void testSetStatus() {
        estacao.setStatus(false);
        assertFalse(estacao.isStatus());
    }

    @Test
    public void testIsOcupadoLadoA() {
    	assertEquals("Livre", estacao.getOcupadoLadoA());
    }

    @Test
    public void testIsOcupadoLadoB() {
    	assertEquals("Livre", estacao.getOcupadoLadoB());
    }


    @Test
    public void testIsOcorrencia() {
        assertFalse(estacao.isOcorrencia());
    }

    @Test
    public void testSetOcorrencia() {
        estacao.setOcorrencia(true);
        assertTrue(estacao.isOcorrencia());
    }
}
