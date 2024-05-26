package com.cptm.model;

import java.util.List;

import com.cptm.model.Estacao;
import com.cptm.model.Trem;

public class ControladorDeTrem implements Trem{
    private Trem trem;
    private List<Estacao> rota;
    private int estacaoAtualIndex;

    public ControladorDeTrem(Trem trem, List<Estacao> rotaInicial) {
        this.trem = trem;
        this.rota = rotaInicial;
        this.estacaoAtualIndex = 0;
    }

    public void iniciarViagem() throws InterruptedException {
    	for (Estacao estacao : rota) {
            ocuparEstacao(estacao, true);
            System.out.println(gerarJsonStatusEstacoes());
            Thread.sleep(5000);
            ocuparEstacao(estacao, false);
        }
        System.out.println("O trem " + trem.getNome() + " completou sua rota.");
    }

    private boolean podePararNaEstacao(Estacao estacao) {
        return estacao.isStatus() && !estacao.isOcorrencia();
    }

    private void ocuparEstacao(Estacao estacao, boolean ocupar) {
        if (ocupar) {
            estacao.setOcupadoLadoA(true); 
        } else {
            estacao.setOcupadoLadoA(false);
        }
    }

    private String gerarJsonStatusEstacoes() {
    	StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("{\n\"estacoes\": [\n");
        for (Estacao estacao : rota) {
            jsonBuilder.append(String.format("\t{\"nome\": \"%s\", \"ocupado\": %b, \"tremId\": %s},\n",
                    estacao.getNome(), estacao.isOcupadoLadoA(), estacao.isOcupadoLadoA() ? "\"" + trem.getId() + "\"" : "null"));
        }
        jsonBuilder.deleteCharAt(jsonBuilder.length() - 2); // Remove a última vírgula
        jsonBuilder.append("\n]\n}");
        return jsonBuilder.toString();
    }
}
	
