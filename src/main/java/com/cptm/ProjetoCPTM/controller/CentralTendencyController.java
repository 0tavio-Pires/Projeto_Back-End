package com.cptm.ProjetoCPTM.controller;

import com.cptm.ProjetoCPTM.model.CentralTendencyCalculator;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CentralTendencyController {

    @GetMapping("/central-tendency")
    public String calculateCentralTendency() {
        // Dados de exemplo
        double[] data = {1, 2, 3, 4, 5, 5, 5, 6, 7, 8, 9};

        // Calculando as medidas de centralidade
        double mean = CentralTendencyCalculator.calculateMean(data);
        double median = CentralTendencyCalculator.calculateMedian(data);
        double mode = CentralTendencyCalculator.calculateMode(data);

        // Construindo a resposta como uma string
        String response = "Medida de centralidade dos dados:\n"
                        + "Média: " + mean + "\n"
                        + "Mediana: " + median + "\n"
                        + "Moda: " + mode + "\n";

        return response;
    }
}
