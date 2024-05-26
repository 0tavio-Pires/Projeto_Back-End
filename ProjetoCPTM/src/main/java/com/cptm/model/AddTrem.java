package com.cptm.model;

import java.util.ArrayList;

public interface AddTrem {
	
	public static void cadastraTrem(Trem trem, ArrayList<Trem> lista) {
		lista.add(trem);
	}
	
	public static void add(Object item, ArrayList<Object> lista) {
		lista.add(item);
	}
}
