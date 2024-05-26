package com.cptm.model;

import java.util.LinkedList;
import java.util.ListIterator;

public class CircularTrainLines {

    private LinkedList<String> linha7;
    private LinkedList<String> linha8;
    private LinkedList<String> linha10;
    private LinkedList<String> linha11;
    private LinkedList<String> linha12;
    private LinkedList<String> linha13;

    public CircularTrainLines() {
        linha7 = createCircularList(new String[]{"Luz", "Barra Funda", "Pirituba", "Caieiras", "Franco da Rocha", "Francisco Morato", "Campo Limpo Paulista", "Jundiaí"});
        linha8 = createCircularList(new String[]{"Júlio Prestes", "Barra Funda", "Palmeiras-Barra Funda", "Osasco", "Carapicuíba", "Barueri", "Itapevi", "Amador Bueno"});
        linha10 = createCircularList(new String[]{"Brás", "Tamanduateí", "Santo André", "Mauá", "Ribeirão Pires", "Rio Grande da Serra"});
        linha11 = createCircularList(new String[]{"Luz", "Brás", "Guaianases", "Ferraz de Vasconcelos", "Poá", "Suzano", "Mogi das Cruzes", "Estudantes"});
        linha12 = createCircularList(new String[]{"Brás", "Tatuapé", "Engenheiro Goulart", "USP Leste", "Itaim Paulista", "Calmon Viana"});
        linha13 = createCircularList(new String[]{"Engenheiro Goulart", "Guarulhos-Cecap", "Aeroporto-Guarulhos"});

        printCircularList(linha7);
        printCircularList(linha8);
        printCircularList(linha10);
        printCircularList(linha11);
        printCircularList(linha12);
        printCircularList(linha13);
    }

    private LinkedList<String> createCircularList(String[] stations) {
        LinkedList<String> list = new LinkedList<>();
        for (String station : stations) {
            list.add(station);
        }
        return list;
    }

    private void printCircularList(LinkedList<String> list) {
        if (list.isEmpty()) return;

        ListIterator<String> iterator = list.listIterator();
        int startIndex = iterator.nextIndex();

        for (int i = 0; i < list.size() * 2; i++) {
            if (!iterator.hasNext()) {
                iterator = list.listIterator(); // Restart from the beginning
            }
            System.out.print(iterator.next() + " -> ");
        }
        System.out.println("(repeat)");
    }

    public static void main(String[] args) {
        new CircularTrainLines();
    }
}