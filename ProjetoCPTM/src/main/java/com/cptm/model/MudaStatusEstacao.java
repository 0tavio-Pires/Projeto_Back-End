package com.cptm.model;

public interface MudaStatusEstacao {
	
		public static String mudaStatusEstacao(boolean situacao) {
		
		String status;
		
		if (situacao == true){
			status = "Operando";
		} else {
			status = "Desativado";
		}
		return status;
	}
}
