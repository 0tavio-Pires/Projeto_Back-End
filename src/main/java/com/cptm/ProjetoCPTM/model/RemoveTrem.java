package com.cptm.ProjetoCPTM.model;

import java.util.ArrayList;

public interface RemoveTrem {
	
	public static void removeTrem(Trem trem, ArrayList<Trem> trens) {
		trens.remove(trem);
	}
}