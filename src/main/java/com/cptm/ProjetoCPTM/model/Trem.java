package com.cptm.ProjetoCPTM.model;

import java.util.List;

public class Trem {

    private String id;
    private String nome;
    private int capacidade;
    private List<Estacao> estacoes;
    private int vagoes;
    private boolean status;
    private String modelo;
    private int atraso;
    private int posicao;

    public Trem(String id, String nome, int capacidade, List<Estacao> estacoesTrem2, int vagoes, boolean status, String modelo, int atraso, int posicao) {
        this.id = id;
        this.nome = nome;
        this.capacidade = capacidade;
        this.estacoes = estacoesTrem2;
        this.vagoes = vagoes;
        this.status = status;
        this.modelo = modelo;
        this.atraso = atraso;
        this.posicao = posicao;
    } 
    
	public int getPosicao() {
		return posicao;
	}

	public void setPosicao(int posicao) {
		this.posicao = posicao;
	}

	public int getAtraso() {
		return atraso;
	}

	public void setAtraso(int atraso) {
		this.atraso = atraso;
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

	public int getCapacidade() {
		return capacidade;
	}

	public void setCapacidade(int capacidade) {
		this.capacidade = capacidade;
	}

	public List<Estacao> getEstacoes() {
		return estacoes;
	}

	public void setEstacoes(List<Estacao> estacoes) {
		this.estacoes = estacoes;
	}

	public int getVagoes() {
		return vagoes;
	}

	public void setVagoes(int vagoes) {
		this.vagoes = vagoes;
	}

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public String getModelo() {
		return modelo;
	}

	public void setModelo(String modelo) {
		this.modelo = modelo;
	}
}