package com.cptm.ProjetoCPTM;

import org.junit.jupiter.api.Test;

import com.cptm.ProjetoCPTM.model.AddEstacao;
import com.cptm.ProjetoCPTM.model.Estacao;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class AddEstacaoTest {

    @Test
    public void testCadastraEstacao() {
        // Cria uma estação
        Estacao estacao = new Estacao(/* parâmetros da estação */);
        ArrayList<Estacao> estacoes = new ArrayList<>();

        // Chama o método estático
        AddEstacao.cadastraEstacao(estacao, estacoes);

        // Verifica se a estação foi adicionada corretamente
        assertEquals(1, estacoes.size());
        assertTrue(estacoes.contains(estacao));
    }

    @Test
    public void testAdd() {
        // Cria um objeto
        Object item = new Object();
        ArrayList<Object> objetos = new ArrayList<>();

        // Chama o método estático
        AddEstacao.add(item, objetos);

        // Verifica se o objeto foi adicionado corretamente
        assertEquals(1, objetos.size());
        assertTrue(objetos.contains(item));
    }
}
