package com.cptm.model;

import java.util.ArrayList;

import com.cptm.model.Estacao;

public interface AddEstacao {
	
	public static void cadastraEstacao(Estacao estacao, ArrayList<Estacao> lista) {
		lista.add(estacao);
	}
	
	public static void add(Object item, ArrayList<Object> lista) {
		lista.add(item);
	}
}