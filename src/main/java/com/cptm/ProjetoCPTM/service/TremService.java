package com.cptm.ProjetoCPTM.service;

import org.springframework.stereotype.Service;
import com.cptm.ProjetoCPTM.model.Estacao;
import com.cptm.ProjetoCPTM.model.Trem;
import com.cptm.ProjetoCPTM.model.Linha;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

@Service
public class TremService {
    private Linha linha;

    public TremService() {
        List<Estacao> estacoes = new ArrayList<>();
        estacoes.add(new Estacao("1", "Estação Central", "Rua Central, 123", "Linha 1", false, null, null, false));
        estacoes.add(new Estacao("2", "Estação Norte", "Rua Norte, 456", "Linha 1", false, null, null, false));
        estacoes.add(new Estacao("3", "Estação Sul", "Rua Sul, 789", "Linha 1", false, null, null, false));
        estacoes.add(new Estacao("4", "Estação Oeste", "Rua Oeste, 1322", "Linha 1", false, null, null, false));

        List<Trem> trens = new ArrayList<>();
        trens.add(new Trem("1", "Trem 1", 200, null, 8, true, "Modelo A", 0, 0)); // Inicia na posição 0 no lado A
        trens.add(new Trem("2", "Trem 2", 200, null, 8, true, "Modelo A", 0, 1)); // Inicia na posição 1 no lado A
        trens.add(new Trem("3", "Trem 3", 200, null, 8, true, "Modelo A", 0, 2)); // Inicia na posição 2 no lado A

        linha = new Linha("1", "Linha 1", new ArrayList<>(estacoes), new ArrayList<>(trens));
        iniciarAtualizacao();
    }

    public Linha getLinha() {
        return linha;
    }

    public List<Estacao> getEstacoes() {
        return linha.getEstacoes();
    }

    public void ocorrencia(int id, int atraso) {
        for (Trem trem : linha.getTrens()) {
            if (trem.getId().equals(String.valueOf(id))) {
                trem.setAtraso(atraso);
                break;
            }
        }
    }

    public void adicionarTrem(Trem trem) {
        linha.getTrens().add(trem);
    }

    public void alterarStatusTrem(String id, boolean status) {
        for (Trem trem : linha.getTrens()) {
            if (trem.getId().equals(id)) {
                if (!status) {
                    removerTremDaEstacao(trem);
                }
                trem.setStatus(status);
                break;
            }
        }
    }

    private void removerTremDaEstacao(Trem trem) {
        List<Estacao> estacoes = linha.getEstacoes();
        for (Estacao estacao : estacoes) {
            if (trem.getId().equals(estacao.getOcupadoLadoA())) {
                estacao.setOcupadoLadoA(null);
            } else if (trem.getId().equals(estacao.getOcupadoLadoB())) {
                estacao.setOcupadoLadoB(null);
            }
        }
    }

    private void iniciarAtualizacao() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                atualizarTrens();
            }
        }, 0, 5000);
    }

    private void atualizarTrens() {
        List<Estacao> estacoes = linha.getEstacoes();
        int tamanhoEstacoes = estacoes.size();

        for (Trem trem : linha.getTrens()) {
            if (!trem.isStatus()) {
                continue; // Ignora trens com status false
            }

            if (trem.getAtraso() > 0) {
                trem.setAtraso(trem.getAtraso() - 1);
            } else {
                int posicaoAtual = trem.getPosicao();
                Estacao estacaoAtual = estacoes.get(posicaoAtual);
                Estacao proximaEstacao;

                if (estacaoAtual.getOcupadoLadoA() != null && estacaoAtual.getOcupadoLadoA().equals(trem.getId())) {
                    // Trem está no lado A
                    if (posicaoAtual == tamanhoEstacoes - 1) {
                        // Última estação no lado A, muda para lado B
                        estacaoAtual.setOcupadoLadoA(null);
                        proximaEstacao = estacoes.get(posicaoAtual);
                        // Verifica se a próxima estação está livre no lado B
                        if (proximaEstacao.getOcupadoLadoB() == null) {
                            trem.setPosicao(posicaoAtual);
                            proximaEstacao.setOcupadoLadoB(trem.getId());
                        } else {
                            estacaoAtual.setOcupadoLadoA(trem.getId()); // Mantém o trem na estação atual
                        }
                    } else {
                        // Próxima estação no lado A
                        estacaoAtual.setOcupadoLadoA(null);
                        proximaEstacao = estacoes.get(posicaoAtual + 1);
                        // Verifica se a próxima estação está livre no lado A
                        if (proximaEstacao.getOcupadoLadoA() == null) {
                            trem.setPosicao(posicaoAtual + 1);
                            proximaEstacao.setOcupadoLadoA(trem.getId());
                        } else {
                            estacaoAtual.setOcupadoLadoA(trem.getId()); // Mantém o trem na estação atual
                        }
                    }
                } else if (estacaoAtual.getOcupadoLadoB() != null && estacaoAtual.getOcupadoLadoB().equals(trem.getId())) {
                    // Trem está no lado B
                    if (posicaoAtual == 0) {
                        // Primeira estação no lado B, muda para lado A
                        estacaoAtual.setOcupadoLadoB(null);
                        proximaEstacao = estacoes.get(posicaoAtual);
                        // Verifica se a próxima estação está livre no lado A
                        if (proximaEstacao.getOcupadoLadoA() == null) {
                            trem.setPosicao(posicaoAtual);
                            proximaEstacao.setOcupadoLadoA(trem.getId());
                        } else {
                            estacaoAtual.setOcupadoLadoB(trem.getId()); // Mantém o trem na estação atual
                        }
                    } else {
                        // Próxima estação no lado B
                        estacaoAtual.setOcupadoLadoB(null);
                        proximaEstacao = estacoes.get(posicaoAtual - 1);
                        // Verifica se a próxima estação está livre no lado B
                        if (proximaEstacao.getOcupadoLadoB() == null) {
                            trem.setPosicao(posicaoAtual - 1);
                            proximaEstacao.setOcupadoLadoB(trem.getId());
                        } else {
                            estacaoAtual.setOcupadoLadoB(trem.getId()); // Mantém o trem na estação atual
                        }
                    }
                } else {
                    // Primeira vez que o trem chega na estação
                    estacaoAtual.setOcupadoLadoA(trem.getId());
                }
            }
        }
    }
}
