package br.com.geradorapostas.base;

import java.io.File;
import java.util.List;

public class AtualizadorBD {
	
	public static void atualizarBancoDeDados(Modalidade modalidade, File arquivoSorteios) {
		
		// Obtém os sorteios realizados do arquivo fornecido da caixa
		System.out.printf("=> Obtendo sorteios realizados do arquivo %s...", arquivoSorteios.getName());
		List<SorteioRealizado> sorteiosRealizadosArquivo = SorteioRealizado.obterSorteiosRealizados(modalidade, arquivoSorteios);
		System.out.printf("OK\n");

		// Obtém o número do último sorteio realizado no arquivo
		
		Integer numUltimoSorteioArquivo = sorteiosRealizadosArquivo.get(sorteiosRealizadosArquivo.size() - 1).getNumConcurso();

		// Obtém o número do último sorteio existente no banco
		
		Integer numUltimoSorteioBanco = 0;
		
		SorteioRealizado ultimoSorteio = SorteioRealizado.obterUltimoSorteioRealizado(modalidade);
		
		if (ultimoSorteio != null) {
			numUltimoSorteioArquivo = ultimoSorteio.getNumConcurso();
		}
		
		// Atualiza o banco se o numero do último sorteio no banco for menor que o do arquivo
		
		if (numUltimoSorteioBanco < numUltimoSorteioArquivo) {
			
			System.out.printf("=> Inserindo os novos sorteios\n");
			
			// Obtém o índice do próximo sorteio após o último
			
			Integer indInicio = sorteiosRealizadosArquivo.indexOf(numUltimoSorteioBanco) + 1;
			
			if (indInicio < 0) {
				indInicio = 0;
			}
			
			// Insere os novos sorteios
			
//				for (int i = indInicio; i < sorteiosRealizadosArquivo.size(); i++) {
//					
//
//				}
			
			SorteioRealizado.inserirSorteios(sorteiosRealizadosArquivo.subList(indInicio, sorteiosRealizadosArquivo.size()), true); 
			
			System.out.printf("  => OK\n");
			
			// Atualiza as estatísticas dos números
			
			EstatisticasNumero estatisticasNumero = EstatisticasNumero.obterEstatisticas(modalidade, 0);
			
			
		}
		else {
			System.out.printf("O banco de dados já está atualizado\n");
		}
		
	}
	
}
