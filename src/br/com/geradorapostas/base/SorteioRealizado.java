/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geradorapostas.base;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author usuario
 */

@SuppressWarnings("serial")
public class SorteioRealizado implements Serializable {
    
    private final Integer       numConcurso;
    private final String        dataRealizacao;
    private final List<Integer> numerosSorteados;

    public SorteioRealizado(Integer numConcurso, String dataRealizacao, List<Integer> numerosSorteados) {
        this.numConcurso = numConcurso;
        this.dataRealizacao = dataRealizacao;
        this.numerosSorteados = numerosSorteados;
    }

    public Integer getNumConcurso() {
        return numConcurso;
    }

    public String getDataRealizacao() {
        return dataRealizacao;
    }
    
    public List<Integer> getNumerosSorteados() {
        return numerosSorteados;
    }
    
    public static List<SorteioRealizado> obterSorteiosRealizados(String nomeArquivo) 
            throws FileNotFoundException, IOException {
        
        File arquivo = new File(nomeArquivo);
        
        ArrayList<SorteioRealizado> sorteios = new ArrayList<>();
        
        if (arquivo.exists()) {
            
            try (BufferedReader reader = Files.newBufferedReader(arquivo.toPath())) {
                String linha;
                
                while ((linha = reader.readLine()) != null) {                    
                    
                    String[] itensLinha = linha.split(" ");
                    
                    Integer numConcurso = Integer.parseInt(itensLinha[0]);
                    String dataRealizacao = itensLinha[1];
                    
                    ArrayList<Integer> numeros = new ArrayList<>();                    
                    
                    String[] strNumeros = Arrays.copyOfRange(itensLinha, 2, itensLinha.length);                    
                    
                    for (String strNumero : strNumeros) {
                        numeros.add(Integer.parseInt(strNumero));
                    }
                    
                    SorteioRealizado sorteioRealizado = new SorteioRealizado(numConcurso, dataRealizacao, numeros);
                    sorteios.add(sorteioRealizado);
                }
            } 
            catch (IOException ioe) {
                throw ioe;
            }
        }
        else {
            throw new FileNotFoundException(String.format("Arquivo \"%s\" não encontrado!", nomeArquivo));
        }
        
        return sorteios;
    }

    
    
}
