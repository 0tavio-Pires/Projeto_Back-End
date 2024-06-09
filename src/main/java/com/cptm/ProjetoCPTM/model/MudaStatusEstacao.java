package com.cptm.ProjetoCPTM.model;

public interface MudaStatusEstacao {

    static String mudaStatusEstacao(boolean situacao) {
        String status;
        if (situacao) {
            status = "Operando";
        } else {
            status = "Desativado";
        }
        return status;
    }
}
