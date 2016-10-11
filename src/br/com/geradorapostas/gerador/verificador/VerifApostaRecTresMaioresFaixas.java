/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geradorapostas.gerador.verificador;

import br.com.geradorapostas.base.Aposta;
import br.com.geradorapostas.base.Conferencia;
import br.com.geradorapostas.base.bd.Modalidade;
import br.com.geradorapostas.base.bd.SorteioRealizado;

import java.util.Collections;
import java.util.List;
import java.util.TreeMap;

/**
 *
 * @author usuario
 */
public class VerifApostaRecTresMaioresFaixas implements VerificadorApostaRecomendada {

    @Override
    public Boolean isApostaRecomendada(Modalidade modalidade, Aposta aposta, List<SorteioRealizado> sorteioRealizados) {
        
        Boolean apostaRecomendada = false;
        
        // Contabiliza quantas vezes a aposta premiou por faixa de premiação
        
        TreeMap<Integer, Integer> totalPremiacoesPorFaixa = new TreeMap<>(Collections.reverseOrder());
        
        for (SorteioRealizado sorteioRealizado : sorteioRealizados) {

            Conferencia conferencia = new Conferencia(aposta, sorteioRealizado);
            conferencia.conferir();

            if (modalidade.getAcertosPremiacao().contains(conferencia.getTotalAcertos())) {
                if (totalPremiacoesPorFaixa.containsKey(conferencia.getTotalAcertos())) {
                    Integer totalApostas = totalPremiacoesPorFaixa.get(conferencia.getTotalAcertos());
                    totalApostas++;
                    totalPremiacoesPorFaixa.put(conferencia.getTotalAcertos(), totalApostas);
                } else {
                    totalPremiacoesPorFaixa.put(conferencia.getTotalAcertos(), 1);
                }
            }
        }
        
        // Verifica se a aposta premiou pelo menos uma vez nas três maiores faixas de premiação
        // Se conseguiu, a aposta é recomendada.
        
        Integer qtdeFaixasPremiadas = 0;
        
        if ((totalPremiacoesPorFaixa.get(modalidade.getAcertosPremiacao().get(0)) != null)
         && (totalPremiacoesPorFaixa.get(modalidade.getAcertosPremiacao().get(0)) >= 1)) {
            qtdeFaixasPremiadas++;
        }
        
        if ((totalPremiacoesPorFaixa.get(modalidade.getAcertosPremiacao().get(1)) != null)
         && (totalPremiacoesPorFaixa.get(modalidade.getAcertosPremiacao().get(1)) >= 2)) {
            qtdeFaixasPremiadas++;
        }
        
        if ((totalPremiacoesPorFaixa.get(modalidade.getAcertosPremiacao().get(2)) != null)
         && (totalPremiacoesPorFaixa.get(modalidade.getAcertosPremiacao().get(2)) >= 3)) {
            qtdeFaixasPremiadas++;
        }
        
        if (qtdeFaixasPremiadas >= 3) {
            apostaRecomendada = true;
        }
        
        return apostaRecomendada;
    }
    
}
