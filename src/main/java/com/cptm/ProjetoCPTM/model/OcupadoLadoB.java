package com.cptm.ProjetoCPTM.model;

public interface OcupadoLadoB {
	
    static String ocupadoLadoB(boolean situacao) {
        return situacao ? "Ocupado" : "Livre";
    }
}
