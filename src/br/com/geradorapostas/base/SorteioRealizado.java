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
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
        this.numConcurso 		= numConcurso;
        this.dataRealizacao 	= dataRealizacao;
        this.numerosSorteados 	= numerosSorteados;
    }

    // Obtém os sorteios realizados para modalidade oriundos dos arquivos disponibilizados pela Caixa
    
    public static List<SorteioRealizado> obterSorteiosRealizados(Modalidade modalidade, File arquivo) 
            throws IOException, ParseException {
        
        ArrayList<SorteioRealizado> sorteios = new ArrayList<>();
        
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
        		
//        		System.out.println("-------------");
        		
        		sorteios.add(new SorteioRealizado(numConcurso, dataRealizacao, numerosSorteados));
			}

        }
        else {
            throw new FileNotFoundException(String.format("Arquivo \"%s\" não encontrado!", arquivo.getName()));
        }
        
        return sorteios;
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
    
    @Override
    public String toString() {
    	return String.format(
    			"Concurso: %d, Data: %s, Números: %s", 
    			this.numConcurso, 
    			new SimpleDateFormat("dd/MM/yyyy").format(this.dataRealizacao), 
    			this.numerosSorteados
    	);
    }
}
