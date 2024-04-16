package cptm;

import cptm.Estacao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Principal {

	public static void main(String[] args) throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		Estacao lapa = new Estacao("123", "lapa", "lapa", "ruby");
		Estacao republica = new Estacao("456", "republica", "republica", "azul");
		
		ArrayList<Estacao> estacoes = new ArrayList<>();
		
		Estacao.cadastraEstacao(lapa, estacoes);
		Estacao.cadastraEstacao(republica, estacoes);
		
		Trem thomas = new Trem("654321", "Thomas", 2021, estacoes, 20, true, "Rugby");
		
		String jsonData = mapper.writeValueAsString(thomas);
		System.out.println(jsonData);
		
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

            System.out.println("ID: " + estacaoId);
            System.out.println("Nome: " + estacaoNome);
            System.out.println("Localização: " + estacaoLocalizacao);
            System.out.println("Linha: " + estacaoLinha);
        }
	}
}