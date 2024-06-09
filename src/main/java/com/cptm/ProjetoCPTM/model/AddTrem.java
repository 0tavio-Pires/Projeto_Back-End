package com.cptm.ProjetoCPTM.model;

import java.util.ArrayList;
import java.util.List;

public interface AddTrem {
	
	public static void cadastraTrem(Trem trem, List<Trem> trens) {
		trens.add(trem);
	}
	
	public static void add(Object item, ArrayList<Object> lista) {
		lista.add(item);
	}
}
