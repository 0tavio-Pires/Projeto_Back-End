package com.cptm.ProjetoCPTM;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.cptm.ProjetoCPTM.model.MudaStatusEstacao;

public class MudaStatusEstacaoTest {

    @Test
    public void testMudaStatusEstacaoOperando() {
        String status = MudaStatusEstacao.mudaStatusEstacao(true);
        assertEquals("Operando", status);
    }

    @Test
    public void testMudaStatusEstacaoDesativado() {
        String status = MudaStatusEstacao.mudaStatusEstacao(false);
        assertEquals("Desativado", status);
    }
}
