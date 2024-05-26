package com.cptm.model;

public interface MudaStatusTrem {
	
	public static int MudaStatusTrem(boolean situacao) {
		int status;
		if (situacao == true){
			status = 1;
		}
		else {
			status = 0;
		}
		return status;
	}
}
