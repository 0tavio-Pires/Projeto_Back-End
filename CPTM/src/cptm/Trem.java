<<<<<<< HEAD
package cptm;

import java.util.ArrayList;
import java.util.List;

public class Trem {

    private String id;
    private String nome;
    private int capacidade;
    private List<Estacao> estacoes;
    private int vagoes;
    private boolean status;
    private String modelo;

    public Trem(String id, String nome, int capacidade, ArrayList<Estacao> estacoes, int vagoes, boolean status, String modelo) {
        this.id = id;
        this.nome = nome;
        this.capacidade = capacidade;
        this.estacoes = estacoes;
        this.vagoes = vagoes;
        this.status = status;
        this.modelo = modelo;
    }
	
	public static void cadastraTrem(Trem trem, ArrayList<Trem> lista) {
		lista.add(trem);
	}
	
	public static void removeTrem(Trem trem, ArrayList<Trem> lista) {
		lista.remove(trem);
	}
	
	public void add(Object item, ArrayList<Object> lista) {
		lista.add(item);
	}
	
	public int MudaStatus(boolean situacao) {
		int status;
		if (situacao == true){
			status = 1;
		}
		else {
			status = 0;
		}
		return status;
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
=======
package cptm;
//sugestão
import java.util.ArrayList;
import java.util.List;

public class Trem {

    private String id;
    private String nome;
    private int capacidade;
    private List<Estacao> estacoes;
    private int vagoes;
    private boolean status;
    private String modelo;

    public Trem(String id, String nome, int capacidade, ArrayList<Estacao> estacoes, int vagoes, boolean status, String modelo) {
        this.id = id;
        this.nome = nome;
        this.capacidade = capacidade;
        this.estacoes = estacoes;
        this.vagoes = vagoes;
        this.status = status;
        this.modelo = modelo;
    }
	
	public static void cadastraTrem(Trem trem, ArrayList<Trem> lista) {
		lista.add(trem);
	}
	
	public static void removeTrem(Trem trem, ArrayList<Trem> lista) {
		lista.remove(trem);
	}
	
	public void add(Object item, ArrayList<Object> lista) {
		lista.add(item);
	}
	
	public int MudaStatus(boolean situacao) {
		int status;
		if (situacao == true){
			status = 1;
		}
		else {
			status = 0;
		}
		return status;
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
>>>>>>> 24f790473d1505e99a1e3a29f4f26e4685d7761f
}