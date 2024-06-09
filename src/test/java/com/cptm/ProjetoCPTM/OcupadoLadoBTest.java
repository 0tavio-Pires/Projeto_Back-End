package com.cptm.ProjetoCPTM;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.cptm.ProjetoCPTM.model.OcupadoLadoB;

public class OcupadoLadoBTest {

    @Test
    public void testOcupadoLadoBOcupado() {
        String status = OcupadoLadoB.ocupadoLadoB(true);
        assertEquals("Ocupado", status);
    }

    @Test
    public void testOcupadoLadoBLivre() {
        String status = OcupadoLadoB.ocupadoLadoB(false);
        assertEquals("Livre", status);
    }
}
