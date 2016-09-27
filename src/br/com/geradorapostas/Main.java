/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geradorapostas;

import br.com.geradorapostas.base.AtualizadorBD;
import br.com.geradorapostas.base.ProgramaParametros;

/**
 *
 * @author usuario
 */
public class Main {

    public static void main(String[] args) {
        
        try {
            
            // Valida os parâmetros fornecidos
            
        	args = new String[] {"-md=ms","-ae=d_megasc.htm"};
        	        	
        	ProgramaParametros parametros = ProgramaParametros.obterParametros(args);
        	
        	if (parametros.isAtualizarEstatisticas()) {
        		// Atualiza as estatísticas
        		AtualizadorBD.atualizarBancoDeDados(parametros.getModalidade(), parametros.getArquivoSorteios());
			}
        	else {
        		// Gera as apostas 
        	}
        	
//        	System.out.println("[1,2,3]".replaceAll("(\\[|\\])", ""));
        	
        	
        	return;
 /*           
            System.out.printf("\n");
            System.out.printf("-------------------------------------\n");
            System.out.printf("-- GERADOR DE APOSTAS RECOMENDADAS --\n");
            System.out.printf("-------------------------------------\n");
            System.out.printf("\n");

            // Obtém os sorteios realizados
            
            System.out.printf("1. Carregando sorteios realizados\n", parametros.getNomeArquivoSorteios());
            System.out.printf(" => Arquivo origem: %s\n", parametros.getNomeArquivoSorteios());
            
            List<SorteioRealizado> sorteioRealizados = SorteioRealizado.obterSorteiosRealizados(parametros.getNomeArquivoSorteios());
            
            System.out.printf(" => OK\n");
            
            // Realiza a geração das apostas
            
            System.out.printf("2. Iniciando geração das apostas\n");
            System.out.printf(" => Quantidade de Apostas para Gerar: %d\n", parametros.getQtdeApostas());
            
            List<Aposta> apostasGeradas = GerenciadorProcessamento.gerarApostas(parametros, sorteioRealizados);
            
            if (apostasGeradas.isEmpty()) {
                throw new IllegalStateException("Nenhuma aposta foi gerada.");
            }
            
            System.out.printf("\n");
            System.out.printf(" => OK\n");
            
            // Gerar arquivo com as apostas geradas
            
            System.out.printf("3. Gerando arquivo contendo as apostas geradas\n");
//            GeradorArquivoApostas.gerarArquivoApostas(apostasGeradas, parametros.getNomeArquivoApostas());
//            System.out.printf(" => Arquivo '%s' criado.\n", parametros.getNomeArquivoApostas());
 * 
 */
            
        } 
        catch (IllegalArgumentException | IllegalStateException ex) {
        	ex.printStackTrace();
        }
        
    }
}
