package com.cptm.ProjetoCPTM;

import org.junit.jupiter.api.Test;

import com.cptm.ProjetoCPTM.model.AddTrem;
import com.cptm.ProjetoCPTM.model.Trem;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AddTremTest {

    @Test
    public void testCadastraTrem() {
        // Cria um trem
        Trem trem = new Trem(null, null, 0, null, 0, false, null, 0, 0);
        List<Trem> trens = new ArrayList<>();

        // Chama o método estático
        AddTrem.cadastraTrem(trem, trens);

        // Verifica se o trem foi adicionado corretamente
        assertEquals(1, trens.size());
        assertTrue(trens.contains(trem));
    }

    @Test
    public void testAdd() {
        // Cria um objeto
        Object item = new Object();
        ArrayList<Object> lista = new ArrayList<>();

        // Chama o método estático
        AddTrem.add(item, lista);

        // Verifica se o objeto foi adicionado corretamente
        assertEquals(1, lista.size());
        assertTrue(lista.contains(item));
    }
}
