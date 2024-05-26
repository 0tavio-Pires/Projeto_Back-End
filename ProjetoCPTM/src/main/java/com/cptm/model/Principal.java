package com.cptm.model;

import com.cptm.model.ControladorDeTrem;
import com.cptm.model.Estacao;
import com.cptm.model.Trem;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@RestController 
public class Principal {
 
    @GetMapping("/iniciarViagem")
    public static String iniciarViagem() throws InterruptedException {
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
        return "Viagem iniciada!";
    }
}
