package com.cptm.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cptm.model.OcupadoLadoB;

@RestController
@RequestMapping("/estacao")
public class EstacaoOcupadoBController implements OcupadoLadoB {

    @GetMapping("/mudaStatusLadoB")
    public int mudaStatusEstacao(@RequestParam boolean situacao) {
        return OcupadoLadoB.mudaStatusEstacao(situacao);
    }

    // Outros métodos e mapeamentos podem ser adicionados aqui
}
