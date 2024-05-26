package com.cptm.controller;

import com.example.jsonmicroservice.model.Person;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class JsonController {

    @Autowired
    private ObjectMapper objectMapper;

    @PostMapping("/object-to-json")
    public String objectToJson(@RequestBody Person person) {
        try {
            return objectMapper.writeValueAsString(person);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    @PostMapping("/json-to-object")
    public Person jsonToObject(@RequestBody String json) {
        try {
            return objectMapper.readValue(json, Person.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }
}