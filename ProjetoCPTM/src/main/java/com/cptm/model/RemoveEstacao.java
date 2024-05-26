package com.cptm.model;

import java.util.ArrayList;

public interface RemoveEstacao {
		
	public static void removeEstacao(Estacao estacao, ArrayList<Estacao> lista) {
		lista.remove(estacao);
	}	
}
