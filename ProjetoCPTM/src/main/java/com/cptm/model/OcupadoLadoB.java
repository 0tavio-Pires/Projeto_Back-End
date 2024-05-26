package com.cptm.model;

public interface OcupadoLadoB {
	
	public static int mudaStatusEstacao(boolean situacao) {
		
		int ocupadoLadoB;
		
		if (situacao == true){
			ocupadoLadoB = 1;
		} else {
			ocupadoLadoB = 0;
		}
		return ocupadoLadoB;
	}
}
