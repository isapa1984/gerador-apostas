/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geradorapostas;

import br.com.geradorapostas.base.Modalidade;

/**
 *
 * @author usuario
 */
public class Testes {
    public static void main(String[] args) {
        
        try {
            System.out.println(Modalidade.obterModalidade("lf").getAcertosPremiacao());

        } 
        catch (Exception e) {
            System.err.println(e.getMessage());
        }
        
    }
}
