package com.cptm.ProjetoCPTM.controller;

import java.util.ArrayList;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cptm.ProjetoCPTM.model.AddEstacao;
import com.cptm.ProjetoCPTM.model.Estacao;
import com.cptm.ProjetoCPTM.model.RemoveEstacao;

@RestController
public class RemoveEstacaoController {
	
	@GetMapping("/removeEstacoes")
	public String getRemoveEstacoes() {
		ArrayList<Estacao> estacoes = new ArrayList<>();
	
	    Estacao estacao1 = new Estacao("1", "Estação A", "Local A", "Linha 1", true, null, null, false);
	    Estacao estacao2 = new Estacao("2", "Estação B", "Local B", "Linha 2", true, null, null, false);
	
	    // Adicionando estações à lista
	    AddEstacao.cadastraEstacao(estacao1, estacoes);
	    AddEstacao.cadastraEstacao(estacao2, estacoes);
	
	    // Removendo uma estação utilizando o método da interface RemoveEstacao
	    RemoveEstacao.removeEstacao(estacao1, estacoes);
	    RemoveEstacao.removeEstacao(estacao2, estacoes);
	    
	    return "Sem Estacoes";
	}
}
