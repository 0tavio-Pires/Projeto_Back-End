package cptm;

public class OcupadoLadoB {
	
	public int mudaStatusEstacao(boolean situacao) {
		
		int ocupadoLadoB;
		
		if (situacao == true){
			ocupadoLadoB = 1;
		} else {
			ocupadoLadoB = 0;
		}
		return ocupadoLadoB;
	}
}
