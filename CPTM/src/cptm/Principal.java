package cptm;

import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Principal {

	public static void main(String[] args) throws InterruptedException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Digite seu nome de usuário:");
        String userName = scanner.nextLine();
        System.out.println("Nome de usuário é: " + userName);

        // Criação e adição das estações
        Estacao lapa = new Estacao("123", "Lapa", "Lapa", "Ruby", true, false, false, false);
        Estacao republica = new Estacao("456", "República", "República", "Azul", true, false, false, false);
        List<Estacao> estacoes = new ArrayList<>();
        estacoes.add(lapa);
        estacoes.add(republica);

        // Criação do trem
        Trem thomas = new Trem("654321", "Thomas", 2021, new ArrayList<>(estacoes), 20, true, "Rugby");

        // Inicializando o controlador de trem
        ControladorDeTrem controlador = new ControladorDeTrem(thomas, estacoes);
        controlador.iniciarViagem();

        scanner.close();
    }
}