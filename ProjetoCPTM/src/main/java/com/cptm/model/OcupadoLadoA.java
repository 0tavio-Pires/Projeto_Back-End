package com.cptm.model;

public interface OcupadoLadoA {
	
		public static int mudaStatusEstacao(boolean situacao) {
		
		int ocupadoLadoA;
		
		if (situacao == true){
			ocupadoLadoA = 1;
		} else {
			ocupadoLadoA = 0;
		}
		return ocupadoLadoA;
	}
}
