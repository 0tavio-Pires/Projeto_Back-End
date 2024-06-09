package com.cptm.ProjetoCPTM.controller;

import java.util.ArrayList;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cptm.ProjetoCPTM.model.AddEstacao;
import com.cptm.ProjetoCPTM.model.Estacao;

@RestController
public class AddEstacaoController {
	
	@GetMapping("/addEstacao")
	public ArrayList<Estacao> getAddEstacoes() {
		
	    ArrayList<Estacao> estacoes = new ArrayList<>();

	    Estacao estacao1 = new Estacao("1", "Estação A", "Local A", "Linha 1", true, null, null, false);
	    Estacao estacao2 = new Estacao("2", "Estação B", "Local B", "Linha 2", true, null, null, false);

	    AddEstacao.cadastraEstacao(estacao1, estacoes);
	    AddEstacao.cadastraEstacao(estacao2, estacoes);
	    
	    return estacoes;
	}
}
