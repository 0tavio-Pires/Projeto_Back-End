package com.cptm.ProjetoCPTM.model;

import java.io.File;
import java.io.IOException;

import org.springframework.boot.json.JsonParseException;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonParaObj {

    private static final ObjectMapper mapper = new ObjectMapper();

    @SuppressWarnings("unchecked")
	public static <T> T lerJsonParaObjeto(String arquivo) {
        File file = new File(arquivo);
        if (!file.exists()) {
            System.err.println("Arquivo não encontrado: " + arquivo);
            return null;
        }

        try {
            return mapper.readValue(file, (Class<T>) Object.class);
        } catch (JsonParseException | JsonMappingException e) {
            ((Throwable) e).printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}