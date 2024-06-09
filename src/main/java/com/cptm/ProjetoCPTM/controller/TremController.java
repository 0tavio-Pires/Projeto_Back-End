package com.cptm.ProjetoCPTM.controller;

import com.cptm.ProjetoCPTM.model.Estacao;
import com.cptm.ProjetoCPTM.model.ObjParaJson;
import com.cptm.ProjetoCPTM.model.Trem;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/trem")
public class TremController {

    @GetMapping("/detalhes")
    public String obterDetalhesTrem() {
        Trem trem = criarTremExemplo(); // Criação de um trem exemplo
        String json = ObjParaJson.transformarParaJson(trem);
        System.out.println(json); // Imprime o JSON no console
        return json;
    }

    // Método auxiliar para criar um trem exemplo (substitua com dados reais se necessário)
    private Trem criarTremExemplo() {
        // Criação de estações exemplo
        Estacao estacao1 = new Estacao(
                "2F8A7",
                "Japao Liberdade",
                "Rua Praça da Liberdade, 133",
                "Linha 1 - Azul",
                true,
                null,
                null,
                false
        );
        Estacao estacao2 = new Estacao(
                "9BCD3",
                "Vila Mariana",
                "Rua Caieras, 456",
                "Linha 1 - Azul",
                true,
                null,
                null,
                true
        );
        List<Estacao> estacoes = new ArrayList<>();
        estacoes.add(estacao1);
        estacoes.add(estacao2);

        // Criação do trem exemplo
        Trem trem = new Trem(
                "A23F9",
                "Thomas",
                500,
                (ArrayList<Estacao>) estacoes,
                10,
                true,
                "Modelo 9967", 0, 0
        );
        return trem;
    }
}
