/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geradorapostas.base;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import joptsimple.HelpFormatter;
import joptsimple.OptionDescriptor;
import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import joptsimple.ValueConversionException;
import joptsimple.ValueConverter;

/**
 *
 * @author usuario
 */
public class ProgramaParametros {

	// Parametros para atualização das estatísticas

	private final Boolean atualizarEstatisticas;
	private final File arquivoSorteios;

	// Parametros para geração de apostas

	private final Modalidade modalidade;
	private final String geradorAposta;
	private final Integer qtdeApostas;
	private final Integer qtdeNumerosAposta;

	public ProgramaParametros(Boolean atualizarEstatisticas, File arquivoSorteios, Modalidade modalidade,
			String geradorAposta, Integer qtdeApostas, Integer qtdeNumerosAposta) {
		this.atualizarEstatisticas = atualizarEstatisticas;
		this.arquivoSorteios = arquivoSorteios;
		this.modalidade = modalidade;
		this.geradorAposta = geradorAposta;
		this.qtdeApostas = qtdeApostas;
		this.qtdeNumerosAposta = qtdeNumerosAposta;
	}

	public static ProgramaParametros obterParametros(String[] args) throws IllegalArgumentException {
		
		ProgramaParametros parametrosEntrada = null;
		
		// Prepara a estrutura para reconhecimento das opções do programa
		
		OptionParser optionParser = new OptionParser(false);
		
		// Define a estrutura do help
		
		optionParser.formatHelpWith(new GeradorApostasHelpFormatter());
		
		// Define as opções que serão disponibilizadas 
		
		// Opção Help
		
		optionParser.accepts("h","Mostra este help").forHelp();
		
		// Opção Modalidade 
		
		String argName = "MODALIDADE";
		
		StringBuilder descricao = new StringBuilder();

		descricao.append("Sigla da modalidade de loteria que será utilizada definida em " + argName + ". Siglas válidas: ");

		descricao.append("(");
		
		for (Modalidade modalidade : Modalidade.obterModalidades()) {
			descricao.append(String.format("%s=%s ", modalidade.getSigla(), modalidade.getDescricao()));
		}
		
		descricao.append(")");
		
		OptionSpec<Modalidade> optModalidade = optionParser
				.accepts("md", descricao.toString())
				.withRequiredArg()
				.describedAs(argName)
				.withValuesConvertedBy(new ModalidadeConverter())
				.required();
		
		// Opção Atualiza as estatísticas

		argName = "ARQUIVO_SORTEIO";
		
		OptionSpec<File> optAtualizarEstats = optionParser
				.accepts("ae", "Sinaliza para atualizar o banco de estatísticas da modalidade selecionada a partir dos sorteios contidos em " + argName + ".")
				.withOptionalArg()
				.describedAs(argName)
				.ofType(File.class)
				.defaultsTo(new File("sorteios.html"));
		
		// Opção Gerador
		
		argName = "GERADOR";

		OptionSpec<String> optGerador = optionParser
				.accepts("g", "Gerador a ser utilizado ao criar as apostas definido por " + argName + ".")
				.availableUnless("ae")
				.withRequiredArg()
				.describedAs(argName)
				.ofType(String.class)
				.defaultsTo("hist");

		// Opção Quantidade de Apostas
		
		argName = "QTDE_APOSTAS";

		OptionSpec<Integer> optQtdeApostas = optionParser
				.accepts("qa", "Quantidade de Apostas para serem geradas definidos em " + argName + ".")
				.availableUnless("ae")
				.withRequiredArg()
				.describedAs(argName)
				.ofType(Integer.class)
				.defaultsTo(1);

		// Opção Quantidade de Números
		
		argName = "QTDE_NUMEROS";

		OptionSpec<Integer> optQtdeNumerosAposta = optionParser
				.accepts("qn", "Quantidade de Números por apostas definido em " + argName + ". Padrão: Mínimo da modalidade.")
				.availableUnless("ae")
				.withRequiredArg()
				.describedAs(argName)
				.ofType(Integer.class);
		
		try {
			// Realiza o reconhecimento das opções fornecidas
			
			OptionSet optionSet = optionParser.parse(args);
			
			// Imprime o help quando solicitado
			
			if (optionSet.has("h")) {
				optionParser.printHelpOn(System.out);
				return null;
			}
			
			// Obtém a modalidade

			Modalidade modalidade = optModalidade.value(optionSet);
			
			// Se for pra atualizar as estatísticas, obtém o arquivo com os sorteios realizados  
			
			if (optionSet.has(optAtualizarEstats)) {
				parametrosEntrada = new ProgramaParametros(true, optAtualizarEstats.value(optionSet), modalidade, null, null, null);
			}
			else {
				parametrosEntrada = new ProgramaParametros(false, null, modalidade, optGerador.value(optionSet), optQtdeApostas.value(optionSet), optQtdeNumerosAposta.value(optionSet));
			}
		} 
		catch (OptionException | IOException e) {
			
			StringBuilder mensagem = new StringBuilder();
			
			if (e.getCause() != null) {
				mensagem.append(e.getCause().getLocalizedMessage());
			}
			else {
				mensagem.append(e.getLocalizedMessage());
			}
			
			mensagem.append(" (Utilize a opção -h para opções disponíveis.)");
			
			throw new IllegalArgumentException(mensagem.toString());
		}
		
		return parametrosEntrada;
	}
	
