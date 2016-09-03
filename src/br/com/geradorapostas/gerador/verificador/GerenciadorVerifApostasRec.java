/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geradorapostas.gerador.verificador;

/**
 *
 * @author usuario
 */
public class GerenciadorVerifApostasRec {
    public static VerificadorApostaRecomendada getVerificador(String idVerif) {
        
        if (idVerif.equals("pdr")) {
            return new VerifApostaRecPadrao();
        }
        
        if (idVerif.equals("3mf")) {
            return new VerifApostaRecTresMaioresFaixas();
        }
        
        return null;
    }
}
