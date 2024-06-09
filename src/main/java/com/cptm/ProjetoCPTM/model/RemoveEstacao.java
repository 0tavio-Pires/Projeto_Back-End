package com.cptm.ProjetoCPTM.model;

import java.util.ArrayList;

public interface RemoveEstacao {

    static void removeEstacao(Estacao estacao, ArrayList<Estacao> estacoes) {
        estacoes.remove(estacao);
    }
}