	private static final class ModalidadeConverter implements ValueConverter<Modalidade> {

		@Override
		public Modalidade convert(String value) {
			Modalidade modalidade = Modalidade.obterModalidadePorSigla(value);
			
			if (modalidade == null) {
				throw new ValueConversionException("Sigla inválida para modalidade.");
			}
			
			return modalidade;
		}

		@Override
		public String valuePattern() {
			return null;
		}

		@Override
		public Class<? extends Modalidade> valueType() {
			return Modalidade.class;
		}
		
	}
	
	private static final class GeradorApostasHelpFormatter implements HelpFormatter {

		@Override
		public String format(Map<String, ? extends OptionDescriptor> options) {
			
			StringBuilder textoHelp = new StringBuilder();
			
			// Monta o texto do uso do programa
			
			textoHelp.append("Uso \n");
			textoHelp.append("  gerador-apostas ");
			
			for (Map.Entry<String, ? extends OptionDescriptor> entry : options.entrySet()) {
				
				String opcao = entry.getKey();
				OptionDescriptor optionDescriptor = entry.getValue();
				
				if (opcao.contains("arguments")) {
					continue;
				}
				
				if (!optionDescriptor.isRequired()) {
					textoHelp.append("[");
				}
				
				textoHelp.append("-");
				textoHelp.append(opcao);
				
				if (optionDescriptor.acceptsArguments()) {
					textoHelp.append(" ");
					textoHelp.append(optionDescriptor.argumentDescription());
				}
				
				if (!optionDescriptor.isRequired()) {
					textoHelp.append("]");
				}
				
				textoHelp.append(" ");
			}

			textoHelp.append("\n\n");
			
			// Monta o texto explicando cada opção
			
			for (Map.Entry<String, ? extends OptionDescriptor> entry : options.entrySet()) {
				
				String opcao = entry.getKey();
				OptionDescriptor optionDescriptor = entry.getValue();
				
				if (opcao.contains("arguments")) {
					continue;
				}
				
				textoHelp.append("-");
				textoHelp.append(opcao);
				textoHelp.append("\t");				
				textoHelp.append(optionDescriptor.description());
				
				if (!optionDescriptor.defaultValues().isEmpty()) {
					textoHelp.append(" ");				
					textoHelp.append("Padrão: ");
					textoHelp.append(optionDescriptor.defaultValues().toString());
				}

//				if (optionDescriptor.acceptsArguments()) {
//					textoHelp.append(" ");
//					textoHelp.append("Argumento: ");
//					textoHelp.append(optionDescriptor.argumentDescription());
//					
//					if (!optionDescriptor.defaultValues().isEmpty()) {
//						textoHelp.append(", ");
//						textoHelp.append("Valor Padrão: ");
//						textoHelp.append(optionDescriptor.defaultValues().toString());
//					}
//				}
				
				textoHelp.append("\n");
			}
			
			return textoHelp.toString();
		}
	}

	
	public Boolean isAtualizarEstatisticas() {
		return atualizarEstatisticas;
	}

	public File getArquivoSorteios() {
		return arquivoSorteios;
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
