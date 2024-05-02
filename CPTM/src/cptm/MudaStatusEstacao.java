package cptm;

public class MudaStatusEstacao {
	
	public String mudaStatusEstacao(boolean situacao) {
		
		String status;
		
		if (situacao == true){
			status = "Operando";
		} else {
			status = "Desativado";
		}
		return status;
	}
}
