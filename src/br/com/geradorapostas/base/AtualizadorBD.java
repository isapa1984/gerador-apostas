package br.com.geradorapostas.base;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import br.com.geradorapostas.util.bd.GerenciadorConexaoBD;
import br.com.geradorapostas.util.bd.InstrucaoSQL;

public class AtualizadorBD {
	
	public static void atualizarBancoDeDados(Modalidade modalidade, File arquivoSorteios) {
		
		try {
		
			// Obtém os sorteios realizados do arquivo fornecido da caixa
			System.out.printf("=> Obtendo sorteios realizados do arquivo %s...", arquivoSorteios.getName());
			List<SorteioRealizado> sorteiosRealizadosArquivo = obterSorteiosRealizadosDoArquivo(modalidade, arquivoSorteios);
			System.out.printf("OK\n");

			// Obtém o número do último sorteio realizado no arquivo
			
			Integer numUltimoSorteioArquivo = sorteiosRealizadosArquivo.get(sorteiosRealizadosArquivo.size() - 1).getNumConcurso();

			// Obtém o número do último sorteio existente no banco
			
			Integer numUltimoSorteioBanco = getNumUltimoSorteio(modalidade);
			
			// Atualiza o banco se o numero do último sorteio no banco for menor que o do arquivo
			
			if (numUltimoSorteioBanco < numUltimoSorteioArquivo) {
				
				System.out.printf("=> Inserindo os novos sorteios\n");
				
				// Obtém o índice do próximo sorteio após o último
				
				Integer indInicio = sorteiosRealizadosArquivo.indexOf(numUltimoSorteioBanco) + 1;
				
				if (indInicio < 0) {
					indInicio = 0;
				}
				
				// Insere os novos sorteios
				
				PreparedStatement stInsertSorteioReali = GerenciadorConexaoBD.getConexao().prepareStatement(InstrucaoSQL.getInstrucao("sorteios_realizados.insert.novo_registro"));
				
				Integer totalRegistrosInsercao = sorteiosRealizadosArquivo.size() - indInicio;
				Integer ctRegInseridos = 0;
				
				for (int i = indInicio; i < sorteiosRealizadosArquivo.size(); i++) {
					
					// id_modalidade
					
					stInsertSorteioReali.setInt(1, modalidade.getId());
					
					// num_concurso
					
					stInsertSorteioReali.setInt(2, sorteiosRealizadosArquivo.get(i).getNumConcurso());
					
					// data
					
					String data = new SimpleDateFormat("dd/MM/yyyy").format(sorteiosRealizadosArquivo.get(i).getData());
					stInsertSorteioReali.setString(3, data);
					
					// numeros_sorteados
					
					stInsertSorteioReali.setString(4, sorteiosRealizadosArquivo.get(i).getNumerosSorteados().toString().replaceAll("(\\[|\\])", ""));
					
					stInsertSorteioReali.executeUpdate();
					
					ctRegInseridos++;
					
					System.out.printf("  => %d registros inseridos de %d\r", ctRegInseridos, totalRegistrosInsercao);
				}
				
				System.out.printf("  => OK\n");
				
				// Atualiza as estatísticas dos números
				
				EstatisticasNumero estatisticasNumero = EstatisticasNumero.obterEstatisticas(modalidade, 0);
				
				
			}
			else {
				System.out.printf("O banco de dados já está atualizado\n");
			}
		} 
		catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	private static Integer getNumUltimoSorteio(Modalidade modalidade) {
		
		Integer numUltimoSorteioBanco = 0;
		
		try {
			PreparedStatement stNumUltimoSorteio = GerenciadorConexaoBD.getConexao().prepareStatement(InstrucaoSQL.getInstrucao("sorteios_realizados.select.num_ultimo_sorteio"));
			stNumUltimoSorteio.setInt(1, modalidade.getId());
			ResultSet rs = stNumUltimoSorteio.executeQuery();
			
			if (rs.next()) {
				numUltimoSorteioBanco = rs.getInt("num_ultimo_sorteio");
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return numUltimoSorteioBanco;
	}
	
    private static List<SorteioRealizado> obterSorteiosRealizadosDoArquivo(Modalidade modalidade, File arquivo) {
        
        ArrayList<SorteioRealizado> sorteios = new ArrayList<>();
        
		try {
			
			if (arquivo.exists()) {
				Document htmlDoc = Jsoup.parse(arquivo, null);
				Elements trElements = htmlDoc.select("tr:has(td)");
				
				for (Element trElement : trElements) {
					
					Elements tdElements = trElement.children();
					
					if (tdElements.size() < 6) {
						continue;
					}
					
					Integer numConcurso 				= Integer.parseInt(tdElements.get(0).text());
					Date dataRealizacao 				= new SimpleDateFormat("dd/MM/yyyy").parse(tdElements.get(1).text());
					ArrayList<Integer> numerosSorteados = new ArrayList<>();
					
					for (int i = 1, j = 2; i <= modalidade.getQtdeMinMarcacao(); i++, j++) {
						numerosSorteados.add(Integer.parseInt(tdElements.get(j).text()));
					}
					
					sorteios.add(new SorteioRealizado(numConcurso, dataRealizacao, numerosSorteados));
				}
			}
			else {
				throw new FileNotFoundException(String.format("Arquivo \"%s\" não encontrado!", arquivo.getName()));
			}
			
		} catch (IOException | ParseException e) {
			e.printStackTrace();
		}
        
        return sorteios;
    }
	
}
