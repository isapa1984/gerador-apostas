/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geradorapostas.gerador.verificador;

import br.com.geradorapostas.base.Aposta;
import br.com.geradorapostas.base.bd.Modalidade;
import br.com.geradorapostas.base.bd.SorteioRealizado;

import java.util.List;

/**
 *
 * @author usuario
 */
public interface VerificadorApostaRecomendada {
    public Boolean isApostaRecomendada(Modalidade modalidade, Aposta aposta, List<SorteioRealizado> sorteioRealizados);
}
