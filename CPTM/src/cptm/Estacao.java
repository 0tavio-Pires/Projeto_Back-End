package cptm;

import java.util.ArrayList;

public class Estacao {
	
	private String id;
	private String nome;
	private String localizacao;
	private String linha;
	
	public Estacao() {}
	
	public Estacao(String id, String nome, String localizacao, String linha) {
		super();
		this.id = id;
		this.nome = nome;
		this.localizacao = localizacao;
		this.linha = linha;
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
	
	public static void cadastraEstacao(Estacao estacao, ArrayList<Estacao> lista) {
		lista.add(estacao);
	}
	
	public static void removeEstacao(Estacao estacao, ArrayList<Estacao> lista) {
		lista.remove(estacao);
	}
	
	public void add(Object item, ArrayList<Object> lista) {
		lista.add(item);
	}
}
