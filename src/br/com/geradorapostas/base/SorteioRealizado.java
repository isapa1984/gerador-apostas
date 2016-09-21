/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geradorapostas.base;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author usuario
 */

@SuppressWarnings("serial")
public class SorteioRealizado implements Serializable {
    
    private final Integer       numConcurso;
    private final Date        	dataRealizacao;
    private final List<Integer> numerosSorteados;

    public SorteioRealizado(Integer numConcurso, Date dataRealizacao, List<Integer> numerosSorteados) {
        this.numConcurso = numConcurso;
        this.dataRealizacao = dataRealizacao;
        this.numerosSorteados = numerosSorteados;
    }

    public Integer getNumConcurso() {
        return numConcurso;
    }

    public Date getDataRealizacao() {
        return dataRealizacao;
    }
    
    public List<Integer> getNumerosSorteados() {
        return numerosSorteados;
    }
    
    public static List<SorteioRealizado> obterSorteiosRealizados(File arquivo) 
            throws IOException {
        
        ArrayList<SorteioRealizado> sorteios = new ArrayList<>();
        
        if (arquivo.exists()) {
            
        	Document htmlDoc = Jsoup.parse(arquivo, null);
        	
        	Elements trElements = htmlDoc.select("tr:has(td)");
        	
        	for (int i = 0; i < 3; i++) {
        		Element tr = trElements.get(i);
        		System.out.println("-----------------------------------------");
        		System.out.println(tr.html());
			}
        	
//        	for (Element trElement : trElements) {
//        		System.out.println(trElement.html());
//			}

        }
        else {
            throw new FileNotFoundException(String.format("Arquivo \"%s\" não encontrado!", arquivo.getName()));
        }
        
        return sorteios;
    }

    
    
}
