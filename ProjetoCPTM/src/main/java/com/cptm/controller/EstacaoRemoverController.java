package com.cptm.controller;

import java.util.ArrayList;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cptm.model.Estacao;
import com.cptm.model.RemoveEstacao;

@RestController
@RequestMapping("/estacoes")
public class EstacaoRemoverController implements RemoveEstacao {
    
    private ArrayList<Estacao> listaEstacoes = new ArrayList<>();

    @DeleteMapping("/{id}")
    public String removeEstacao(@PathVariable String id) {
        Estacao estacaoRemover = null;
        for (Estacao estacao : listaEstacoes) {
            if (estacao.getId().equals(id)) {
                estacaoRemover = estacao;
                break;
            }
        }
        
        if (estacaoRemover != null) {
            listaEstacoes.remove(estacaoRemover);
            return "Estação removida com sucesso!";
        } else {
            return "Estação não encontrada!";
        }
    }

    // Outros métodos e mapeamentos podem ser adicionados aqui
}
