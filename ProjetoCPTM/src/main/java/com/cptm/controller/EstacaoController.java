package com.cptm.controller;

import com.cptm.model.Estacao;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/estacao")
public class EstacaoController {

    private List<Estacao> estacoes = new ArrayList<>();

    @GetMapping
    public List<Estacao> getAllEstacoes() {
        return estacoes;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Estacao> getEstacaoById(@PathVariable String id) {
        for (Estacao estacao : estacoes) {
            if (estacao.getId().equals(id)) {
                return ResponseEntity.ok(estacao);
            }
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public Estacao createEstacao(@RequestBody Estacao estacao) {
        estacoes.add(estacao);
        return estacao;
    }

    @PutMapping("/{id}")
    public ResponseEntity<Estacao> updateEstacao(@PathVariable String id, @RequestBody Estacao estacaoDetails) {
        for (Estacao estacao : estacoes) {
            if (estacao.getId().equals(id)) {
                estacao.setNome(estacaoDetails.getNome());
                estacao.setLocalizacao(estacaoDetails.getLocalizacao());
                estacao.setLinha(estacaoDetails.getLinha());
                estacao.setStatus(estacaoDetails.isStatus());
                estacao.setOcupadoLadoA(estacaoDetails.isOcupadoLadoA());
                estacao.setOcupadoLadoB(estacaoDetails.isOcupadoLadoB());
                estacao.setOcorrencia(estacaoDetails.isOcorrencia());
                return ResponseEntity.ok(estacao);
            }
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEstacao(@PathVariable String id) {
        for (Estacao estacao : estacoes) {
            if (estacao.getId().equals(id)) {
                estacoes.remove(estacao);
                return ResponseEntity.noContent().build();
            }
        }
        return ResponseEntity.notFound().build();
    }
}