package com.cptm.ProjetoCPTM.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cptm.ProjetoCPTM.model.AddEstacao;
import com.cptm.ProjetoCPTM.model.AddTrem;
import com.cptm.ProjetoCPTM.model.Estacao;
import com.cptm.ProjetoCPTM.model.Trem;

@RestController
public class AddTremController {
	
	@GetMapping("/addTrem")
	public ArrayList<Trem> getAddEstacoes() {
		
		 // Exemplo de uso das funções da interface AddEstacao
        ArrayList<Estacao> estacoes = new ArrayList<>();
        ArrayList<Trem> trens = new ArrayList<>();

        Estacao estacao1 = new Estacao("1", "Estação A", "Local A", "Linha 1", true, null, null, false);
        Estacao estacao2 = new Estacao("2", "Estação B", "Local B", "Linha 2", true, null, null, false);

        List<Estacao> estacoesTrem1 = new ArrayList<>();
        estacoesTrem1.add(estacao1);
        estacoesTrem1.add(estacao2);

        Trem trem1 = new Trem("101", "Trem Thomas", 500, estacoesTrem1, 8, true, "Modelo A", 0, 0);

        List<Estacao> estacoesTrem2 = new ArrayList<>();
        estacoesTrem2.add(estacao2);

        Trem trem2 = new Trem("102", "Trem Modelo B", 600, estacoesTrem2, 10, true, "Modelo B", 0, 0);

        // Utilizando o método específico de cadastro de estação
        AddEstacao.cadastraEstacao(estacao1, estacoes);
        AddEstacao.cadastraEstacao(estacao2, estacoes);

        // Utilizando o método específico de cadastro de trem
        AddTrem.cadastraTrem(trem1, trens);
        AddTrem.cadastraTrem(trem2, trens);

	    return trens;
	}
}
