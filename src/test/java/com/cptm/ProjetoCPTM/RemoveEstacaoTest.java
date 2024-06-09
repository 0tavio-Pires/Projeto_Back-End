package com.cptm.ProjetoCPTM;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.cptm.ProjetoCPTM.model.Estacao;
import com.cptm.ProjetoCPTM.model.RemoveEstacao;

import java.util.ArrayList;

public class RemoveEstacaoTest {

    private ArrayList<Estacao> estacoes;

    @BeforeEach
    public void setUp() {
        // Inicializa a lista de estações para cada teste
        estacoes = new ArrayList<>();

        // Adiciona algumas estações para teste
        estacoes.add(new Estacao("1", "Estação 1", "Localização 1", "Linha 1",
                true, null, null, false));
        estacoes.add(new Estacao("2", "Estação 2", "Localização 2", "Linha 2",
                true, null, null, false));
        estacoes.add(new Estacao("3", "Estação 3", "Localização 3", "Linha 3",
                true, null, null, false));
    }

    @Test
    public void testRemoveEstacao() {
        // Remove a primeira estação
        Estacao estacaoRemovida = estacoes.get(0);
        RemoveEstacao.removeEstacao(estacaoRemovida, estacoes);

        // Verifica se a estação foi removida corretamente
        assertFalse(estacoes.contains(estacaoRemovida));
        assertEquals(2, estacoes.size());
    }

    @Test
    public void testRemoveEstacaoInexistente() {
        // Cria uma estação que não está na lista
        Estacao estacaoInexistente = new Estacao("4", "Estação 4", "Localização 4", "Linha 4",
                true, null, null, false);

        // Remove a estação inexistente
        RemoveEstacao.removeEstacao(estacaoInexistente, estacoes);

        // Verifica se a lista não foi alterada
        assertEquals(3, estacoes.size());
    }
}
