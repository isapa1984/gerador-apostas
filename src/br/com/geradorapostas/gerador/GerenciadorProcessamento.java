/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geradorapostas.gerador;

import br.com.geradorapostas.base.ProgramaParametros;
import br.com.geradorapostas.base.bd.SorteioRealizado;
import br.com.geradorapostas.base.Aposta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author usuario
 */
public class GerenciadorProcessamento {

    private static final Integer QTDE_APOSTAS_POR_THREAD        = 5;
    private static final Integer MIN_APOSTAS_PROC_MULTI_THREAD  = 5;

    public static List<Aposta> gerarApostas(ProgramaParametros parametros, List<SorteioRealizado> sorteiosRealizados) {

        List<Aposta> apostasGeradas = new ArrayList<>();
        GeradorEstadoListenerPadrao geradorEstadoListenerImpl = new GeradorEstadoListenerPadrao();

        Integer numThreads, numApostasPorThread;

        if (parametros.getQtdeApostas() > MIN_APOSTAS_PROC_MULTI_THREAD) {
            numThreads = Math.round(parametros.getQtdeApostas() / QTDE_APOSTAS_POR_THREAD);
            numApostasPorThread = Math.round(parametros.getQtdeApostas() / numThreads);
        } else {
            numThreads = 1;
            numApostasPorThread = parametros.getQtdeApostas();
        }

        HashMap<Thread, GeradorApostasRecomendadas> threadsGeradores = new HashMap<>();

        for (int i = 1; i <= numThreads; i++) {
            GeradorApostasRecomendadas geradorApostasRecomendadas = new GeradorApostasRecomendadas(
                    numApostasPorThread, 
                    parametros.getQtdeNumerosAposta(), 
                    parametros.getModalidade(), 
                    sorteiosRealizados, 
                    geradorEstadoListenerImpl
            );
            Thread threadExecucao = new Thread(geradorApostasRecomendadas);
            threadExecucao.start();
            threadsGeradores.put(threadExecucao, geradorApostasRecomendadas);
        }

        Boolean concluiuProcessamento = false;

        while (!concluiuProcessamento) {
            Integer threadConcluidas = 0;

            for (Map.Entry<Thread, GeradorApostasRecomendadas> threadsMap : threadsGeradores.entrySet()) {
                Thread threadGerador = threadsMap.getKey();
                if (threadGerador.getState() == Thread.State.TERMINATED) {
                    threadConcluidas++;
                }
            }

            if (threadConcluidas == threadsGeradores.size()) {
                concluiuProcessamento = true;
            }
        }

        for (Map.Entry<Thread, GeradorApostasRecomendadas> entry : threadsGeradores.entrySet()) {
            GeradorApostasRecomendadas gerador = entry.getValue();
            apostasGeradas.addAll(gerador.getApostasGeradas());
        }

        return apostasGeradas;
    }

}
