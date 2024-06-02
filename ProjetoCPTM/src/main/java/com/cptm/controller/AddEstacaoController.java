package com.cptm.controller;

import java.util.ArrayList;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cptm.model.Estacao;
import com.cptm.model.AddEstacao;

@RestController
@RequestMapping("/addEstacao")
public class AddEstacaoController implements AddEstacao {

    private ArrayList<Estacao> listaEstacoes = new ArrayList<>();

    @PostMapping
    public void cadastraEstacao(@RequestBody Estacao estacao) {
        AddEstacao.cadastraEstacao(estacao, listaEstacoes);
    }
}
