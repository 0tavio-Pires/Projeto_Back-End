package com.cptm.ProjetoCPTM;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.cptm.ProjetoCPTM.model.Estacao;
import com.cptm.ProjetoCPTM.model.Linha;
import com.cptm.ProjetoCPTM.model.Trem;

public class LinhaTest {

    private Linha linha;
    private ArrayList<Estacao> estacoes;
    private ArrayList<Trem> trens;

    @BeforeEach
    public void setUp() {
        // Inicializa uma lista de estações e trens
        estacoes = new ArrayList<>();
        trens = new ArrayList<>();

        // Cria uma linha para cada teste
        linha = new Linha("1", "Linha Teste", estacoes, trens);
    }

    @Test
    public void testGetId() {
        assertEquals("1", linha.getId());
    }

    @Test
    public void testSetId() {
        linha.setId("2");
        assertEquals("2", linha.getId());
    }

    @Test
    public void testGetNome() {
        assertEquals("Linha Teste", linha.getNome());
    }

    @Test
    public void testSetNome() {
        linha.setNome("Nova Linha");
        assertEquals("Nova Linha", linha.getNome());
    }

    @Test
    public void testGetEstacoes() {
        assertEquals(estacoes, linha.getEstacoes());
    }

    @Test
    public void testSetEstacoes() {
        ArrayList<Estacao> novasEstacoes = new ArrayList<>();
        linha.setEstacoes(novasEstacoes);
        assertEquals(novasEstacoes, linha.getEstacoes());
    }

    @Test
    public void testGetTrens() {
        assertEquals(trens, linha.getTrens());
    }

    @Test
    public void testSetTrens() {
        ArrayList<Trem> novosTrens = new ArrayList<>();
        linha.setTrens(novosTrens);
        assertEquals(novosTrens, linha.getTrens());
    }
}
