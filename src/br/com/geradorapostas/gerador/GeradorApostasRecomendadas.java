/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geradorapostas.gerador;

import java.util.ArrayList;
import java.util.List;

import br.com.geradorapostas.base.Aposta;
import br.com.geradorapostas.base.Modalidade;
import br.com.geradorapostas.base.SorteioRealizado;
import br.com.geradorapostas.gerador.verificador.GerenciadorVerifApostasRec;
import br.com.geradorapostas.gerador.verificador.VerificadorApostaRecomendada;

/**
 *
 * @author usuario
 */
public class GeradorApostasRecomendadas implements Runnable {
    
    private final Integer qtdeApostas; 
    private final Integer qtdeNumeros; 
    private final Modalidade modalidade; 
    private final List<SorteioRealizado> sorteiosRealizados;
    private final GeradorEstadoListener geradorEstadoListener;
    
    private List<Aposta> apostasGeradas;

    public GeradorApostasRecomendadas(Integer qtdeApostas, Integer qtdeNumeros, Modalidade modalidade, List<SorteioRealizado> sorteiosRealizados, GeradorEstadoListener geradorEstadoListener) {
        this.qtdeApostas = qtdeApostas;
        this.qtdeNumeros = qtdeNumeros;
        this.modalidade = modalidade;
        this.sorteiosRealizados = sorteiosRealizados;
        this.geradorEstadoListener = geradorEstadoListener;
    }

    @Override
    public void run() {
        apostasGeradas = gerarApostasRecomendadas();
    }
    
    private List<Aposta> gerarApostasRecomendadas() {
        
        ArrayList<Aposta> apostasRecomendadas = new ArrayList<>();
        
        if (qtdeApostas <= 0) {
            throw new IllegalStateException("Quantidade de Apostas não pode ser menor ou igual a 0");
        }
        
        while (apostasRecomendadas.size() < qtdeApostas) {
            
            // Obtem uma aposta             
            
            Aposta aposta = Aposta.obterAposta(modalidade.getMinNumero(), modalidade.getMaxNumero(), qtdeNumeros);

            // Verifica se a aposta é recomendada 
            
            VerificadorApostaRecomendada verificadorApostas = GerenciadorVerifApostasRec.getVerificador("3mf");
            
            Boolean apostaRecomendada = verificadorApostas.isApostaRecomendada(modalidade, aposta, sorteiosRealizados);

            if (apostaRecomendada) {
                if (!apostasRecomendadas.contains(aposta)) {
                	apostasRecomendadas.add(aposta);
                	geradorEstadoListener.incrementarApostasGeradas();
				}
            }
        }
        
        return apostasRecomendadas;
    }

    public List<Aposta> getApostasGeradas() {
        return apostasGeradas;
    }
    
}
