/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geradorapostas.gerador;

import br.com.geradorapostas.base.Aposta;
import br.com.geradorapostas.util.ArquivoUtil;
import java.io.IOException;
import java.util.List;

/**
 *
 * @author usuario
 */
public class GeradorArquivoApostas {
    
    public static void gerarArquivoApostas(List<Aposta> apostas, String nomeArquivo) throws IOException {
        StringBuilder conteudoArquivo = new StringBuilder();
            
        for (int i = 0; i < apostas.size(); i++) {
            Aposta aposta = apostas.get(i);

            conteudoArquivo.append(aposta.getNumerosStr());

            // Se ainda não for o último registro, escrever quebra de linha
            
            if (i < (apostas.size() - 1)) {
                conteudoArquivo.append("\n");
            }
        }

        ArquivoUtil.escreverEmArquivoTexto(nomeArquivo, conteudoArquivo.toString());
    }
}
