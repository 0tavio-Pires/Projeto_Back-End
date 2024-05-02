package cptm;

public class OcupadoLadoA {
	
	public int mudaStatusEstacao(boolean situacao) {
		
		int ocupadoLadoA;
		
		if (situacao == true){
			ocupadoLadoA = 1;
		} else {
			ocupadoLadoA = 0;
		}
		return ocupadoLadoA;
	}
}
