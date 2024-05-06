package cptm;

public class Main {
	    public static void main(String[] args) {

	        int[] intervalos = {2,2, 5, 3, 4, 4, 6, 3, 3, 5, 3,2,2,2};

	        
	        double media = Centralidade.calculaMedia(intervalos);
	        System.out.println("Média: " + media);
	        double mediana = Centralidade.calculaMediana(intervalos);
	        System.out.println("Mediana: " + mediana);
	        
	        
	        
	        int[] modaEFrequencia = Centralidade.calculaModa(intervalos);
	        int moda = modaEFrequencia[0];
	        int frequenciaModa = modaEFrequencia[1];
	        //int moda = Centralidade.calculaModa(intervalos);
	        System.out.println("Moda: " + moda);
	        System.out.println("a frequencia de vezes que a moda aparece é: " + frequenciaModa);
	        
	        
	        int amplitude = Dispersao.calculaAmplitude(intervalos);
	        System.out.println("Amplitude: " + amplitude);
	    }
	}


