/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geradorapostas.base;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author usuario
 */
public class ProgramaParametros {
    
    private static final String NOME_ARQUIVO_SORTEIOS      = "sorteios.txt";
    private static final String NOME_ARQUIVO_APOSTAS_REC   = "apostas-geradas.txt";
    
    private final Modalidade modalidade; 
    private final Integer qtdeApostas; 
    private final Integer qtdeNumerosAposta;
    private final String nomeArquivoApostas;
    private final String nomeArquivoSorteios;

    public ProgramaParametros(Modalidade modalidade, Integer qtdeApostas, Integer qtdeNumerosAposta, String nomeArquivoApostas, String nomeArquivoSorteios) {
        this.modalidade = modalidade;
        this.qtdeApostas = qtdeApostas;
        this.qtdeNumerosAposta = qtdeNumerosAposta;
        this.nomeArquivoApostas = nomeArquivoApostas;
        this.nomeArquivoSorteios = nomeArquivoSorteios;
    }
    
    public static ProgramaParametros obterParametros(String[] args) throws IllegalArgumentException {
        
        // Verifica se possui pelo menos o parâmetro de modalidade
        
        if (args.length < 1) {
            throw new IllegalArgumentException(obterMensagemErroStr());
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
        
        ProgramaParametros parametrosEntrada = new ProgramaParametros(modalidadeSelec, qtdeApostas, qtdeNumeroApostas, nomeArquivoApostas, nomeArquivoSorteios);
        
        return parametrosEntrada;
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

    public String getNomeArquivoApostas() {
        return nomeArquivoApostas;
    }

    public String getNomeArquivoSorteios() {
        return nomeArquivoSorteios;
    }
    

    


    
    
    
    
}
