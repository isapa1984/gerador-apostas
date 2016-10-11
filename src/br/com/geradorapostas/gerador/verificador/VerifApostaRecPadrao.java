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

import java.util.List;

/**
 *
 * @author igor de souza
 * 
 * Classe padrão de verificação de apostas recomendadas. Considera uma aposta recomendada aquela premiou pelo menos uma vez em qualquer
 * faixa de premiação 
 * 
 */
public class VerifApostaRecPadrao implements VerificadorApostaRecomendada {

    @Override
    public Boolean isApostaRecomendada(Modalidade modalidade, Aposta aposta, List<SorteioRealizado> sorteioRealizados) {
        
        // Realiza a conferência da aposta com os sorteios realizados
        
        Boolean apostaRecomendada = false;
        
        for (SorteioRealizado sorteioRealizado : sorteioRealizados) {

            Conferencia conferencia = new Conferencia(aposta, sorteioRealizado);
            conferencia.conferir();

            // Se o total de acertos está dentro da faixa de premiação, incluir Aposta

            if (modalidade.getAcertosPremiacao().contains(conferencia.getTotalAcertos())) {
                apostaRecomendada = true;
            }
        }
        
        return apostaRecomendada;
    }
    
}
