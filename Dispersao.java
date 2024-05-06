package cptm;

import java.util.Arrays;

public class Dispersao {
	 public static int calculaAmplitude(int[] intervalos) {
	        Arrays.sort(intervalos); 
	        int menorValor = intervalos[0]; 
	        int maiorValor = intervalos[intervalos.length - 1]; 
	        return maiorValor - menorValor; //a vida é linda eu amo java
	    }

}
