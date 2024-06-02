package com.cptm.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cptm.model.AtivaOcorrencia;

@RestController
@RequestMapping("/ocorrencia")
public class OcorrenciaController implements AtivaOcorrencia {

    @GetMapping("/ativa")
    public String ativaOcorrencia(@RequestParam boolean ocorrencias) {
        return AtivaOcorrencia.ativaOcorrencia(ocorrencias);
    }
}
