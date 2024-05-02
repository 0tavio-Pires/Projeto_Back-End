package cptm;

public class AtivaOcorrencia {
	
	public String ativaOcorrencia(boolean ocorrencias) {
		
		String ocorrencia;
		
		if (ocorrencias == true) {
			ocorrencia = "Ativa";
		} else {
			ocorrencia = "Desativada";
		}
		return ocorrencia;
	}
}
