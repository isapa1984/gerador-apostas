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
    
    public static List<SorteioRealizado> obterSorteiosRealizados(Modalidade modalidade) 
            throws IOException, ParseException {
        
        ArrayList<SorteioRealizado> sorteios = new ArrayList<>();
        
       
        
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
