package com.cptm.model;

import java.util.ArrayList;

public interface RemoveTrem {
	
	public static void removeTrem(Trem trem, ArrayList<Trem> lista) {
		lista.remove(trem);
	}
}
