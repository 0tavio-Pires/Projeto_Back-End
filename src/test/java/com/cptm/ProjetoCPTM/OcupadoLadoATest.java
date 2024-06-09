package com.cptm.ProjetoCPTM;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.cptm.ProjetoCPTM.model.OcupadoLadoA;

public class OcupadoLadoATest {

    @Test
    public void testOcupadoLadoAOcupado() {
        String status = OcupadoLadoA.ocupadoLadoA(true);
        assertEquals("Ocupado", status);
    }

    @Test
    public void testOcupadoLadoALivre() {
        String status = OcupadoLadoA.ocupadoLadoA(false);
        assertEquals("Livre", status);
    }
}
