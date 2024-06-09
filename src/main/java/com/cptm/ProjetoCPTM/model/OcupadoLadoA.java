package com.cptm.ProjetoCPTM.model;

public interface OcupadoLadoA {
    
    static String ocupadoLadoA(boolean situacao) {
        return situacao ? "Ocupado" : "Livre";
    }
}
