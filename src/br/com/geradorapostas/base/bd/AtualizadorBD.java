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
			
			SorteioRealizado.inserirSorteios(sorteiosRealizadosArquivo.subList(indInicio, sorteiosRealizadosArquivo.size()), true); 
			
			System.out.printf("  => OK\n");
			
			// Atualização das estatísticas dos números

			System.out.printf("=> Atualizando as estatísticas a partir dos sorteios\n");
			
			// Para cada sorteio realizado que ainda não existe base de dados
			
			for (int i = indInicio; i < sorteiosRealizadosArquivo.size(); i++) {
				
				// Para cada uns dos números da modalidade
				
				for (int numeroAtual = modalidade.getMinNumero(); numeroAtual <= modalidade.getMaxNumero(); numeroAtual++) {

					// Variáveis auxiliares para guardar as estatisticas atuais
					
					Integer qtdeRepeticoes; 
					Integer qtdeAtrasos; 

					// Obtém a estatística atual do número
					
					EstatisticasNumero estatisticasNumeroAtual = EstatisticasNumero.obterEstatisticas(modalidade, numeroAtual);
					
					// Se existe registro de estatísticas para o número
					
					if (estatisticasNumeroAtual != null) {
						// Utiliza os dados já existentes
						qtdeRepeticoes 	= estatisticasNumeroAtual.getQtdeRepeticoes();
						qtdeAtrasos 	= estatisticasNumeroAtual.getQtdeAtrasos();
					}
					else {
						// Senão atribui valor 0
						qtdeRepeticoes 	= 0;
						qtdeAtrasos 	= 0;
					}
					
					// Se o número apareceu no sorteio atual, contabiliza repetição e zera o atraso
					
					if (sorteiosRealizadosArquivo.get(i).getNumerosSorteados().contains(numeroAtual)) {
						qtdeRepeticoes++;
						qtdeAtrasos = 0;
					}
					else {
						// Senão contabiliza atraso
						qtdeAtrasos++;
					}
					
					// Se não existe o registro de estatística no banco 
					
					if (estatisticasNumeroAtual == null) {
						
						// Cria um novo registro
						
						estatisticasNumeroAtual = new EstatisticasNumero(modalidade, numeroAtual, qtdeRepeticoes, qtdeAtrasos);
						EstatisticasNumero.inserirEstatistica(estatisticasNumeroAtual);
					}
					else {
						
						// Senão Atualiza
						
						estatisticasNumeroAtual.setQtdeRepeticoes(qtdeRepeticoes);
						estatisticasNumeroAtual.setQtdeAtrasos(qtdeAtrasos);
						
						EstatisticasNumero.atualizarEstatistica(estatisticasNumeroAtual);
					}
					
				}
				
			}
			
		}
		else {
			System.out.printf("O banco de dados já está atualizado\n");
		}
		
	}
	
	public static void atualizarBancoDeDados(ProgramaParametros programaParametros) {
		
		try (
			Connection conexao = GerenciadorConexaoBD.getConexao();
			PreparedStatement stUltimoSorteio = conexao.prepareStatement(InstrucaoSQL.getInstrucao("sorteios_realizados.select.ultimo_sorteio"));
			PreparedStatement stInsertSorteioReali = conexao.prepareStatement(InstrucaoSQL.getInstrucao("sorteios_realizados.insert.novo_registro"));
		) {
			
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
			
			ResultSet rsUltimoSorteio = stUltimoSorteio.executeQuery();
			
			if (rsUltimoSorteio.next()) {
				numUltimoSorteioBanco = rsUltimoSorteio.getInt("num_concurso"); 
			}
			
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
				
				Integer ctRegInseridos = 0;
				
				for (SorteioArquivo novoSorteio : novosSorteios) {
					
					// id_modalidade
					stInsertSorteioReali.setInt(1, modalidade.getId());
					
					// num_concurso
					stInsertSorteioReali.setInt(2, novoSorteio.getNumConcurso());
					
					// data
//					String data = new SimpleDateFormat("dd/MM/yyyy").format(novoSorteio.getData());
					stInsertSorteioReali.setString(3, novoSorteio.getData());
					
					// numeros_sorteados
					stInsertSorteioReali.setString(4, novoSorteio.getNumerosSorteados().toString().replaceAll("(\\[|\\])", ""));
					
					stInsertSorteioReali.executeUpdate();
					
					System.out.printf("  => %d registros inseridos de %d\r", ctRegInseridos, novosSorteios.size());
				}
				
				System.out.printf("  => OK\n");
				
				// Atualização das estatísticas dos números

				System.out.printf("=> Atualizando as estatísticas a partir dos sorteios\n");
				
				// Para cada sorteio realizado que ainda não existe na base de dados
				
				
				
				
			}
			else {
				System.out.printf("O banco de dados já está atualizado\n");
			}
		}
    	catch (SQLException | IOException | ParseException e) {
			e.printStackTrace();
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
