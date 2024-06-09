package com.cptm.ProjetoCPTM.model;

import java.util.ArrayList;

public class Linha {
	
	private String id;
	private String nome;
	private ArrayList<Estacao> estacoes;
	private ArrayList<Trem> trens;
	
	public Linha(String id, String nome, ArrayList<Estacao> estacoes, ArrayList<Trem> trens) {
		super();
		this.id = id;
		this.nome = nome;
		this.estacoes = estacoes;
		this.trens = trens;
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

	public ArrayList<Estacao> getEstacoes() {
		return estacoes;
	}

	public void setEstacoes(ArrayList<Estacao> estacoes) {
		this.estacoes = estacoes;
	}

	public ArrayList<Trem> getTrens() {
		return trens;
	}

	public void setTrens(ArrayList<Trem> trens) {
		this.trens = trens;
	}
}
