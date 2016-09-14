/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geradorapostas.base;

import java.util.ArrayList;
import java.util.Arrays;
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
    
    private static final String NOME_ARQUIVO_SORTEIOS      = "sorteios.txt";
    private static final String NOME_ARQUIVO_APOSTAS_REC   = "apostas-geradas.txt";
    
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
        
		Options options = criarCmdOptions(args);
		
		try {
			CommandLineParser parser = new DefaultParser();
			CommandLine commandLine = parser.parse(options, args);
		} catch (ParseException e) {
			mostrarHelp(options);
			return null;
		}
		
        // Verifica se possui pelo menos um parametro
        
        if (args.length <= 0) {
            mostrarHelp(options);
            return null;
        }
        
        // Se passou uma modalidade, verifica se é válida
        
        List<String> opcoesModalidade = new ArrayList<>();
        
        List<Modalidade> modalidades = Modalidade.obterModalidades();
        
        for (Modalidade modalidade : modalidades) {
            opcoesModalidade.add("-" + modalidade.getSigla());
        }
        
        if (!opcoesModalidade.contains(args[0])) {
            throw new IllegalArgumentException(obterMensagemErroStr());
        }
        
        Modalidade modalidadeSelec = Modalidade.obterModalidadePorSigla(args[0].replace("-", ""));
        
        // Verifica as outras opções se necessário
        
        List<String> opcoesValidas = Arrays.asList("-qa", "-qn", "-as", "-aa");
        
        Integer qtdeApostas         = 1; 
        Integer qtdeNumeroApostas   = modalidadeSelec.getQtdeMinMarcacao();
        String nomeArquivoSorteios  = NOME_ARQUIVO_SORTEIOS;
        String nomeArquivoApostas   = NOME_ARQUIVO_APOSTAS_REC;
        
        String opcaoAtual           = "";
                
        for (int i = 1; i < args.length; i++) {
            String arg = args[i];
            
            if (opcaoAtual.isEmpty()) {
                if (opcoesValidas.contains(arg)) {
                    opcaoAtual = arg;
                }
                else {
                    throw new IllegalArgumentException(obterMensagemErroStr());
                }
            }
            else {
                if (opcaoAtual.equals("-qa")) {
                    qtdeApostas = Integer.parseInt(arg);
                }

                if (opcaoAtual.equals("-qn")) {
                    qtdeNumeroApostas = Integer.parseInt(arg);
                }

                if (opcaoAtual.equals("-as")) {
                    nomeArquivoSorteios = arg;
                }

                if (opcaoAtual.equals("-aa")) {
                    nomeArquivoApostas = arg;
                }

                opcaoAtual = "";
            }
        }
        
        
        
        ProgramaParametros parametrosEntrada = null;
        
        return parametrosEntrada;
    }
	
	private static Options criarCmdOptions(String[] args) {
		
		Options options = new Options();
		
		StringBuilder descricao = new StringBuilder();
		
		descricao.append("Sigla da modalidade de loteria que será utilizada. Siglas válidas:\n");
		
		for (Modalidade modalidade : Modalidade.obterModalidades()) {
			descricao.append(String.format("%-2s para %s\n", modalidade.getSigla(), modalidade.getDescricao()));
		}
		
		Option opt = Option.builder("md")
				.longOpt("modalidade")
				.hasArg()
				.argName("SIGLA")
				.desc(descricao.toString())
				.required()
				.build();
		options.addOption(opt);
		
		opt = new Option("ae", "atuest", false, "Atualiza a base de estatísticas dos números.");
		options.addOption(opt);
		
		opt = Option.builder("as")
					.longOpt("arq-sorteios")
					.hasArg()
					.argName("N")
					.desc("Arquivo fornecido pela caixa com os dados dos sorteios realizados.\n Somente quando for atualizar estatísticas.")
					.build();
		options.addOption(opt);
		
		opt = Option.builder("g")
				.longOpt("gerador")
				.hasArg()
				.argName("GERADOR")
				.desc("Gerador a ser utilizado ao criar as apostas")
				.build();
		options.addOption(opt);
		
		
		opt = Option.builder("qa")
				.longOpt("qtde-apostas")
				.hasArg()
				.argName("N")
				.desc("Quantidade de Apostas para serem geradas. Padrão: 1")
				.build();
		options.addOption(opt);
		
		opt = Option.builder("qn")
				.longOpt("qtde-num")
				.hasArg()
				.argName("N")
				.desc("Quantidade de Números para serem geradas. Padrão: Mínimo da modalidade.")
				.build();
		options.addOption(opt);
		
		return options;
	}
	
	private static void mostrarHelp(Options options) {
        HelpFormatter helpFormatter = new HelpFormatter();
        helpFormatter.setOptionComparator(null);
        helpFormatter.printHelp("gerador-apostas", options, true);
	}
	
	
    private static String obterMensagemErroStr() {
        StringBuilder mensagemErro = new StringBuilder();
        
        mensagemErro.append("Os parâmetros passados estão inválidos. Parâmetros esperados: modalidade [opções]\n");
        mensagemErro.append("Para modalidade use uma das seguintes opções:\n");
        
        for (Modalidade modalidade : Modalidade.obterModalidades()) {
            mensagemErro.append(String.format(" -%-2s para %s\n", modalidade.getSigla(), modalidade.getDescricao()));
        }
        
        mensagemErro.append("Para [opções] use uma ou mais de uma das seguintes opções: \n");
        mensagemErro.append(" -qa <qtdeApostas>: Define a quantidade de apostas. Padrão: 1\n");
        mensagemErro.append(" -qn <qtdeNumeros>: Define a quantidade de numeros na aposta. Padrão: Valor Mínimo da Modalidade\n");
        mensagemErro.append(" -as <arquivoSorteios>: Define o arquivo contendo os Sorteios Realizados. Padrão: sorteios.txt\n");
        mensagemErro.append(" -aa <arquivoApostas>: Define o arquivo contendo as Apostas Geradas. Padrão: apostas-geradas.txt\n");
        
        return mensagemErro.toString();
    }

    public Modalidade getModalidade() {
        return modalidade;
    }

    public Integer getQtdeApostas() {
        return qtdeApostas;
    }

    public Integer getQtdeNumerosAposta() {
        return qtdeNumerosAposta;
    }

}
