package com.cptm.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cptm.model.OcupadoLadoA;

@RestController
@RequestMapping("/estacao")
public class EstacaoOcupadoController implements OcupadoLadoA {

    @GetMapping("/mudaStatusLadoA")
    public int mudaStatusEstacao(@RequestParam boolean situacao) {
        return OcupadoLadoA.mudaStatusEstacao(situacao);
    }

    // Outros métodos e mapeamentos podem ser adicionados aqui
}
