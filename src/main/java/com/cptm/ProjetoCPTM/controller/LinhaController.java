package com.cptm.ProjetoCPTM.controller;

import com.cptm.ProjetoCPTM.model.ObjParaJson;
import com.cptm.ProjetoCPTM.model.Linha;
import com.cptm.ProjetoCPTM.service.TremService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/linha")
public class LinhaController {

    @Autowired
    private TremService tremService;

    @GetMapping("/detalhes")
    public String obterDetalhesLinha() {
        Linha linha = tremService.getLinha(); // Obter a linha do serviço
        String json = ObjParaJson.transformarParaJson(linha);
        System.out.println(json); // Imprime o JSON no console
        return json;
    }
}