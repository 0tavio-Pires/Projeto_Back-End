package com.cptm.model;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class TransformaEmJson {
    @Bean
	public static void transformarEExibirDadosEmJson(Object thomas) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            
            String jsonData = mapper.writeValueAsString(thomas);
            
            Map<String, Object> jsonMap = mapper.readValue(jsonData, Map.class);
            
            // Processar os dados
            String id = (String) jsonMap.get("id");
            String nome = (String) jsonMap.get("nome");
            int capacidade = (int) jsonMap.get("capacidade");
            List<Map<String, Object>> estacoes1 = (List<Map<String, Object>>) jsonMap.get("estacoes");
            int vagoes = (int) jsonMap.get("vagoes");
            boolean status = (boolean) jsonMap.get("status");
            String modelo = (String) jsonMap.get("modelo");
            
            // Exibir as informações
            System.out.println("ID: " + id);
            System.out.println("Nome: " + nome);
            System.out.println("Capacidade: " + capacidade);
            System.out.println("Vagões: " + vagoes);
            System.out.println("Status: " + status);
            System.out.println("Modelo: " + modelo);
            
            System.out.println("Estações:");
            for (Map<String, Object> estacao : estacoes1) {
                String estacaoId = (String) estacao.get("id");
                String estacaoNome = (String) estacao.get("nome");
                String estacaoLocalizacao = (String) estacao.get("localizacao");
                String estacaoLinha = (String) estacao.get("linha");
                boolean estacaoStatus = (boolean) estacao.get("status");
                boolean estacaoOcupadoLadoA = (boolean) estacao.get("ocupadoLadoA");
                boolean estacaoOcupadoLadoB = (boolean) estacao.get("ocupadoLadoB");
                boolean estacaoOcorrencia = (boolean) estacao.get("ocorrencia");
                
                System.out.println("ID: " + estacaoId);
                System.out.println("Nome: " + estacaoNome);
                System.out.println("Localização: " + estacaoLocalizacao);
                System.out.println("Linha: " + estacaoLinha);
                System.out.println("Status: " + estacaoStatus);
                System.out.println("Ocupado Lado A: " + estacaoOcupadoLadoA);
                System.out.println("Ocupado Lado B: " + estacaoOcupadoLadoB);
                System.out.println("Ocorrência: " + estacaoOcorrencia);
            }
        } catch (IOException e) {
            System.out.println(e);
        }
    }
}



