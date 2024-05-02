package cptm;

<<<<<<< HEAD
=======
import java.util.ArrayList;

>>>>>>> 24f790473d1505e99a1e3a29f4f26e4685d7761f
public class Estacao {
	
	private String id;
	private String nome;
	private String localizacao;
	private String linha;
<<<<<<< HEAD
	private boolean status;
	private boolean ocupadoLadoA;
	private boolean ocupadoLadoB;
	private boolean ocorrencia;
	
	public Estacao() {}
	
	public Estacao(String id, String nome, String localizacao, String linha, boolean status, boolean ocupadoLadoA, boolean ocupadoLadoB, boolean ocorrencia) {
=======
	
	public Estacao() {}
	
	public Estacao(String id, String nome, String localizacao, String linha) {
>>>>>>> 24f790473d1505e99a1e3a29f4f26e4685d7761f
		super();
		this.id = id;
		this.nome = nome;
		this.localizacao = localizacao;
		this.linha = linha;
<<<<<<< HEAD
		this.status = status;
		this.ocupadoLadoB = ocupadoLadoA;
		this.ocupadoLadoB = ocupadoLadoB;
		this.ocorrencia = ocorrencia;
=======
>>>>>>> 24f790473d1505e99a1e3a29f4f26e4685d7761f
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
	
<<<<<<< HEAD
	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public boolean isOcupadoLadoA() {
		return ocupadoLadoA;
	}

	public void setOcupadoLadoA(boolean ocupadoLadoA) {
		this.ocupadoLadoA = ocupadoLadoA;
	}

	public boolean isOcupadoLadoB() {
		return ocupadoLadoB;
	}

	public void setOcupadoLadoB(boolean ocupadoLadoB) {
		this.ocupadoLadoB = ocupadoLadoB;
	}

	public boolean isOcorrencia() {
		return ocorrencia;
	}

	public void setOcorrencia(boolean ocorrencia) {
		this.ocorrencia = ocorrencia;
=======
	public static void cadastraEstacao(Estacao estacao, ArrayList<Estacao> lista) {
		lista.add(estacao);
	}
	
	public static void removeEstacao(Estacao estacao, ArrayList<Estacao> lista) {
		lista.remove(estacao);
	}
	
	public void add(Object item, ArrayList<Object> lista) {
		lista.add(item);
>>>>>>> 24f790473d1505e99a1e3a29f4f26e4685d7761f
	}
}
