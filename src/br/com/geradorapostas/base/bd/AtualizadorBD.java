package br.com.geradorapostas.base.bd;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import br.com.geradorapostas.base.ProgramaParametros;

public class AtualizadorBD {
	
	private static final class SorteioArquivo {
	    private final Integer       numConcurso;
	    private final String        data;
	    private final List<Integer> numerosSorteados;
		
	    public SorteioArquivo(Integer numConcurso, String data, List<Integer> numerosSorteados) {
			this.numConcurso = numConcurso;
			this.data = data;
			this.numerosSorteados = numerosSorteados;
		}

		public Integer getNumConcurso() {
			return numConcurso;
		}

		public String getData() {
			return data;
		}

		public List<Integer> getNumerosSorteados() {
			return numerosSorteados;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((numConcurso == null) ? 0 : numConcurso.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			SorteioArquivo other = (SorteioArquivo) obj;
			if (numConcurso == null) {
				if (other.numConcurso != null)
					return false;
			} else if (!numConcurso.equals(other.numConcurso))
				return false;
			return true;
		}
		
	}
	
	// Atualiza as tabelas de sorteios realizados e estatísticas dos números
	
	public static void atualizarBancoDeDados(ProgramaParametros programaParametros) {
		
		try (Connection conexao = GerenciadorConexaoBD.getConexao()) {
			
			try (
				PreparedStatement stObterUltimoSorteio 	= conexao.prepareStatement(InstrucaoSQL.getInstrucao("sorteios_realizados.select.ultimo_sorteio"));
				PreparedStatement stInsertSorteioReali 	= conexao.prepareStatement(InstrucaoSQL.getInstrucao("sorteios_realizados.insert.novo_registro"));
				PreparedStatement stObterEstatsNumero 	= conexao.prepareStatement(InstrucaoSQL.getInstrucao("estatisticas_numeros.select.estats_numero"));
				PreparedStatement stInserirEstats 		= conexao.prepareStatement(InstrucaoSQL.getInstrucao("estatisticas_numeros.insert.nova_estatistica"));
				PreparedStatement stAtualizarEstats 	= conexao.prepareStatement(InstrucaoSQL.getInstrucao("estatisticas_numeros.update.estatistica"));
			) {
				
				// Inicia a transação
				conexao.setAutoCommit(false);
				
				// Obtém os sorteios realizados do arquivo fornecido da caixa
				
				File arquivoSorteios = programaParametros.getArquivoSorteios();
				Modalidade modalidade = programaParametros.getModalidade();
				
				System.out.printf("=> Obtendo sorteios realizados do arquivo %s...", arquivoSorteios.getName());
				List<SorteioArquivo> sorteiosArquivo = obterSorteiosDoArquivo(modalidade, arquivoSorteios);
				System.out.printf("OK\n");
				
				// Obtém o número do último sorteio realizado no arquivo
				
				Integer numUltimoSorteioArquivo = sorteiosArquivo.get(sorteiosArquivo.size() - 1).getNumConcurso();
				
				// Obtém o número do último sorteio existente no banco
				
				Integer numUltimoSorteioBanco = 0;
				
				ResultSet rsUltimoSorteio = stObterUltimoSorteio.executeQuery();
				
				if (rsUltimoSorteio.next()) {
					numUltimoSorteioBanco = rsUltimoSorteio.getInt("num_concurso"); 
				}
				
				rsUltimoSorteio.close();
				
				// Atualiza o banco se o numero do último sorteio no banco for menor que o do arquivo
				
				if (numUltimoSorteioBanco < numUltimoSorteioArquivo) {
					
					System.out.printf("=> Inserindo os novos sorteios\n");
					
					// Obtém o índice do próximo sorteio no arquivo após o último
					
					Integer indInicio = sorteiosArquivo.indexOf(numUltimoSorteioBanco);
					
					if (indInicio < 0) {
						indInicio = 0;
					}
					else {
						indInicio =+ 1;
					}
					
					// Insere os novos sorteios
					
					List<SorteioArquivo> novosSorteios = sorteiosArquivo.subList(indInicio, sorteiosArquivo.size());
					
					Integer contadorSorteioAtual 		= 1;
					Integer qtdeSorteiosParaProcessar 	= novosSorteios.size();
					
					for (SorteioArquivo novoSorteio : novosSorteios) {
						
						System.out.printf("  => Inserindo sorteio do concurso %d, registro %d de %d\r", novoSorteio.getNumConcurso(), contadorSorteioAtual, qtdeSorteiosParaProcessar);
						
						// id_modalidade
						stInsertSorteioReali.setInt(1, modalidade.getId());
						
						// num_concurso
						stInsertSorteioReali.setInt(2, novoSorteio.getNumConcurso());
						
						// data
						stInsertSorteioReali.setString(3, novoSorteio.getData());
						
						// numeros_sorteados
						stInsertSorteioReali.setString(4, novoSorteio.getNumerosSorteados().toString().replaceAll("(\\[|\\])", ""));
						
						stInsertSorteioReali.addBatch();
												
						contadorSorteioAtual++;
					}
					
					// Executa as operações que sobraram no lote
					stInsertSorteioReali.executeBatch();
					
					// Atualiza as estatísticas dos números para este sorteio
					
					System.out.printf("  => OK\n");
					System.out.printf("=> Atualizando as estatísticas dos números\n");
					
					contadorSorteioAtual = 1;
					
					for (SorteioArquivo novoSorteio : novosSorteios) {
						
						System.out.printf("  => Atualizando estatísticas a partir dos números do concurso %d, registro %d de %d\r", novoSorteio.getNumConcurso(), contadorSorteioAtual, qtdeSorteiosParaProcessar);
						
						stInserirEstats.clearBatch();
						stAtualizarEstats.clearBatch();
						
						// Para cada uns dos números da modalidade
						
						for (int numeroAtual = modalidade.getMinNumero(); numeroAtual <= modalidade.getMaxNumero(); numeroAtual++) {
							
							// Variáveis auxiliares para guardar as estatisticas atuais
							
							Integer qtdeRepeticoes; 
							Integer qtdeAtrasos; 
							Boolean existeEstatistica = false;
							
							// Obtém a estatística atual do número
							
							stObterEstatsNumero.setInt(1, modalidade.getId());
							stObterEstatsNumero.setInt(2, numeroAtual);
							
							ResultSet rsObterEstatsNumero = stObterEstatsNumero.executeQuery();
							
							// Se existe registro de estatísticas para o número
							
							if (rsObterEstatsNumero.next()) {
								// Utiliza os dados já existentes
								qtdeRepeticoes 		= rsObterEstatsNumero.getInt("qtde_repeticoes");
								qtdeAtrasos 		= rsObterEstatsNumero.getInt("qtde_atrasos");
								existeEstatistica 	= true;
							}
							else {
								// Senão atribui valor 0
								qtdeRepeticoes 	= 0;
								qtdeAtrasos 	= 0;
							}
							
							rsObterEstatsNumero.close();
							
							// Se o número apareceu no sorteio atual, contabiliza repetição e zera o atraso
							
							if (novoSorteio.getNumerosSorteados().contains(numeroAtual)) {
								qtdeRepeticoes++;
								qtdeAtrasos = 0;
							}
							else {
								// Senão contabiliza atraso
								qtdeAtrasos++;
							}
							
							// Se existe o registro de estatística no banco 
							
							if (existeEstatistica) {
								
								// Atualiza os dados
								
								stAtualizarEstats.setInt(1, qtdeRepeticoes);
								stAtualizarEstats.setInt(2, qtdeAtrasos);
								stAtualizarEstats.setInt(3, modalidade.getId());
								stAtualizarEstats.setInt(4, numeroAtual);
								
								stAtualizarEstats.addBatch();
							}
							else {
								
								// Senão cria um novo registro
								
								stInserirEstats.setInt(1, modalidade.getId());
								stInserirEstats.setInt(2, numeroAtual);
								stInserirEstats.setInt(3, qtdeRepeticoes);
								stInserirEstats.setInt(4, qtdeAtrasos);
								
								stInserirEstats.addBatch();
							}
							
						}
						
						stInserirEstats.executeBatch();
						stAtualizarEstats.executeBatch();
						
						contadorSorteioAtual++;
					}
					
					// Executa as operações que sobraram no lote
					stInserirEstats.executeBatch();
					stAtualizarEstats.executeBatch();
					
					// Finaliza a transação
					conexao.commit();
					
					System.out.printf("  => OK\n");
				}
				else {
					System.out.printf("O banco de dados já está atualizado\n");
				}
			}
			catch (SQLException | IOException | ParseException e) {
				conexao.rollback();
				e.printStackTrace();
			}
		} 
		catch (SQLException e1) {
			e1.printStackTrace();
		}
		
	}
	
	private static List<SorteioArquivo> obterSorteiosDoArquivo(Modalidade modalidade, File arquivo) throws IOException, ParseException {
		
		List<SorteioArquivo> sorteios = new ArrayList<>();
		
		if (arquivo.exists()) {
			Document htmlDoc = Jsoup.parse(arquivo, null);
			Elements trElements = htmlDoc.select("tr:has(td)");
			
			for (Element trElement : trElements) {
				
				Elements tdElements = trElement.children();
				
				if (tdElements.size() < 6) {
					continue;
				}
				
				Integer numConcurso 				= Integer.parseInt(tdElements.get(0).text());
				String dataRealizacao 				= tdElements.get(1).text();
				ArrayList<Integer> numerosSorteados = new ArrayList<>();
				
				for (int i = 1, j = 2; i <= modalidade.getQtdeMinMarcacao(); i++, j++) {
					numerosSorteados.add(Integer.parseInt(tdElements.get(j).text()));
				}
				
				sorteios.add(new SorteioArquivo(numConcurso, dataRealizacao, numerosSorteados));
			}
		}
		else {
			throw new FileNotFoundException(String.format("Arquivo \"%s\" não encontrado!", arquivo.getName()));
		}
			
		return sorteios;
	}
	
}
