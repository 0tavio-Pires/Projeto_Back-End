package com.cptm.model;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class AddEstacaoTest {

    @Test
    public void testCadastraEstacao() {
        List<Estacao> estacoes = new ArrayList<>();
        Estacao lapa = new Estacao("123", "Lapa", "Lapa", "Ruby", true, false, false, false);
        Estacao republica = new Estacao("456", "República", "República", "Azul", true, false, false, false);

        AddEstacao.cadastraEstacao(lapa, (ArrayList<Estacao>) estacoes);
        AddEstacao.cadastraEstacao(republica, (ArrayList<Estacao>) estacoes);

        assertEquals(2, estacoes.size());
        assertEquals(lapa, estacoes.get(0));
        assertEquals(republica, estacoes.get(1));
    }

    @Test
    public void testAdd() {
        List<Object> objects = new ArrayList<>();
        Trem trem = new Trem("654321", "Thomas", 2021, new ArrayList<>(), 20, true, "Rugby");

        AddEstacao.add(trem, (ArrayList<Object>) objects);

        assertEquals(1, objects.size());
        assertEquals(trem, objects.get(0));
    }
}