package com.cptm.ProjetoCPTM.controller;

import java.util.ArrayList;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cptm.ProjetoCPTM.model.AddEstacao;
import com.cptm.ProjetoCPTM.model.Estacao;
import com.cptm.ProjetoCPTM.model.RemoveTrem;
import com.cptm.ProjetoCPTM.model.Trem;

@RestController
public class RemoveTremController {
	
	@GetMapping("/removeTrem")
	public ArrayList<Trem> getRemoveTrem() {
	    // Exemplo de uso da função da interface RemoveTrem
	    ArrayList<Estacao> estacoes = new ArrayList<>();
	    ArrayList<Trem> trens = new ArrayList<>();
	
	    Estacao estacao1 = new Estacao("1", "Estação A", "Local A", "Linha 1", true, null, null, false);
	    Estacao estacao2 = new Estacao("2", "Estação B", "Local B", "Linha 2", true, null, null, false);
	
	    ArrayList<Estacao> estacoesTrem1 = new ArrayList<>();
	    AddEstacao.cadastraEstacao(estacao1, estacoes);
	    AddEstacao.cadastraEstacao(estacao2, estacoes);
	    
	    Trem trem1 = new Trem("101", "Trem Modelo A", 500, estacoesTrem1, 8, true, "Modelo A", 0, 0);
	
	    ArrayList<Estacao> estacoesTrem2 = new ArrayList<>();
	    estacoesTrem2.add(estacao2);
	
	    Trem trem2 = new Trem("102", "Trem Modelo B", 600, estacoesTrem2, 10, true, "Modelo B", 0, 0);
	
	    // Adicionando trens à lista
	    trens.add(trem1);
	    trens.add(trem2);
	
	    // Removendo um trem utilizando o método da interface RemoveTrem
	    RemoveTrem.removeTrem(trem1, trens);
	    
	    return trens;
	}
}
