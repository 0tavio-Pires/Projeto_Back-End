package com.cptm.ProjetoCPTM.controller;

import com.cptm.ProjetoCPTM.model.Estacao;
import com.cptm.ProjetoCPTM.service.TremService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/estacao")
public class EstacaoController {

    @Autowired
    private TremService tremService;

    @PostMapping("/ocorrencia")
    public void ocorrencia(@RequestBody OcorrenciaRequest request) {
        tremService.ocorrencia(request.getId(), request.getAtraso());
    }

    public static class OcorrenciaRequest {
        private int id;
        private int atraso;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getAtraso() {
            return atraso;
        }

        public void setAtraso(int atraso) {
            this.atraso = atraso;
        }
    }

    @GetMapping("/detalhes")
    public List<Estacao> obterDetalhesEstacao() {
        return tremService.getEstacoes();
    }
}