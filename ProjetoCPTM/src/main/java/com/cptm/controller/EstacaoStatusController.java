package com.cptm.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cptm.model.MudaStatusEstacao;

@RestController
@RequestMapping("/estacao")
public class EstacaoStatusController implements MudaStatusEstacao {

    @GetMapping("/mudaStatus")
    public String mudaStatusEstacao(@RequestParam boolean situacao) {
        return MudaStatusEstacao.mudaStatusEstacao(situacao);
    }
}
