package com.cptm.ProjetoCPTM.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ObjParaJson {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static String transformarParaJson(Object objeto) {
        try {
            return mapper.writeValueAsString(objeto);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }
}