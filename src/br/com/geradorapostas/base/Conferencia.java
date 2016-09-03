/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geradorapostas.base;

import java.io.Serializable;

/**
 *
 * @author usuario
 */
@SuppressWarnings("serial")
public class Conferencia implements Serializable {

    private final Aposta aposta;
    private final SorteioRealizado sorteioRealizado;
    private Integer totalAcertos;
    private String resultado;

    public Conferencia(Aposta aposta, SorteioRealizado sorteioRealizado) {
        this.aposta             = aposta;
        this.sorteioRealizado   = sorteioRealizado;
        this.totalAcertos       = 0;
    }
    
    public void conferir() {
        
        if (aposta == null) {
            resultado = "ERRO: Não foi fornecida aposta.";
            return;
        }
        
        if ((aposta.getNumeros() == null) || (aposta.getNumeros().isEmpty())) {
            resultado = "ERRO: Aposta sem números.";
            return;
        }
        
        if (sorteioRealizado == null) {
            resultado = "ERRO: Não foi fornecido sorteio realizado.";
            return;
        }
        
        if ((aposta.getNumeros() == null) || (aposta.getNumeros().isEmpty())) {
            resultado = "ERRO: Sorteio realizado sem números.";
            return;
        }
        
        for (Integer numeroSorteado : sorteioRealizado.getNumerosSorteados()) {
            if (aposta.getNumeros().contains(numeroSorteado)) {
                totalAcertos++;
            }
        }
        
        resultado = "OK";
    }

    public SorteioRealizado getSorteioRealizado() {
        return sorteioRealizado;
    }
    
    public Integer getTotalAcertos() {
        return totalAcertos;
    }

    public String getResultado() {
        return resultado;
    }
}
