package com.cptm.ProjetoCPTM.model;

public class Estacao {
    
    private String id;
    private String nome;
    private String localizacao;
    private String linha;
    private boolean status;
    private String ocupadoLadoA;
    private String ocupadoLadoB;
    private boolean ocorrencia;
    
    public Estacao() {}
    
    public Estacao(String id, String nome, String localizacao, String linha, boolean status, String ocupadoLadoA, String ocupadoLadoB, boolean ocorrencia) {
        this.id = id;
        this.nome = nome;
        this.localizacao = localizacao;
        this.linha = linha;
        this.status = status;
        this.ocupadoLadoA = ocupadoLadoA;
        this.ocupadoLadoB = ocupadoLadoB;
        this.ocorrencia = ocorrencia;
    }
    
    public String getOcupadoLadoA() {
		return ocupadoLadoA;
	}

	public void setOcupadoLadoA(String ocupadoLadoA) {
		this.ocupadoLadoA = ocupadoLadoA;
	}

	public String getOcupadoLadoB() {
		return ocupadoLadoB;
	}

	public void setOcupadoLadoB(String ocupadoLadoB) {
		this.ocupadoLadoB = ocupadoLadoB;
	}

	public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getLocalizacao() {
        return localizacao;
    }

    public void setLocalizacao(String localizacao) {
        this.localizacao = localizacao;
    }

    public String getLinha() {
        return linha;
    }

    public void setLinha(String linha) {
        this.linha = linha;
    }
    
    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public boolean isOcorrencia() {
        return ocorrencia;
    }

    public void setOcorrencia(boolean ocorrencia) {
        this.ocorrencia = ocorrencia;
    }
}
