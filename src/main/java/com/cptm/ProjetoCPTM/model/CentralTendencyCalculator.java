package com.cptm.ProjetoCPTM.model;

import java.util.Arrays;

public class CentralTendencyCalculator {

    public static double calculateMean(double[] data) {
        double sum = 0;
        for (double value : data) {
            sum += value;
        }
        return sum / data.length;
    }

    public static double calculateMedian(double[] data) {
        Arrays.sort(data);
        int n = data.length;
        if (n % 2 == 0) {
            return (data[n / 2 - 1] + data[n / 2]) / 2.0;
        } else {
            return data[n / 2];
        }
    }

    public static double calculateMode(double[] data) {
        Arrays.sort(data);
        double mode = data[0];
        int maxCount = 1;
        int currentCount = 1;
        for (int i = 1; i < data.length; i++) {
            if (data[i] == data[i - 1]) {
                currentCount++;
            } else {
                if (currentCount > maxCount) {
                    maxCount = currentCount;
                    mode = data[i - 1];
                }
                currentCount = 1;
            }
        }
        if (currentCount > maxCount) {
            mode = data[data.length - 1];
        }
        return mode;
    }

    public static void main(String[] args) {
        // Substitua os dados de exemplo pelos seus próprios dados
        double[] data = {1, 2, 3, 4, 5, 5, 5, 6, 7, 8, 9};

        // Calculando as medidas de centralidade
        double mean = calculateMean(data);
        double median = calculateMedian(data);
        double mode = calculateMode(data);

        // Exibindo os resultados
        System.out.println("Medida de centralidade dos dados:");
        System.out.println("Média: " + mean);
        System.out.println("Mediana: " + median);
        System.out.println("Moda: " + mode);
    }
}
