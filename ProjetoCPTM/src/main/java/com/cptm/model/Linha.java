package com.cptm.model;

import java.util.ArrayList;
import java.util.List;

public class Linha {
		
		private String id;
		private String nome;
		private List<Trem> trens;
		private List<Estacao> estacoes;
		
		public Linha(String id, String nome, List<Trem> trens, List<Estacao> estacoes) {
			super();
			this.id = id;
			this.nome = nome;
			this.trens = trens;
			this.estacoes = estacoes;
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