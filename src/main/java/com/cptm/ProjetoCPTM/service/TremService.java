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
        estacoes.add(new Estacao("1", "Jundiaí", "Rua das Margaridas, 123", "Linha 7", true, null, null, false));
        estacoes.add(new Estacao("2", "Várzea Paulista", "Avenida dos Girassóis, 456", "Linha 7", true, null, null, false));
        estacoes.add(new Estacao("3", "Campo Limpo Paulista", "Travessa das Violetas, 789", "Linha 7", true, null, null, false));
        estacoes.add(new Estacao("4", "Boturuju", "Alameda das Orquídeas, 1011", "Linha 7", true, null, null, false));
        estacoes.add(new Estacao("5", "Francisco Morato", "Praça das Rosas, 1315", "Linha 7", true, null, null, false));
        estacoes.add(new Estacao("6", "Baltazar Fidélis", "Rua dos Lírios, 1718", "Linha 7", true, null, null, false));
        estacoes.add(new Estacao("7", "Franco da Rocha", "Avenida das Hortênsias, 1920", "Linha 7", true, null, null, false));
        estacoes.add(new Estacao("8", "Caieras", "Travessa dos Cravos, 2223", "Linha 7", true, null, null, false));
        estacoes.add(new Estacao("9", "Perus", "Alameda dos Jasmins, 2425", "Linha 7", true, null, null, false));
        estacoes.add(new Estacao("10", "Vila Aurora", "Praça das Tulipas, 2627", "Linha 7", true, null, null, false));
        estacoes.add(new Estacao("11", "Jaraguá", "Rua das Begônias, 2829", "Linha 7", true, null, null, false));
        estacoes.add(new Estacao("12", "Vila Clarice", "Avenida dos Narcisos, 3031", "Linha 7", true, null, null, false));
        estacoes.add(new Estacao("13", "Pirituba", "Travessa das Camélias, 3233", "Linha 7", true, null, null, false));
        estacoes.add(new Estacao("14", "Piqueri", "Alameda das Azaleias, 3435", "Linha 7", true, null, null, false));
        estacoes.add(new Estacao("15", "Lapa", "Praça das Bromélias, 3637", "Linha 7", true, null, null, false));
        estacoes.add(new Estacao("16", "Água Branca", "Rua das Dálias, 3839", "Linha 7", true, null, null, false));
        estacoes.add(new Estacao("17", "Barra Funda", "Avenida das Magnólias, 4041", "Linha 7", true, null, null, false));
        estacoes.add(new Estacao("18", "Luz", "Travessa dos Hibiscos, 4243", "Linha 7", true, null, null, false));
        estacoes.add(new Estacao("19", "Brás", "Alameda das Margaridas, 4445", "Linha 7", true, null, null, false));

        List<Trem> trens = new ArrayList<>();
        trens.add(new Trem("1", "Trem Thomas", 500, null, 8, true, "Modelo 9500", 0, 0)); // Inicia na posição 0 no lado A
        trens.add(new Trem("2", "Trem Emily", 500, null, 8, true, "Modelo 9500", 0, 1)); // Inicia na posição 1 no lado A
        trens.add(new Trem("3", "Trem James", 500, null, 8, true, "Modelo 9500", 0, 2)); // Inicia na posição 2 no lado A
        trens.add(new Trem("4", "Trem Perci", 500, null, 8, true, "Modelo 9500", 0, 3)); // Inicia na posição 0 no lado A
        trens.add(new Trem("5", "Trem Edward", 500, null, 8, true, "Modelo 9500", 0, 4)); // Inicia na posição 1 no lado A
        trens.add(new Trem("6", "Trem Toby", 400, null, 7, true, "Modelo 8500", 0, 14)); // Inicia na posição 2 no lado A
        trens.add(new Trem("7", "Trem Henry", 400, null, 7, true, "Modelo 8500", 0, 15)); // Inicia na posição 0 no lado A
        trens.add(new Trem("8", "Trem Gordon", 400, null, 7, true, "Modelo 8500", 0, 16)); // Inicia na posição 1 no lado A
        trens.add(new Trem("9", "Trem Ben", 400, null, 7, true, "Modelo 8500", 0, 17)); // Inicia na posição 2 no lado A
        trens.add(new Trem("10", "Trem Oliver", 400, null, 7, true, "Modelo 8500", 0, 18)); // Inicia na posição 0 no lado A

        linha = new Linha("0x3A7F2", "Linha 7 - Ruby", new ArrayList<>(estacoes), new ArrayList<>(trens));
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

    public void adicionarEstacao(Estacao estacao) {
        linha.getEstacoes().add(estacao);
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

    public void atualizarTrens() {
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
}
