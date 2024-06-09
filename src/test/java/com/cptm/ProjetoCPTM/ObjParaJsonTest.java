package com.cptm.ProjetoCPTM;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.cptm.ProjetoCPTM.model.Estacao;
import com.cptm.ProjetoCPTM.model.ObjParaJson;

public class ObjParaJsonTest {

    @Test
    public void testTransformarParaJson() {
        Estacao estacao = new Estacao("1", "Estação Teste1", "Localização Teste", "Linha Teste",
                true, null, null, false);

        String json = ObjParaJson.transformarParaJson(estacao);

        assertNotNull(json);

        // Verifica se o JSON contém os campos esperados
        assertTrue(json.contains("\"id\":\"1\""));
        assertTrue(json.contains("\"nome\":\"Estação Teste1\""));
        assertTrue(json.contains("\"localizacao\":\"Localização Teste\""));
        assertTrue(json.contains("\"linha\":\"Linha Teste\""));
        assertTrue(json.contains("\"status\":true"));
        assertTrue(json.contains("\"ocupadoLadoA\":false"));
        assertTrue(json.contains("\"ocupadoLadoB\":false"));
        assertTrue(json.contains("\"ocorrencia\":false"));
    }
    
    @Test
    public void testTransformarParaJson2() {
        Estacao estacao2 = new Estacao("2", "Estação Teste2", "Localização Teste2", "Linha Teste2",
                false, null, null, true);

        String json2 = ObjParaJson.transformarParaJson(estacao2);

        assertNotNull(json2);

        // Verifica se o JSON contém os campos esperados
        assertTrue(json2.contains("\"id\":\"2\""));
        assertTrue(json2.contains("\"nome\":\"Estação Teste2\""));
        assertTrue(json2.contains("\"localizacao\":\"Localização Teste2\""));
        assertTrue(json2.contains("\"linha\":\"Linha Teste2\""));
        assertTrue(json2.contains("\"status\":false"));
        assertTrue(json2.contains("\"ocupadoLadoA\":true"));
        assertTrue(json2.contains("\"ocupadoLadoB\":true"));
        assertTrue(json2.contains("\"ocorrencia\":true"));
    }
}
