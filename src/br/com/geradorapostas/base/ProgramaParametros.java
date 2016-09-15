/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geradorapostas.base;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 *
 * @author usuario
 */
public class ProgramaParametros {

	// Parametros para atualização das estatísticas

	private final Boolean atualizarEstatisticas;
	private final String nomeArquivoSorteios;

	// Parametros para geração de apostas

	private final Modalidade modalidade;
	private final String geradorAposta;
	private final Integer qtdeApostas;
	private final Integer qtdeNumerosAposta;

	public ProgramaParametros(Boolean atualizarEstatisticas, String nomeArquivoSorteios, Modalidade modalidade,
			String geradorAposta, Integer qtdeApostas, Integer qtdeNumerosAposta) {
		super();
		this.atualizarEstatisticas = atualizarEstatisticas;
		this.nomeArquivoSorteios = nomeArquivoSorteios;
		this.modalidade = modalidade;
		this.geradorAposta = geradorAposta;
		this.qtdeApostas = qtdeApostas;
		this.qtdeNumerosAposta = qtdeNumerosAposta;
	}

	public static ProgramaParametros obterParametros(String[] args) throws IllegalArgumentException {

		ProgramaParametros parametrosEntrada = null;
		CommandLine commandLine;
		Options options = criarCmdOptions(args);

		// Verifica se possui pelo menos um parametro

		if (args.length <= 0) {
			throw new IllegalArgumentException(
					"Nenhum parâmetro passado. Utilize a opção -h para saber os parâmetros disponíveis.");
		}

		// Obtém os parâmetros passados para o programa

		try {
			CommandLineParser parser = new DefaultParser();
			commandLine = parser.parse(options, args);

			// Mostra o help se a opção -h foi solicitada

			if (commandLine.hasOption("h")) {
				HelpFormatter helpFormatter = new HelpFormatter();
				helpFormatter.setOptionComparator(null);
				helpFormatter.printHelp("gerador-apostas", options, true);
				return null;
			}

			// Se passou uma modalidade, verifica se é válida

			List<String> opcoesModalidade = new ArrayList<>();

			List<Modalidade> modalidades = Modalidade.obterModalidades();

			for (Modalidade modalidade : modalidades) {
				opcoesModalidade.add(modalidade.getSigla());
			}

			String siglaModalidadeParam = commandLine.getOptionValue("md");

			if (!opcoesModalidade.contains(siglaModalidadeParam)) {
				throw new IllegalArgumentException("Sigla inválida. Utilize a opção -h para siglas disponíveis.");
			}

			Modalidade modalidadeSelec = Modalidade.obterModalidadePorSigla(siglaModalidadeParam);

			// Se foi solicitado a atualização das estatísticas

			String nomeArquivoSorteios = null;

			if (commandLine.hasOption("ae")) {

				if (!commandLine.hasOption("as")) {
					throw new IllegalArgumentException(
							"Arquivo com os sorteios realizados não encontrado ou fornecido.");
				}

				nomeArquivoSorteios = commandLine.getOptionValue("as", "sorteios.html");

				parametrosEntrada = new ProgramaParametros(true, nomeArquivoSorteios, null, null, null, null);
			} else {
				// Verifica as outras opções se necessário

				String geradorApostasSigla = commandLine.getOptionValue("g", "hist");

				Integer qtdeApostas = (Integer) commandLine.getParsedOptionValue("qa");
				if (qtdeApostas == 0) {
					qtdeApostas = 1;
				}

				Integer qtdeNumerosAposta = (Integer) commandLine.getParsedOptionValue("qn");
				if (qtdeNumerosAposta == 0) {
					qtdeNumerosAposta = modalidadeSelec.getQtdeMinMarcacao();
				}

				parametrosEntrada = new ProgramaParametros(false, null, modalidadeSelec, geradorApostasSigla,
						qtdeApostas, qtdeNumerosAposta);
			}
		} catch (ParseException e) {
			throw new IllegalArgumentException("Erro ao ler os parâmetros. Mensagem: " + e.getMessage());
		}

		return parametrosEntrada;
	}

	// Cria o objeto Options que contém as opções que estarão disponíveis para
	// uso

	private static Options criarCmdOptions(String[] args) {

		Options options = new Options();

		// Opção help

		Option opt = new Option("h", "Mostra este help.");
		options.addOption(opt);

		// Opção modalidade
		
		StringBuilder descricao = new StringBuilder();

		descricao.append("Sigla da modalidade de loteria que será utilizada. Siglas válidas:\n");

		for (Modalidade modalidade : Modalidade.obterModalidades()) {
			descricao.append(String.format("%-2s - %s\n", modalidade.getSigla(), modalidade.getDescricao()));
		}

		opt = Option.builder("md").hasArg().argName("sigla").desc(descricao.toString()).build();
		options.addOption(opt);

		// Opção Atualiza as estatísticas

		opt = new Option("ae", "Atualiza a base de estatísticas dos números.");
		options.addOption(opt);

		// Opção Arquivo Sorteios

		opt = Option.builder("as").hasArg().argName("nome_arquivo")
				.desc("Arquivo fornecido pela caixa com os dados dos sorteios realizados. Somente quando for atualizar estatísticas. Padrão: sorteios.html")
				.build();
		options.addOption(opt);

		// Opção Gerador

		opt = Option.builder("g").hasArg().argName("gerador").desc("Gerador a ser utilizado ao criar as apostas")
				.build();
		options.addOption(opt);

		// Opção Quantidade de Apostas

		opt = Option.builder("qa").hasArg().argName("n").desc("Quantidade de Apostas para serem geradas. Padrão: 1")
				.type(Integer.class).build();
		options.addOption(opt);

		// Opção Quantidade de Números

		opt = Option.builder("qn").hasArg().argName("n")
				.desc("Quantidade de Números para serem geradas. Padrão: Mínimo da modalidade.").type(Integer.class)
				.build();
		options.addOption(opt);

		return options;
	}
	
	private static Options criarCmdHelpOptions(String[] args) {

		Options options = new Options();

		// Opção help

		Option opt = new Option("h", "Mostra este help.");
		options.addOption(opt);

		return options;
	}
	

	private static void mostrarHelp(Options options) {
		HelpFormatter helpFormatter = new HelpFormatter();
		helpFormatter.setOptionComparator(null);
		helpFormatter.printHelp("gerador-apostas", options, true);
	}

	public Boolean getAtualizarEstatisticas() {
		return atualizarEstatisticas;
	}

	public String getNomeArquivoSorteios() {
		return nomeArquivoSorteios;
	}

	public Modalidade getModalidade() {
		return modalidade;
	}

	public String getGeradorAposta() {
		return geradorAposta;
	}

	public Integer getQtdeApostas() {
		return qtdeApostas;
	}

	public Integer getQtdeNumerosAposta() {
		return qtdeNumerosAposta;
	}

}
