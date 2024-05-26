package com.cptm.model;

public interface AtivaOcorrencia {
	
	public static String ativaOcorrencia(boolean ocorrencias) {
		
		String ocorrencia;
		
		if (ocorrencias == true) {
			ocorrencia = "Ativa";
		} else {
			ocorrencia = "Desativada";
		}
		return ocorrencia;
	}
}