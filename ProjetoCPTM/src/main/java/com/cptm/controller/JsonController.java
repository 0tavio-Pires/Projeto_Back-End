package com.cptm.controller;

import com.cptm.model.TransformaEmJson;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@Import(TransformaEmJson.class)
public class JsonController {

    private final Object thomas;

    public JsonController(Object thomas) {
        this.thomas = thomas;
    }

    @GetMapping("/thomas")
    public void transformarEExibirDadosEmJson() {
        TransformaEmJson.transformarEExibirDadosEmJson(thomas);
    }
}
