package com.cptm.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cptm.model.AddTrem;
import com.cptm.model.Trem;

@RestController
@RequestMapping("/addTrem")
public class AddTremController implements AddTrem {

    private List<Trem> listaTrens = new ArrayList<>();

    @PostMapping
    public void cadastraTrem(@RequestBody Trem trem) {
        AddTrem.cadastraTrem(trem, listaTrens);
    }

    @GetMapping
    public List<Trem> getTrem() {
        return listaTrens;
    }
}
