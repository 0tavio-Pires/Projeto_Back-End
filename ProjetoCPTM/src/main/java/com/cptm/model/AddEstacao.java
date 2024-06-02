package com.cptm.model;

import java.util.ArrayList;

import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/addEstacao")
public interface AddEstacao {
	
	public static void cadastraEstacao(Estacao estacao, ArrayList<Estacao> lista) {
		lista.add(estacao);
	}
	
	public static void add(Object item, ArrayList<Object> lista) {
		lista.add(item);
	}
}