/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geradorapostas.gerador;

/**
 *
 * @author usuario
 */
public class GeradorEstadoListenerPadrao implements GeradorEstadoListener {

    private Integer totalApostasGeradas;

    public GeradorEstadoListenerPadrao() {
        totalApostasGeradas = 0;
    }
    
    @Override
    public synchronized void incrementarApostasGeradas() {
        totalApostasGeradas++;
        
        System.out.printf(" => Quantidade de Apostas Geradas: %d\r", totalApostasGeradas);
    }

    public Integer getTotalApostasGeradas() {
        return totalApostasGeradas;
    }
}
