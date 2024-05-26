package com.cptm.controller;

import com.cptm.model.Linha;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/linhas")
public class LinhaController {

    private List<Linha> linhas = new ArrayList<>();

    @PostMapping
    public Linha createLinha(@RequestBody Linha linha) {
        linhas.add(linha);
        return linha;
    }

    @GetMapping("/{id}")
    public Linha getLinhaById(@PathVariable String id) {
        return linhas.stream()
                .filter(l -> l.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    @GetMapping
    public List<Linha> getAllLinhas() {
        return linhas;
    }
}