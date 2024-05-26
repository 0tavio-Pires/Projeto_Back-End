package cptm;

import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Principal {

	public static void main(String[] args) {
		
	    Scanner myObj = new Scanner(System.in);  // Create a Scanner object
	    System.out.println("1 - METER ESTACAO");
	    System.out.println("2 - SACAR ESTACAO");
	    System.out.println("3 - ENSACAR ESTACAO");
	    
	    String userName = myObj.nextLine();  // Read user input
	    
	    System.out.println("Username is: " + userName);  // Output user input
	    
		Estacao lapa = new Estacao("123", "lapa", "lapa", "ruby", false, false, false, false);
		Estacao republica = new Estacao("456", "republica", "republica", "azul", false, false, false, false);
		
		ArrayList<Estacao> estacoes = new ArrayList<>();
		
		AddEstacao.cadastraEstacao(lapa, estacoes);
		AddEstacao.cadastraEstacao(republica, estacoes);
		
		Trem thomas = new Trem("654321", "Thomas", 2021, estacoes, 20, true, "Rugby");
	}
}