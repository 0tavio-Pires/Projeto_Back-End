package com.cptm.ProjetoCPTM.model;

import java.util.ArrayList;

public interface AddEstacao {
	
	public static void cadastraEstacao(Estacao estacao, ArrayList<Estacao> estacoes) {
		estacoes.add(estacao);
	}
	
	public static void add(Object item, ArrayList<Object> estacoes) {
		estacoes.add(item);
	}
}