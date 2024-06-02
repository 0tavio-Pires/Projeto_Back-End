package com.cptm.controller;

import java.util.ArrayList;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cptm.model.Trem;
import com.cptm.model.RemoveTrem;

@RestController
@RequestMapping("/trens")
public class TremRemoverController implements RemoveTrem {
    
    private ArrayList<Trem> listaTrens = new ArrayList<>();

    @DeleteMapping("/{id}")
    public String removeTrem(@PathVariable String id) {
        Trem tremRemover = null;
        for (Trem trem : listaTrens) {
            if (trem.getId().equals(id)) {
                tremRemover = trem;
                break;
            }
        }
        
        if (tremRemover != null) {
            listaTrens.remove(tremRemover);
            return "Trem removido com sucesso!";
        } else {
            return "Trem não encontrado!";
        }
    }
}