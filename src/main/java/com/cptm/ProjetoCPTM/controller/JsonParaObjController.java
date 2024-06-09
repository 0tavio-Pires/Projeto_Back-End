package com.cptm.ProjetoCPTM.controller;

import com.cptm.ProjetoCPTM.model.JsonParaObj;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/json")
public class JsonParaObjController {

    @GetMapping("/ler/{arquivo}")
    public String lerJsonParaObjeto(@PathVariable String arquivo) {
        String filePath = "caminho/para/seu/arquivo/" + arquivo; // Substitua pelo caminho correto do seu arquivo

        // Lê o JSON e converte para objeto
        Object obj = JsonParaObj.lerJsonParaObjeto(filePath);

        if (obj != null) {
            String json = obj.toString(); // Converte o objeto de volta para JSON (apenas para demonstração)
            System.out.println(json); // Imprime o JSON no console
            return json;
        } else {
            return "Falha ao ler o arquivo JSON: " + arquivo;
        }
    }
}