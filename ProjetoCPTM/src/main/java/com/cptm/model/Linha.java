package com.cptm.model;

import java.util.ArrayList;
import java.util.List;

public interface Linha extends Estacao, Trem {
		
		static String id = "";
		static String nome = "";
		static List<Trem> trens = null;
		static List<Estacao> estacoes = null;
}