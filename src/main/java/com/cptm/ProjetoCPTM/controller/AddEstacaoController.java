package com.cptm.ProjetoCPTM.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.cptm.ProjetoCPTM.model.Estacao;
import com.cptm.ProjetoCPTM.service.TremService;

import java.util.List;

@RestController
@RequestMapping("/estacao")
public class AddEstacaoController {

    @Autowired
    private TremService tremService;

    @PostMapping("/adicionar")
    public void adicionarEstacao(@RequestBody Estacao estacao) {
        tremService.adicionarEstacao(estacao);
    }

    @PostMapping("/alterarStatus")
    public void alterarStatusTrem(@RequestBody StatusRequest request) {
        tremService.alterarStatusTrem(request.getId(), request.isStatus());
    }

    @GetMapping("/listar")
    public List<Estacao> listarEstacoes() {
        return tremService.getLinha().getEstacoes();
    }

    public static class StatusRequest {
        private String id;
        private boolean status;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public boolean isStatus() {
            return status;
        }

        public void setStatus(boolean status) {
            this.status = status;
        }
    }
}
