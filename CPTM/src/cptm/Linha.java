package cptm;

import java.util.ArrayList;
import java.util.List;

public class Linha {
	
    private String nome;
    private List<Trem> trens;
    private List<Estacao> estacoes;
    
    public Linha(String nome) {
        this.nome = nome;
        this.trens = new ArrayList<>();
        this.estacoes = new ArrayList<>();
    }
    
    public void addTrem(Trem Trem) {
        this.trens.add(Trem);
    }
    
    public void addEstacao(Estacao Estacao) {
        this.estacoes.add(Estacao);
    }

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public List<Trem> getTrens() {
		return trens;
	}

	public void setTrens(List<Trem> trens) {
		this.trens = trens;
	}

	public List<Estacao> getEstacoes() {
		return estacoes;
	}

	public void setEstacoes(List<Estacao> estacoes) {
		this.estacoes = estacoes;
	}
    
    
}