package com.cptm.controller;

import com.cptm.model.Trem;
import com.cptm.model.Estacao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/trem")
public class TremController {

    private List<Trem> trens = new ArrayList<>();

    @GetMapping
    public List<Trem> getAllTrens() {
        return trens;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Trem> getTremById(@PathVariable String id) {
        for (Trem trem : trens) {
            if (trem.getId().equals(id)) {
                return ResponseEntity.ok(trem);
            }
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public Trem createTrem(@RequestBody Trem trem) {
        trens.add(trem);
        return trem;
    }

}