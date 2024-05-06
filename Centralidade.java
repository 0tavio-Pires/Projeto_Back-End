package cptm;
import java.util.Arrays;

public class Centralidade {

		
	    public static double calculaMedia(int[] intervalos) {
	        double soma = 0;
	        for (int intervalo : intervalos) {
	            soma += intervalo;
	        }
	        return soma / intervalos.length;
	    }

 
	    
	    public static double calculaMediana(int[] intervalos) {
	        Arrays.sort(intervalos); //colocando ordem na casa da mae joana
	        int tamanho = intervalos.length;
	        if (tamanho % 2 == 0) {
	            int meio1 = tamanho / 2 - 1; 
	            int meio2 = tamanho / 2;
	            return (intervalos[meio1] + intervalos[meio2]) / 2.0;
	        } else {
	            int indice = tamanho / 2;
	            return intervalos[indice];
	        }
	    }

	    
	    public static int[] calculaModa(int[] intervalos) {
	        int moda = 0;
	        int maiorFrequencia = 0;
	        for (int intervalo : intervalos) {
	            int frequencia = 0; 
	            for (int Intervalo2 : intervalos) {
	                if (intervalo == Intervalo2) {
	                    frequencia++;
	                }
	            }
	            if (frequencia > maiorFrequencia ) {
	                moda = intervalo;
	                maiorFrequencia  = frequencia;
	            }
	        }
	        return new int[]{moda, maiorFrequencia}; //novo array com 2 valores (o de moda e a frequencia)
	    }
	}

